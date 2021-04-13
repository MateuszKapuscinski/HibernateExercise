package sda.hibernate;

import javax.persistence.*;

@Entity
@Table(name = "tab_student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, name = "name",  length = 40)
    private String firstName;
    private String lastName;
    @Embedded
    private Address address;
    /*
    Hibernate nie wymaga getterow i setterow
     */
    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
