package com.kosala.pizza_mania.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kosala.pizza_mania.models.Pizza;

import java.util.ArrayList;
import java.util.List;

public class CartDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pizza_cart.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_CART = "cart_items";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PRICE = "price";
    private static final String COL_IMAGE = "imageUrl";
    private static final String COL_QTY = "quantity";

    public CartDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_CART + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT UNIQUE, "
                + COL_PRICE + " REAL, "
                + COL_IMAGE + " TEXT, "
                + COL_QTY + " INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    /**
     * Add pizza to cart. If item exists, increase quantity.
     */
    public void addToCart(Pizza pizza) {
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor c = db.query(TABLE_CART, new String[]{COL_ID, COL_QTY},
                COL_NAME + "=?", new String[]{pizza.getName()},
                null, null, null)) {

            if (c != null && c.moveToFirst()) {
                int existingQty = c.getInt(c.getColumnIndexOrThrow(COL_QTY));
                int addQty = Math.max(1, pizza.getQuantity());
                int newQty = existingQty + addQty;
                ContentValues cv = new ContentValues();
                cv.put(COL_QTY, newQty);
                db.update(TABLE_CART, cv, COL_NAME + "=?", new String[]{pizza.getName()});
            } else {
                ContentValues cv = new ContentValues();
                cv.put(COL_NAME, pizza.getName());
                cv.put(COL_PRICE, pizza.getPrice());
                cv.put(COL_IMAGE, pizza.getImageUrl());
                cv.put(COL_QTY, Math.max(1, pizza.getQuantity()));
                db.insert(TABLE_CART, null, cv);
            }
        } finally {
            db.close();
        }
    }

    public List<Pizza> getCartItems() {
        List<Pizza> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_CART, null, null, null, null, null, null)) {
            if (c != null) {
                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
                    double price = c.getDouble(c.getColumnIndexOrThrow(COL_PRICE));
                    String image = c.getString(c.getColumnIndexOrThrow(COL_IMAGE));
                    int qty = c.getInt(c.getColumnIndexOrThrow(COL_QTY));
                    Pizza p = new Pizza(name, price, image);
                    p.setQuantity(qty);
                    list.add(p);
                }
            }
        } finally {
            db.close();
        }
        return list;
    }

    public void updateQuantity(String name, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            if (quantity <= 0) {
                db.delete(TABLE_CART, COL_NAME + "=?", new String[]{name});
            } else {
                ContentValues cv = new ContentValues();
                cv.put(COL_QTY, quantity);
                db.update(TABLE_CART, cv, COL_NAME + "=?", new String[]{name});
            }
        } finally {
            db.close();
        }
    }

    public void removeItem(String name) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_CART, COL_NAME + "=?", new String[]{name});
        } finally {
            db.close();
        }
    }

    public void clearCart() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_CART, null, null);
        } finally {
            db.close();
        }
    }

    public double getTotal() {
        double total = 0.0;
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT " + COL_PRICE + ", " + COL_QTY + " FROM " + TABLE_CART, null)) {
            if (c != null) {
                while (c.moveToNext()) {
                    double price = c.getDouble(0);
                    int qty = c.getInt(1);
                    total += price * qty;
                }
            }
        } finally {
            db.close();
        }
        return total;
    }
}
