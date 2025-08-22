package com.kosala.pizzamania.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MenuDAO {

    private DBHelper dbHelper;

    public MenuDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Add menu item
    public long addMenuItem(String name, String description, double price, String category, String imageUrl) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("price", price);
        values.put("category", category);
        values.put("image_url", imageUrl);
        long id = db.insert(DBHelper.TABLE_MENU, null, values);
        db.close();
        return id;
    }

    // Get all menu items
    public Cursor getAllMenuItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"item_id", "name", "description", "price", "category", "image_url"};
        return db.query(DBHelper.TABLE_MENU, columns, null, null, null, null, null);
    }

    // Get menu by category
    public Cursor getMenuByCategory(String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"item_id", "name", "description", "price", "category", "image_url"};
        String selection = "category=?";
        String[] selectionArgs = {category};
        return db.query(DBHelper.TABLE_MENU, columns, selection, selectionArgs, null, null, null);
    }
}
