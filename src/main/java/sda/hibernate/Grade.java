package sda.hibernate;

import javax.persistence.*;

@Entity
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "idstudent")
    private Student student;

    private int value;

    Grade() {
    }

    public Grade(int value, Student student) {
        this.value = value;
        this.student = student;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
