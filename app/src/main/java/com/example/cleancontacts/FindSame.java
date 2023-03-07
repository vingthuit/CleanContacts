package com.example.cleancontacts;

import static com.example.cleancontacts.contacts.ContactManager.areSameNames;
import static com.example.cleancontacts.contacts.ContactManager.deleteContact;
import static com.example.cleancontacts.contacts.ContactManager.getContactList;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cleancontacts.contacts.Contact;

import java.util.ArrayList;

public class FindSame extends AppCompatActivity {
    private ArrayList<Contact> contacts;
    private ArrayList<String> stringContacts;
    private ArrayAdapter<String> adapter;
    private ListView contactList;
    private Contact contact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same);

        contacts = getContactList();
        contactList = findViewById(R.id.contact_list);
        stringContacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);

        getStringContacts();
        contactList.setAdapter(adapter);
    }

    public void onDelete(View view) {
        deleteContact(contact.getLookupKey());
        onNext(view);
    }

    private void getStringContacts() {
        Contact prev = new Contact();
        for (int i = 0; i < contacts.size(); i++) {
            Contact next = contacts.get(i);
            if (areSameNames(prev.getName(), next.getName())) {
                stringContacts.add(prev.toString());
                stringContacts.add(next.toString());
                contact = next;
                return;
            }
            prev = next;
        }
    }

    public void onNext(View view) {
        adapter.clear();
        stringContacts.clear();
        contacts.remove(contact);

        getStringContacts();
        contactList.setAdapter(adapter);
    }
}