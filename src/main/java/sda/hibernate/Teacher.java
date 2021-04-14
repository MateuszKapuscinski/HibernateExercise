package sda.hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Teacher {
    @EmbeddedId
    private NamePk name;

    public Teacher(NamePk name) {
        this.name = name;
    }

    Teacher() {
    }
}
