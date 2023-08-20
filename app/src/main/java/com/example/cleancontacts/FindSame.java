package com.example.cleancontacts;

import static com.example.cleancontacts.contacts.ContactManager.getContactById;
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

    private boolean paused;
    private String currId;
    private int currPos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same);

        contacts = getContactList();
        contactList = findViewById(R.id.contact_list);
        nextContacts = new ArrayList<>();
        stringContacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);

        getSameContacts();
        modify();
    }

    public void modify() {
        contactList.setOnItemClickListener((parent, view, position, id) -> {
            Contact contact = nextContacts.get(position);
            if (contact != null) {
                currPos = position;
                currId = contact.getId();
                openStandardApp(contact.getId());
            }
        });
    }

    protected void onPause() {
        super.onPause();
        paused = true;
    }

    protected void onResume() {
        super.onResume();
        if (paused) {
            paused = false;
            Contact contact = getContactById(String.valueOf(currId));
            stringContacts.set(currPos, contact.toString());
            contactList.setAdapter(adapter);
        }
    }

    private void openStandardApp(String id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));
        intent.setData(uri);
        startActivity(intent);
    }

    private void addContact(Contact contact) {
        nextContacts.add(contact);
        stringContacts.add(contact.toString());
    }

    private void getSameContacts() {
        for (int i = 0; i < contacts.size() - 1; i++) {
            Contact first = contacts.get(i);
            int j = i + 1;
            if (compareContacts(first, contacts.get(j))) {
                addContact(first);
                while (compareContacts(first, contacts.get(j))) {
                    addContact(contacts.get(j));
                    j++;
                }
                break;
            }
        }
        contactList.setAdapter(adapter);
    }

    private boolean compareContacts(Contact first, Contact next){
        return first.hasSameName(next.getName()) || first.hasSamePhone(next);
    }

    private void clearAll() {
        adapter.clear();
        nextContacts.forEach(v -> contacts.remove(v));
        nextContacts.clear();
        stringContacts.clear();
    }

    public void onNext(View view) {
        clearAll();
        getSameContacts();
    }

    public void onBack(View view) {
        return;
    }
}