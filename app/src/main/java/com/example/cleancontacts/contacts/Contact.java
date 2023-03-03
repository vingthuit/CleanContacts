package com.example.cleancontacts.contacts;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Contact {
    private String id;
    private String name;
    private List<ContactDetail> phones;
    private List<ContactDetail> addresses;

    public Contact(String id, String name, List<ContactDetail> phones, List<ContactDetail> addresses) {
        this.id = id;
        this.name = name;
        this.phones = phones;
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

    public List<ContactDetail> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ContactDetail> addresses) {
        this.addresses = addresses;
    }

    @NonNull
    @Override
    public String toString() {
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
        return a.getName().compareToIgnoreCase(b.getName());
    }
}
