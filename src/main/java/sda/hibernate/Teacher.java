package sda.hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Teacher {
    @EmbeddedId
    private NamePk name;
}
