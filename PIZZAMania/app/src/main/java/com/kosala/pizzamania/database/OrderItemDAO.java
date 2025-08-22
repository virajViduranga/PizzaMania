package com.kosala.pizzamania.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OrderItemDAO {

    private DBHelper dbHelper;

    public OrderItemDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Add order item
    public long addOrderItem(int orderId, int itemId, int quantity, double price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("item_id", itemId);
        values.put("quantity", quantity);
        values.put("price", price);
        long id = db.insert(DBHelper.TABLE_ORDER_ITEMS, null, values);
        db.close();
        return id;
    }

    // Get items by order
    public Cursor getOrderItemsByOrder(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"order_item_id", "item_id", "quantity", "price"};
        String selection = "order_id=?";
        String[] selectionArgs = {String.valueOf(orderId)};
        return db.query(DBHelper.TABLE_ORDER_ITEMS, columns, selection, selectionArgs, null, null, null);
    }
}

