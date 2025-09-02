package com.kosala.pizzamania.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PaymentDAO {

    private DBHelper dbHelper;

    public PaymentDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Add payment
    public long addPayment(int orderId, double amount, String method, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("amount", amount);
        values.put("method", method);
        values.put("status", status);
        long id = db.insert(DBHelper.TABLE_PAYMENTS, null, values);
        db.close();
        return id;
    }

    // Update payment status
    public int updatePaymentStatus(int paymentId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int rows = db.update(DBHelper.TABLE_PAYMENTS, values, "payment_id=?", new String[]{String.valueOf(paymentId)});
        db.close();
        return rows;
    }

    // Get payment by order
    public Cursor getPaymentByOrder(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"payment_id", "amount", "method", "status", "payment_date"};
        String selection = "order_id=?";
        String[] selectionArgs = {String.valueOf(orderId)};
        return db.query(DBHelper.TABLE_PAYMENTS, columns, selection, selectionArgs, null, null, null);
    }
}

