package com.kosala.pizzamania.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OrderDAO {

    private DBHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Place a new order
    public long addOrder(int userId, int branchId, double totalPrice, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("branch_id", branchId);
        values.put("total_price", totalPrice);
        values.put("status", status);
        long orderId = db.insert(DBHelper.TABLE_ORDERS, null, values);
        db.close();
        return orderId;
    }

    // Update order status
    public int updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int rows = db.update(DBHelper.TABLE_ORDERS, values, "order_id=?", new String[]{String.valueOf(orderId)});
        db.close();
        return rows;
    }

    // Get all orders for a user
    public Cursor getOrdersByUser(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"order_id", "branch_id", "status", "total_price", "order_date"};
        String selection = "user_id=?";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.query(DBHelper.TABLE_ORDERS, columns, selection, selectionArgs, null, null, "order_date DESC");
    }

    // Get order by ID
    public Cursor getOrderById(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"order_id", "user_id", "branch_id", "status", "total_price", "order_date"};
        String selection = "order_id=?";
        String[] selectionArgs = {String.valueOf(orderId)};
        return db.query(DBHelper.TABLE_ORDERS, columns, selection, selectionArgs, null, null, null);
    }
}
