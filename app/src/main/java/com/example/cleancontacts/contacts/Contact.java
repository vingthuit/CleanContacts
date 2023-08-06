package com.example.cleancontacts.contacts;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Contact {
    private String id;
    private String name;
    private String account;
    private Set<ContactDetail> phones;
    private Set<ContactDetail> addresses;

    public Contact(String id, String name, String account, Set<ContactDetail> phones, Set<ContactDetail> addresses) {
        this.id = id;
        this.name = name;
        this.account = account;
        this.phones = phones;
        this.addresses = addresses;
    }

    public Contact() {
        this.name = "emptyContact";
        this.account = "null";
        this.phones = new HashSet<>();
        this.addresses = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Set<ContactDetail> getPhones() {
        return phones;
    }

    public void setPhones(Set<ContactDetail> phones) {
        this.phones = phones;
    }

    public Set<ContactDetail> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<ContactDetail> addresses) {
        this.addresses = addresses;
    }

    public boolean hasSamePhone(Contact contact) {
        for (ContactDetail phone : phones) {
            if (contact.getPhones().contains(phone)) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append("\naccount: ").append(account);
        for (ContactDetail phone : phones) {
            sb.append("\nphone: ").append(phone.getDetail());
        }
        for (ContactDetail address : addresses) {
            sb.append("\naddress: ").append(address.getDetail());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return id.equals(contact.id) && name.equals(contact.name) &&
                account.equals(contact.account) && phones.equals(contact.phones) &&
                addresses.equals(contact.addresses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, account, phones, addresses);
    }
}

class ContactComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact a, Contact b) {
        return a.getName().compareToIgnoreCase(b.getName());
    }
}
