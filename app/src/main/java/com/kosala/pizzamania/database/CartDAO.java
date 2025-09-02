package com.kosala.pizzamania.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CartDAO {

    private DBHelper dbHelper;

    public CartDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Add item to cart
    public long addToCart(int userId, int itemId, int quantity, double price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("item_id", itemId);
        values.put("quantity", quantity);
        values.put("price", price);
        long id = db.insert("Cart", null, values);
        db.close();
        return id;
    }

    // Get cart items by user
    public Cursor getCartItems(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"rowid", "item_id", "quantity", "price"};
        String selection = "user_id=?";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.query("Cart", columns, selection, selectionArgs, null, null, null);
    }

    // Remove item from cart
    public int removeFromCart(int userId, int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Cart", "user_id=? AND item_id=?", new String[]{String.valueOf(userId), String.valueOf(itemId)});
        db.close();
        return rows;
    }

    // Clear cart after order
    public int clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Cart", "user_id=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows;
    }
}
