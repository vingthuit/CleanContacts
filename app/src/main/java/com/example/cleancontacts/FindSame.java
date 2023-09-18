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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FindSame extends AppCompatActivity {
    private List<Contact> contacts;
    private List<List<Contact>> sameContacts;
    private List<Contact> nextContacts;
    private List<String> stringContacts;
    private ArrayAdapter<String> adapter;
    private ListView contactList;
    private int clickedContact;
    private int currSamePos = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same);

        contacts = getContactList();
        sameContacts = new ArrayList<>();
        contactList = findViewById(R.id.contact_list);
        nextContacts = new ArrayList<>();
        stringContacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);

        getSameContacts();
        contactList.setOnItemClickListener((parent, view, position, id) -> clickOnContact(position));
    }

    private void clickOnContact(int position) {
        Contact contact = nextContacts.get(position);
        if (contact != null) {
            clickedContact = position;
            openDialog(contact);
        }
    }

    private void openDialog(Contact contact) {
        AlertDialog contactDialog = new AlertDialog.Builder(
                FindSame.this).setMessage("open in standard app or delete?")
                .setNegativeButton("open", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.getId()));
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setPositiveButton("delete", (dialog, which) -> {
                    deleteContact(contact.getLookupKey());
                    nextContacts.remove(clickedContact);
                    stringContacts.remove(clickedContact);
                    contactList.setAdapter(adapter);
                })
                .create();
        contactDialog.show();
        ((TextView) Objects.requireNonNull(contactDialog.findViewById(android.R.id.message)))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addToList(List<Contact> contacts) {
        contacts.forEach(c -> {
            nextContacts.add(c);
            stringContacts.add(c.toString());
        });
        contactList.setAdapter(adapter);
    }

    private void getSameContacts() {
        for (int i = 0; i < contacts.size() - 1; i++) {
            Contact contact = contacts.get(i);
            List<Contact> list = contacts.stream().filter(c ->
                    c.hasSameName(contact.getName()) || c.hasSamePhone(contact)).collect(Collectors.toList());
            if (list.size() > 1) {
                while (i != -1) { // remove prev contacts - they haven't duplicates
                    contacts.remove(0);
                    i--;
                }
                list.forEach(c -> contacts.remove(c)); // remove prev duplicates
                sameContacts.add(list);
                currSamePos++;
                addToList(list);
                return;
            }
        }
    }

    private void clearAll() {
        adapter.clear();
        nextContacts.clear();
        stringContacts.clear();
    }

    public void onNext(View view) {
        if (currSamePos < sameContacts.size() - 1) {
            currSamePos++;
            getSame();
            return;
        }
        clearAll();
        getSameContacts();
    }

    public void onBack(View view) {
        if (currSamePos == 0) {
            finish();
            return;
        }
        currSamePos--;
        getSame();
    }

    private void getSame() {
        System.out.println(currSamePos);
        clearAll();
        addToList(sameContacts.get(currSamePos));
    }
}