package sda.hibernate;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
//Serialiable konieczne  w klasach pod klucze złożone
public class NamePk implements Serializable {
    private String firstName;
    private String lastName;
    /*
    equals i hashCode konieczne w klasach ktore maja być kluczami złożonymi
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamePk namePk = (NamePk) o;
        return firstName.equals(namePk.firstName) && lastName.equals(namePk.lastName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    public NamePk(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    NamePk() {
    }

    @Override
    public String toString() {
        return "NamePk{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
