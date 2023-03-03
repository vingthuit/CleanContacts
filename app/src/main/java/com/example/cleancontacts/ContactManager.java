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

    static ArrayList<Contact> getContactList() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // получаем каждый контакт
                    @SuppressLint("Range") String name = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    List<ContactDetail> phones = getNums(cursor);

                   /* if (name.equals("Ксюня")) {
                        getAddress(cursor);
                    }*/
                    contacts.add(new Contact(name, phones, null));
                }
                contacts.sort(new ContactComparator());
            }
        }
        return contacts;
    }

    static boolean compareNames(Contact a, Contact b) {
        String aName = remove(a.getName());
        String bName = remove(b.getName());
        return aName.equals(bName) || aName.contains(bName) || bName.contains(aName);
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

            insertPhone(uri);
            //contentResolver.delete(uri, null, null);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void insertPhone(Uri uri) {
        ArrayList<ContentProviderOperation> op = new ArrayList<>();
        op.add(ContentProviderOperation.newInsert(uri)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "99999")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, op);
        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
        }
    }

    @SuppressLint("Range")
    private static List<ContactDetail> getNums(Cursor cursor) {
        List<ContactDetail> phones = new ArrayList<>();
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
            try (Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null)) {
                while (pCur.moveToNext()) {
                    int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phones.add(new ContactDetail(phoneType, getFormattedPhone(phone)));
                }
            }
        }
        return phones;
    }

    @SuppressLint("Range")
    private void getAddress(Cursor cursor) {
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        try (Cursor postals = contentResolver.query(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = "
                        + contactId, null, null)) {
            while (postals.moveToNext()) {
                int type = postals.getInt(postals.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                String address = postals.getString(postals.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                System.out.println();
            }
        }

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
