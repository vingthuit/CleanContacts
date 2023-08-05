package com.example.cleancontacts;

import static com.example.cleancontacts.contacts.ContactManager.areSameNames;
import static com.example.cleancontacts.contacts.ContactManager.deleteContact;
import static com.example.cleancontacts.contacts.ContactManager.getContactList;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cleancontacts.contacts.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindSame extends AppCompatActivity {
    private ArrayList<Contact> contacts;
    private Map<Integer, Contact> twoContacts;
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
        twoContacts = new HashMap<>();
        stringContacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);

        getStringContacts();
        contactList.setAdapter(adapter);
        delete();
    }

    public void delete(){
        contactList.setOnItemClickListener((parent, view, position, id) -> {
            Contact c = twoContacts.get(position);
            deleteContact(c.getLookupKey());
            onNext(view);
        });
    }

    public void onDelete(View view) {
        //delete second contact
        deleteContact(contact.getLookupKey());
        onNext(view);
    }

    private void getStringContacts() {
        Contact prev = new Contact();
        for (int i = 0; i < contacts.size(); i++) {
            Contact next = contacts.get(i);
            if (areSameNames(prev.getName(), next.getName())) {
                twoContacts.put(0, prev);
                twoContacts.put(1, next);

                //need to rewrite
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