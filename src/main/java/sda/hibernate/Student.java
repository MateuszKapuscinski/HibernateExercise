package sda.hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


import static sda.hibernate.Student.FIND_STUDENT_BY_FIRST_NAME;
import static sda.hibernate.Student.FIND_STUDENT_BY_ID;

@Entity
@Table(name = "tab_student")
//@EntityListeners() - służy do tego samego co adnotacje takie jak PrePersist
@NamedQueries(value = {@NamedQuery(name = FIND_STUDENT_BY_FIRST_NAME,query = "SELECT s FROM Student s WHERE s.firstName = :firstName "),
@NamedQuery(name = FIND_STUDENT_BY_ID,query = "SELECT s FROM Student s WHERE s.id = :studentId")})
public class Student {
    public static class StudentBuilder {

        private String firstName;
        private String lastName;
        private InvoiceData invoiceData;
        private List<Grade> grades;
        private Address address;

        private StudentBuilder() {

        }

        public StudentBuilder setAddress(Address address) {
            this.address = address;
            return this;
        }

        public StudentBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public StudentBuilder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public StudentBuilder setInvoiceData(InvoiceData invoiceData) {
            this.invoiceData = invoiceData;
            return this;
        }

        public StudentBuilder setGrades(List<Grade> grades) {
            this.grades = grades;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }

    public static final String FIND_STUDENT_BY_FIRST_NAME = "Student.findByFirstName";
    public static final String FIND_STUDENT_BY_ID = "Student.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, name = "name", length = 40)
    private String firstName;
    private String lastName;
    private LocalDateTime createTime;
    private LocalDateTime lastModifiedTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idinvoicedata")
    private InvoiceData invoiceData;

    @ManyToMany(mappedBy = "students")
    private List<Academy> academies;


    @OneToMany(mappedBy = "student",
            orphanRemoval = true) //steruje usunieciem z bazy danych wraz z usunieciem z listy
    private List<Grade> grades;


    @Version //optimistic locking, przy 2 update zabezpiecza
    //powoduje: https://docs.oracle.com/javaee/7/api/javax/persistence/OptimisticLockException.html
    private Integer version;

    @PrePersist
    void perPersist() {
        System.out.println("[Student.class] Before persist");
        createTime = LocalDateTime.now();
    }

    @PostLoad
    void postLoad() {
        System.out.println("[Student.class] Post load");
    }

    @PreUpdate
    void preUpdate() {
        lastModifiedTime = LocalDateTime.now();
        System.out.println("[Student.class] PreUpdate update");
    }

    @PostUpdate
    void postUpdate() {
        System.out.println("[Student.class] Post update");
    }

    @Embedded
    private Address address;

    Student() {
    }

    public Student(String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.invoiceData = null;
    }

    public Student(String firstName, String lastName, Address address, InvoiceData invoiceData) {
        this(firstName, lastName, address);
        this.invoiceData = invoiceData;
    }

    private Student(StudentBuilder studentBuilder) {
        this.firstName = studentBuilder.firstName;
        this.lastName = studentBuilder.lastName;
        this.invoiceData = studentBuilder.invoiceData;
        this.grades = studentBuilder.grades;
        this.address = studentBuilder.address;
    }

    public static StudentBuilder builder() {
        return new StudentBuilder();
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
                ", createTime=" + createTime +
                ", lastModifiedTime=" + lastModifiedTime +
                //  ", invoiceData=" + invoiceData  uwazajmy na encje w relacjach!
                ", version=" + version +
                ", address=" + address +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public InvoiceData getInvoiceData() {
        return invoiceData;
    }

    public List<Grade> getGrades() {
        return grades;
    }
}
