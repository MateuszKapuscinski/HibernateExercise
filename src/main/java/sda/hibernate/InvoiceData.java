package sda.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity //encja
public class InvoiceData {

    @Id //klucz głowny
    private String nip;

    public InvoiceData(String nip) {
        this.nip = nip;
    }

    public InvoiceData() {
    }

    @Override
    public String toString() {
        return "InvoiceData{" +
                "nip='" + nip + '\'' +
                '}';
    }
}
