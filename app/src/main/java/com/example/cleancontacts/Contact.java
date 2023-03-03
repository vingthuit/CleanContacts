package com.example.cleancontacts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Contact {
    private String name = "a";
    private List<ContactDetail> phones = new ArrayList<>();
    private List<ContactDetail> addresses = new ArrayList<>();

    public Contact(String name, List<ContactDetail> phones, List<ContactDetail> addresses) {
        this.name = name;
        this.phones = phones;
        this.addresses = addresses;
    }

    public List<ContactDetail> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ContactDetail> addresses) {
        this.addresses = addresses;
    }

    public Contact() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ContactDetail> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactDetail> phones) {
        this.phones = phones;
    }

    @Override
    public String toString() {
        if (phones == null) {
            phones = new ArrayList<>();
        }
        if (addresses == null) {
            addresses = new ArrayList<>();
        }


        StringBuilder sb = new StringBuilder(name);
        for (int i = 0; i < phones.size(); i++) {
            sb.append('\n').append(phones.get(i).getDetail());
        }
        for (int i = 0; i < addresses.size(); i++) {
            sb.append('\n').append(addresses.get(i).getDetail());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(name, contact.name) && Objects.equals(phones, contact.phones) && Objects.equals(addresses, contact.addresses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phones, addresses);
    }
}

class ContactComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact a, Contact b) {
        if (a.getName() == null) {
            a.setName("a");
        }
        if (b.getName() == null) {
            b.setName("b");
        }


        return a.getName().compareToIgnoreCase(b.getName());
    }
}
