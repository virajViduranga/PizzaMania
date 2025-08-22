package com.kosala.pizzamania.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDAO {

    private DBHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Add new user
    public long addUser(String name, String email, String password, String phone, String address) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        values.put("address", address);
        long id = db.insert(DBHelper.TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Check login
    public boolean checkUserLogin(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"user_id"};
        String selection = "email=? AND password=?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(DBHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Get user by email
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"user_id", "name", "email", "phone", "address"};
        String selection = "email=?";
        String[] selectionArgs = {email};
        return db.query(DBHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }
}