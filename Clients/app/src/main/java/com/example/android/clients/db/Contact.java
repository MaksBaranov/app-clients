package com.example.android.clients.db;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.clients.db.DatabaseHelper.*;
@DAO.Table(name = TABLE_CONTACT)
public class Contact implements Serializable {

    public static final DAO<Contact> DAO = new DAO<Contact>(Contact.class);


public Contact(){}


    public Contact(String userName){

        this.userName=userName;
    }



  @DAO.Column(name =  COLUMN_ID,primaryKey = true)
    long id;

    @DAO.Column(references = TABLE_USER + "(" + COLUMN_ID + ")")
    long ownerId;

    @DAO.Column(name =  COLUMN_USERNAME)
    String userName;

    @DAO.Column()
    String photoURL;

    @DAO.Column
    String type;


    @DAO.Column
    String category;

    @DAO.Column
    Date lastUpdate;

    @DAO.Column
    String LastUserUpdate;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUserUpdate() {
        return LastUserUpdate;
    }

    public void setLastUserUpdate(String lastUserUpdate) {
        LastUserUpdate = lastUserUpdate;
    }


    public static Contact createContactWithUsername(Context context, String username) {
        long newContactId;
        try {
            newContactId = Contact.DAO.insert(context.getContentResolver(), new Contact(username));
        }
        catch (Exception e) {
            Log.e("Error_db",String.valueOf(e));
            return null;
        }
        return findContactById(context, newContactId);
    }

    public static Contact findContactById(Context context, long id) {
        return Contact.DAO.queryById(context.getContentResolver(), id);
    }

    public static Contact findContactByUsername(Context context, String username) {
        List<Contact> contactList = Contact.DAO.query(context.getContentResolver(), COLUMN_USERNAME + " = ?", new String[]{username}, null);

        if (contactList.isEmpty()) {
            Log.d("Contact", "No contact found.");
            return null;
        }

        return contactList.get(0);
    }

    public static List<String> getUsernames(Context context) {
        List<String> result = new ArrayList<String>();

        Cursor cursor = context.getContentResolver().query(
                DatabaseProvider.CONTACTS_URI,
                new String[]{COLUMN_USERNAME},
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    result.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }

        return result;
    }



    public static List<String> getUsernamesByType(Context context,String category) {
        List<String> result = new ArrayList<String>();

        Cursor cursor = context.getContentResolver().query(
                DatabaseProvider.CONTACTS_URI,
                new String[]{COLUMN_USERNAME},
                COLUMN_CATEGORY + " = ?",
                new String[]{category},
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    result.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }

        return result;
    }



    public static Contact findOrCreateContactWithUsername(Context context, String username) {

        Contact contact = createContactWithUsername(context, username);
        if (contact == null) {
            contact = findContactByUsername(context, username);
        }
        return contact;
    }




}
