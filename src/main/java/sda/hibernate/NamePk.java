package sda.hibernate;

import java.io.Serializable;
import java.util.Objects;

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
}
