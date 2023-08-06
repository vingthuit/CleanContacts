package com.example.cleancontacts.contacts;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContactManager {
    private static ContentResolver contentResolver;
    private static ArrayList<Contact> contacts;

    public static ArrayList<Contact> getContactList() {
        return contacts;
    }

    @SuppressLint("Range")
    public static void setContactList(ContentResolver contentResolver) {
        ContactManager.contentResolver = contentResolver;
        contacts = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null)) {
            while (cursor.moveToNext()) {
                Contact contact = getContactFromCursor(cursor);
                contacts.add(contact);
            }
            contacts.sort(new ContactComparator());
        }
    }

    @SuppressLint("Range")
    private static Contact getContactFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        StringBuilder account = new StringBuilder(cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)));
        String accountType = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
        account.append(" (").append(accountType).append(')');

        Set<ContactDetail> phones = new HashSet<>();
        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
            getPhones(id, phones);
        }

        Set<ContactDetail> addresses = getAddresses(id);

        return new Contact(id, name, account.toString(), phones, addresses);
    }

    @SuppressLint("Range")
    public static Contact getContactById(String id) {
        Contact contact = new Contact();
        String selection = ContactsContract.Contacts._ID + " =" + id;
        try (Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, selection, null, null)) {
            if (cursor.moveToFirst()) {
                contact = getContactFromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contact;
    }

    public static void deleteContact(String lookupKey) {
        try {
            //make field lookupKey or uri in Contact
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            contentResolver.delete(uri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    private static void getPhones(String id, Set<ContactDetail> phones) {
        try (Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null)) {
            while (cursor.moveToNext()) {
                int phoneType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phones.add(new ContactDetail(phoneType, getFormattedPhone(phone)));
            }
        }
    }

    @SuppressLint("Range")
    private static Set<ContactDetail> getAddresses(String id) {
        Set<ContactDetail> addresses = new HashSet<>();
        try (Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = "
                        + id, null, null)) {
            while (cursor.moveToNext()) {
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                String address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                addresses.add(new ContactDetail(type, address));
            }
        }
        return addresses;
    }

    private static String getFormattedPhone(String phone) {
        phone = phone.replace("+7", "8");
        return phone.replaceAll("[()\\- ]", "");
    }

    private String checkPhones(String nums) {
        nums = nums.replaceFirst("\n", "");
        String[] phones = nums.split("\n");
        Set<String> set = new HashSet<>(Arrays.asList(phones));
        if (set.size() != phones.length) {
            nums += "\nsame";
        }
        return "\n" + nums;
    }

    //make in new thread
    private void insertContact(String name) {
        ArrayList<ContentProviderOperation> op = new ArrayList<>();
        /* Добавляем пустой контакт */
        op.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        /* Добавляем данные имени */
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        /* Добавляем данные телефона */
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "11-22-32")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "111111")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, op);
        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
        }
    }
}
