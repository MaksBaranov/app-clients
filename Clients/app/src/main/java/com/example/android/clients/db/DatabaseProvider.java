package com.example.android.clients.db;

import android.content.*;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.android.clients.db.DatabaseHelper.*;

public class DatabaseProvider extends ContentProvider {
    private static final String TAG = "DatabaseProvider";

    public static final String AUTHORITY = "com.example.android.clients.provider";

    public static final Uri USER_URI = tableUri(TABLE_USER);
    public static final Uri CONTACTS_URI = tableUri(TABLE_CONTACT);


    public static Uri tableUri(String tableName) {
        return Uri.parse("content://" + AUTHORITY + "/" + tableName);
    }



    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        //SQLiteDatabase.loadLibs(getContext());
        dbHelper = new DatabaseHelper(getContext().getApplicationContext());
        if (database != null) {
            return true;
        }

        try {
            database = dbHelper.getWritableDatabase();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Failed to open database", e);
            return false;
        }



    }


     private synchronized boolean openDatabase(String password) {

        if (database != null) {
            return true;
        }

        try {
            database = dbHelper.getWritableDatabase();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Failed to open database", e);
            return false;
        }
    }




    private synchronized SQLiteDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Database is not open");
        }
        return database;
    }

    private static String getTableName(Uri uri) {
        return uri.getPathSegments().get(0);
    }

    private static String addIdToSelection(Uri uri, String selection) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 2) {
            return (TextUtils.isEmpty(selection) ? "" : "(" + selection + ") AND ") + "_ID = ?";
        }
        return selection;
    }

    private static String[] addIdToSelectionArgs(Uri uri, String[] selectionArgs) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 2) {
            if (selectionArgs == null) {
                return new String[]{pathSegments.get(1)};
            }
            List<String> args = Arrays.asList(selectionArgs);
            args.add(pathSegments.get(1));
            return (String[]) args.toArray();
        }
        return selectionArgs;
    }

    private static void checkThread(String description) {
        final String threadName = Thread.currentThread().getName();
        Log.d(TAG, description + " " + threadName);
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sort) {
        checkThread("query " + uri.toString() + " (" + selection + ")");



        selection = addIdToSelection(uri, selection);
        selectionArgs = addIdToSelectionArgs(uri, selectionArgs);
        Cursor cursor = getDatabase().query(getTableName(uri), columns, selection, selectionArgs, null, null, sort);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        checkThread("insert " + uri.toString());

        long id = getDatabase().insertOrThrow(getTableName(uri), null, contentValues);
        Uri result = Uri.withAppendedPath(uri, Long.toString(id));
        getContext().getContentResolver().notifyChange(result, null);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        checkThread("delete " + uri.toString() + " (" + selection + ")");

        selection = addIdToSelection(uri, selection);
        selectionArgs = addIdToSelectionArgs(uri, selectionArgs);
        int count = getDatabase().delete(getTableName(uri), selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        checkThread("update " + uri.toString() + " (" + selection + ")");

        selection = addIdToSelection(uri, selection);
        selectionArgs = addIdToSelectionArgs(uri, selectionArgs);
        int count = getDatabase().update(getTableName(uri), contentValues, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        checkThread("applyBatch");

        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        ContentProviderResult[] results;
        try {
            results = super.applyBatch(operations);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
        return results;
    }
}