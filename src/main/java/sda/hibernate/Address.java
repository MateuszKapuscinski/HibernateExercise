package sda.hibernate;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Address {

    private String city;

    private String street;

    Address() {
    }



    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}
