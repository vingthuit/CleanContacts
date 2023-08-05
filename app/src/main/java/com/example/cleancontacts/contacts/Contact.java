package com.example.cleancontacts.contacts;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Contact {
    private String lookupKey;
    private String name;
    private String account;
    private List<ContactDetail> phones;
    private List<ContactDetail> addresses;

    public Contact(String lookupKey, String name,  String account, List<ContactDetail> phones, List<ContactDetail> addresses) {
        this.lookupKey = lookupKey;
        this.name = name;
        this.account = account;
        this.phones = phones;
        this.addresses = addresses;
    }

    public Contact() {
        this.lookupKey = "-1";
        this.name = "emptyContact";
        this.account = "null";
        this.phones = new ArrayList<>();
        this.addresses = new ArrayList<>();
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
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
        sb.append("\naccount: ").append(account);
        for (int i = 0; i < phones.size(); i++) {
            sb.append("\nphone: ").append(phones.get(i).getDetail());
        }
        for (int i = 0; i < addresses.size(); i++) {
            sb.append("\naddress: ").append(addresses.get(i).getDetail());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return lookupKey.equals(contact.lookupKey) && name.equals(contact.name) && account.equals(contact.account) && phones.equals(contact.phones) && addresses.equals(contact.addresses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lookupKey, name, account, phones, addresses);
    }
}

class ContactComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact a, Contact b) {
        return a.getName().compareToIgnoreCase(b.getName());
    }
}
