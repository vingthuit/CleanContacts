package com.example.cleancontacts;

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
import java.util.List;
import java.util.Set;

public class ContactManager {
    private static ContentResolver contentResolver;

    static void setContentResolver(ContentResolver contentResolver) {
        ContactManager.contentResolver = contentResolver;
    }

    @SuppressLint("Range")
    static ArrayList<Contact> getContactList() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null)) {
            while (cursor.moveToNext()) {
                // получаем каждый контакт
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (name == null) {
                    name = "empty";
                }
                List<ContactDetail> phones = new ArrayList<>();
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    getPhones(id, phones);
                }
                List<ContactDetail> addresses = getAddresses(id);
                contacts.add(new Contact(id, name, phones, addresses));
            }
            contacts.sort(new ContactComparator());
        }
        return contacts;
    }

    static boolean compareNames(String first, String second) {
        first = remove(first);
        second = remove(second);
        return first.equals(second) || first.contains(second) || second.contains(first);
    }

    static String remove(String name) {
        int startIndex = name.indexOf("/");
        if (startIndex == -1) {
            return name;
        }
        String toBeReplaced = name.substring(startIndex);
        return name.replace(toBeReplaced, "");
    }

    private void deleteContact(Cursor cursor) {
        try {
            //make field lookupKey or uri in Contact
            @SuppressLint("Range") String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);

            //contentResolver.delete(uri, null, null);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    @SuppressLint("Range")
    private static void getPhones(String id, List<ContactDetail> phones) {
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
    private static List<ContactDetail> getAddresses(String id) {
        List<ContactDetail> addresses = new ArrayList<>();
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
