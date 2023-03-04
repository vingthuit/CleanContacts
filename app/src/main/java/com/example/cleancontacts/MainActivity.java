package com.example.cleancontacts;

import static com.example.cleancontacts.contacts.ContactManager.compareNames;
import static com.example.cleancontacts.contacts.ContactManager.getContactList;
import static com.example.cleancontacts.contacts.ContactManager.setContentResolver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cleancontacts.contacts.Contact;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;

    ArrayList<String> stringContacts = new ArrayList<>();
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getContactsWithPermission();

        button = findViewById(R.id.button);
    }

    private void getContactsWithPermission() {
        // получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true;
        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true;
            }
        }
        if (READ_CONTACTS_GRANTED) {
            loadContacts();
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    private void loadContacts() {
        stringContacts.clear();
        setContentResolver(getContentResolver());
        getStringContacts(getContactList());
        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);
        ListView contactList = findViewById(R.id.contactList);
        // устанавливаем для списка адаптер
        contactList.setAdapter(adapter);
    }

    private void getStringContacts(ArrayList<Contact> contacts) {
        Contact prev = contacts.get(0);
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

    public void onFind(View view){

    }
}