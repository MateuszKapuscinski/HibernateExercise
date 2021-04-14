package sda.hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static sda.hibernate.Student.FIND_STUDENT_BY_FIRST_ID;
import static sda.hibernate.Student.FIND_STUDENT_BY_FIRST_NAME;

@Entity
@Table(name = "tab_student")
//@EntityListeners() - służy do tego samego co adnotacje takie jak PrePersist
@NamedQueries(value = {@NamedQuery(name = FIND_STUDENT_BY_FIRST_NAME,query = "SELECT s FROM Student s WHERE s.firstName = :firstName "),
@NamedQuery(name = FIND_STUDENT_BY_FIRST_ID,query = "SELECT s FROM Student s WHERE s.id = :studentId")})
public class Student {

    public static final String FIND_STUDENT_BY_FIRST_NAME = "Student.findByFirstName";
    public static final String FIND_STUDENT_BY_FIRST_ID = "Student.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, name = "name",  length = 40)
    private String firstName;
    private String lastName;
    private LocalDateTime createTime;
    private LocalDateTime lastModifiedTime;

    @PrePersist
    void perPersist(){ // zaraz przed zapisem się odpali
        System.out.println("Before persist");
        createTime = LocalDateTime.now();
    }

    @PostLoad
    void postLoad() {
        System.out.println("[Student.class] Post load");
    }

    @PreUpdate
    void preUpdate(){
        lastModifiedTime = LocalDateTime.now();
        System.out.println("[Student.class] PreUpdate update");
    }

    @Embedded
    private Address address;

    Student() {
    }

    public Student(String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    /*
    Hibernate nie wymaga getterow i setterow
     */

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                '}';
    }

    public Integer getId() {
        return id;
    }
}
