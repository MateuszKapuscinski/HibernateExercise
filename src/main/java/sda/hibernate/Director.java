package sda.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(NamePk.class)
public class Director {

    @Id
    private String firstName;

    @Id
    private String lastName;

}
