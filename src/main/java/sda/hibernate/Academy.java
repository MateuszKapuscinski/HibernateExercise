package sda.hibernate;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Academy {
    @Id
    private String name;

    @ManyToMany()
    @JoinTable(name = "academy_student", joinColumns = @JoinColumn(name = "academyname"), inverseJoinColumns = @JoinColumn(name = "idstudent"))
    //join table opcjonalne, hibernate ma swoje defaulty
    private Set<Student> students;
    // many to many modelowane jako set zamiast listy w celu lepszego funkcjonowanie usuwania

    Academy() {
    }

    public Academy(String name, Set<Student> students) {
        this.name = name;
        this.students = students;
    }

    @Override
    public String toString() {
        return "Academy{" +
                "name='" + name + '\'' +
                '}';
    }

    public Set<Student> getStudents() {
        return students;
    }
}
