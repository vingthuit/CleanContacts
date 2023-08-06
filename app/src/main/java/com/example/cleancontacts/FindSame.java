package com.example.cleancontacts;

import static com.example.cleancontacts.contacts.ContactManager.getContact;
import static com.example.cleancontacts.contacts.ContactManager.getContactList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cleancontacts.contacts.Contact;

import java.util.ArrayList;

public class FindSame extends AppCompatActivity {
    private ArrayList<Contact> contacts;
    private ArrayList<Contact> nextContacts;
    private ArrayList<String> stringContacts;
    private ArrayAdapter<String> adapter;
    private ListView contactList;
    private final StringComparator comparator = new LevenshteinDistance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same);

        contacts = getContactList();
        contactList = findViewById(R.id.contact_list);
        nextContacts = new ArrayList<>();
        stringContacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);

        compareContacts();

        modify();
    }

    public void modify() {
        contactList.setOnItemClickListener((parent, view, position, id) -> {
            Contact contact = nextContacts.get(position);
            if (contact != null) {
                openStandardApp(contact.getId());

                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.getId()));
                getContact(String.valueOf(id));
            }
        });
    }

    private void openStandardApp(String id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));
        intent.setData(uri);
        startActivity(intent);
    }

    private void addContact(Contact contact){
        nextContacts.add(contact);
        stringContacts.add(contact.toString());
    }

    private void compareContacts() {
        for (int i = 0; i < contacts.size() - 1; i++) {
            Contact first = contacts.get(i);
            Contact next = contacts.get(i + 1);
            int distance = comparator.calculateDistance(first.getName(), next.getName());
            if (distance < 3) {
                addContact(first);
                int j = i + 1;
                while (distance < 3) {
                    addContact(next);

                    j++;
                    next = contacts.get(j);
                    distance = comparator.calculateDistance(first.getName(), next.getName());
                }
                break;
            }
        }
        contactList.setAdapter(adapter);
    }

    private void clearAll(){
        adapter.clear();
        nextContacts.forEach(v -> contacts.remove(v));
        nextContacts.clear();
        stringContacts.clear();
    }

    public void onNext(View view) {
        clearAll();
        compareContacts();
    }
}