package com.example.cleancontacts;

import static com.example.cleancontacts.contacts.ContactManager.compareNames;
import static com.example.cleancontacts.contacts.ContactManager.getContactList;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same);

        contacts = getContactList();
        contactList = findViewById(R.id.contactList);
        stringContacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);
        drawContacts();
    }

    private void drawContacts() {
        adapter.clear();
        stringContacts.clear();
        getStringContacts();
        adapter.addAll(stringContacts);
        contactList.setAdapter(adapter);
    }

    public void onFind(View view) {
        contacts.remove(0);
        drawContacts();
    }

    private void getStringContacts() {
        Contact prev = contacts.get(0);
        stringContacts.add(prev.toString());
        for (int i = 1; i < contacts.size(); i++) {
            Contact next = contacts.get(i);
            String contact = next.toString();
            if (compareNames(prev.getName(), next.getName())) {
                contact += "\n\nDELETE";
            }
            prev = next;
            stringContacts.add(contact);
        }
    }
}