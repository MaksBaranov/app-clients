package com.example.android.clients.db;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.clients.db.DatabaseHelper.*;

@DAO.Table(name = TABLE_USER)
public class User implements Serializable {

    public static final DAO<User> DAO = new DAO<User>(User.class);

    public User(){}

    public User(String name){
        this.name=name;
    }

    @DAO.Column(name = COLUMN_ID,primaryKey = true)
    long id;

    @DAO.Column
    String name;


    @DAO.Column
    String gender;

    @DAO.Column
    Date age;


    @DAO.Column
    Date ageRange;

    @DAO.Column
    String profileFhoto;

    @DAO.Column
    String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAge() {
        return age;
    }

    public void setAge(Date age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(Date ageRange) {
        this.ageRange = ageRange;
    }

    public String getProfileFhoto() {
        return profileFhoto;
    }

    public void setProfileFhoto(String profileFhoto) {
        this.profileFhoto = profileFhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public static User createUserWithUsername(Context context, String username) {
        long newUserId;
        try {
            newUserId = User.DAO.insert(context.getContentResolver(), new User(username));
        }
        catch (Exception e) {
            return null;
        }
        return findUserById(context, newUserId);
    }


    public static User findUserById(Context context, long id) {
        return User.DAO.queryById(context.getContentResolver(), id);
    }

    public static User findUserByUsername(Context context, String username) {
        List<User> userList = User.DAO.query(context.getContentResolver(), COLUMN_USERNAME + " = ?", new String[]{username}, null);

        if (userList.isEmpty()) {
            Log.d("Contact", "No contact found.");
            return null;
        }

        return userList.get(0);
    }

    public static List<String> getUserNames(Context context) {
        List<String> result = new ArrayList<String>();

        Cursor cursor = context.getContentResolver().query(
                DatabaseProvider.USER_URI,
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

    public static User findOrCreateUserWithUsername(Context context, String username) {

        User user = createUserWithUsername(context, username);
        if (user == null) {
            user = findUserByUsername(context, username);
        }
        return user;
    }

}
