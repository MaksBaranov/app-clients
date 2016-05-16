package com.example.android.clients.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USER = "user";
    public static final String TABLE_CONTACT = "contact";

    //public static final String VIEW_CHATS_AND_CONTACTS = "chats_and_contacts";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "name";
    public static final String COLUMN_OWNER_ID = "owner_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TYPE = "type";



    private static final DAO[] DAOS = new DAO[] {
            User.DAO,
            Contact.DAO,
                };


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_key = 1");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (DAO dao : DAOS) {
            database.execSQL(dao.getTableCreationSql());
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        for (DAO<?> dao : DAOS) {
            if (dao.shouldCreateTable(oldVersion, newVersion)) {
                database.execSQL(dao.getTableCreationSql());
            }
            for (String query : dao.getTableUpdateSql(oldVersion, newVersion)) {
                database.execSQL(query);
            }
        }
    }
}
