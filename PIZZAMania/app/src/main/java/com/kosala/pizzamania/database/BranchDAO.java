package com.kosala.pizzamania.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kosala.pizzamania.models.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    private DBHelper dbHelper;

    public BranchDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Get all branches
    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT branch_id, name, latitude, longitude FROM Branches", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("branch_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                branches.add(new Branch(id, name, lat, lng));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return branches;
    }

    // Check if branch has stock for item
    public boolean hasStock(int branchId, int itemId, int quantity) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quantity FROM Stock WHERE branch_id=? AND item_id=?",
                new String[]{String.valueOf(branchId), String.valueOf(itemId)});
        boolean result = false;
        if (cursor.moveToFirst()) {
            int stockQty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            if (stockQty >= quantity) result = true;
        }
        cursor.close();
        db.close();
        return result;
    }
}
