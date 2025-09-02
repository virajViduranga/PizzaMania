package com.kosala.pizzamania.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pizzamania.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_BRANCHES = "Branches";
    public static final String TABLE_MENU = "Menu";
    public static final String TABLE_STOCK = "Stock";
    public static final String TABLE_ORDERS = "Orders";
    public static final String TABLE_ORDER_ITEMS = "OrderItems";
    public static final String TABLE_PAYMENTS = "Payments";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "phone TEXT, " +
                "address TEXT)");

        // Branches
        db.execSQL("CREATE TABLE " + TABLE_BRANCHES + " (" +
                "branch_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "latitude REAL, " +
                "longitude REAL)");

        // Menu
        db.execSQL("CREATE TABLE " + TABLE_MENU + " (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "category TEXT, " +
                "image_url TEXT)");

        // Stock
        db.execSQL("CREATE TABLE " + TABLE_STOCK + " (" +
                "stock_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "branch_id INTEGER, " +
                "item_id INTEGER, " +
                "quantity INTEGER, " +
                "FOREIGN KEY(branch_id) REFERENCES Branches(branch_id), " +
                "FOREIGN KEY(item_id) REFERENCES Menu(item_id))");

        // Orders
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "branch_id INTEGER, " +
                "status TEXT DEFAULT 'Pending', " +
                "total_price REAL, " +
                "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY(branch_id) REFERENCES Branches(branch_id))");

        // OrderItems
        db.execSQL("CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                "order_item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "item_id INTEGER, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "FOREIGN KEY(order_id) REFERENCES Orders(order_id), " +
                "FOREIGN KEY(item_id) REFERENCES Menu(item_id))");

        // Payments
        db.execSQL("CREATE TABLE " + TABLE_PAYMENTS + " (" +
                "payment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "amount REAL, " +
                "method TEXT, " +
                "status TEXT DEFAULT 'Pending', " +
                "payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(order_id) REFERENCES Orders(order_id))");

        // Cart table
        db.execSQL("CREATE TABLE Cart (" +
                "rowid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "item_id INTEGER, " +
                "quantity INTEGER, " +
                "price REAL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables and recreate if DB is upgraded
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
