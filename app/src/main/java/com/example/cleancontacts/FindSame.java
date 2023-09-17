package com.example.cleancontacts;

import static com.example.cleancontacts.contacts.ContactManager.deleteContact;
import static com.example.cleancontacts.contacts.ContactManager.getContactList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cleancontacts.contacts.Contact;

import java.util.ArrayList;
import java.util.Objects;

public class FindSame extends AppCompatActivity {
    private ArrayList<Contact> contacts;
    private ArrayList<Contact> nextContacts;
    private ArrayList<String> stringContacts;
    private ArrayAdapter<String> adapter;
    private ListView contactList;
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
            clickOnContact(position);
        });
    }

    private void clickOnContact(int position) {
        Contact contact = nextContacts.get(position);
        if (contact != null) {
            currPos = position;
            openDialog(contact);
        }
    }

    private void openDialog(Contact contact) {
        AlertDialog contactDialog = new AlertDialog.Builder(
                FindSame.this).setMessage("open in standard app or delete?")
                .setNegativeButton("open", (dialog, which) -> {
                    openStandardApp(contact.getId());
                })
                .setPositiveButton("delete", (dialog, which) -> {
                    deleteContact(contact.getLookupKey());
                    deleteContactFromList();
                })
                .create();
        contactDialog.show();
        ((TextView) Objects.requireNonNull(contactDialog.findViewById(android.R.id.message)))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void deleteFromList(){
        nextContacts.remove(currPos);
        stringContacts.remove(currPos);
    }

    private void deleteContactFromList() {
        deleteFromList();
        contactList.setAdapter(adapter);
    }

    private void addToList(Contact contact) {
        nextContacts.add(contact);
        stringContacts.add(contact.toString());
    }

    private void openStandardApp(String id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));
        intent.setData(uri);
        startActivity(intent);
    }

    private void getSameContacts() {
        for (int i = 0; i < contacts.size() - 1; i++) {
            Contact first = contacts.get(i);
            int j = i + 1;
            if (compareContacts(first, contacts.get(j))) {
                addToList(first);
                while (compareContacts(first, contacts.get(j))) {
                    addToList(contacts.get(j));
                    j++;
                }
                break;
            }
        }
        contactList.setAdapter(adapter);
    }

    private boolean compareContacts(Contact first, Contact next) {
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