package com.example.cleancontacts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class FindSame extends AppCompatActivity {
    ArrayList<String> stringContacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_same);
        Bundle bundle = this.getIntent().getExtras();
        stringContacts = bundle.getStringArrayList("list");

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ListView contactList = findViewById(R.id.contactList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringContacts);
        contactList.setAdapter(adapter);
    }

    public void onFind(View view){
        Bundle bundle = new Bundle();
        stringContacts.remove(0);
        bundle.putStringArrayList("list", stringContacts);
        Intent intent = new Intent(this, FindSame.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}