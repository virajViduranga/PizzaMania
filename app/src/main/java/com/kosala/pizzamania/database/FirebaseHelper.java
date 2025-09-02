package com.kosala.pizzamania.database;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    private DatabaseReference database;
    private Context context;
    private MenuDAO menuDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;

    public FirebaseHelper(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance().getReference();
        menuDAO = new MenuDAO(context);
        orderDAO = new OrderDAO(context);
        orderItemDAO = new OrderItemDAO(context);
    }

    // Sync menu from Firebase → SQLite
    public void syncMenuFromFirebase() {
        database.child("menu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear local menu table first
                // You can implement a clearMenu() in MenuDAO
                // menuDAO.clearMenu();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String name = itemSnapshot.child("name").getValue(String.class);
                    String description = itemSnapshot.child("description").getValue(String.class);
                    Double price = itemSnapshot.child("price").getValue(Double.class);
                    String category = itemSnapshot.child("category").getValue(String.class);
                    String imageUrl = itemSnapshot.child("image_url").getValue(String.class);

                    menuDAO.addMenuItem(name, description, price != null ? price : 0, category, imageUrl);
                }
                Log.d("FirebaseSync", "Menu synced from Firebase");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseSync", "Failed to sync menu: " + error.getMessage());
            }
        });
    }

    // Push order from SQLite → Firebase
    public void pushOrderToFirebase(int orderId) {
        Cursor orderCursor = orderDAO.getOrderById(orderId);
        if (orderCursor != null && orderCursor.moveToFirst()) {
            int userId = orderCursor.getInt(orderCursor.getColumnIndexOrThrow("user_id"));
            int branchId = orderCursor.getInt(orderCursor.getColumnIndexOrThrow("branch_id"));
            String status = orderCursor.getString(orderCursor.getColumnIndexOrThrow("status"));
            double totalPrice = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow("total_price"));

            DatabaseReference orderRef = database.child("orders").child(String.valueOf(orderId));
            orderRef.child("userId").setValue(userId);
            orderRef.child("branchId").setValue(branchId);
            orderRef.child("status").setValue(status);
            orderRef.child("totalPrice").setValue(totalPrice);

            // Push order items
            Cursor itemsCursor = orderItemDAO.getOrderItemsByOrder(orderId);
            while (itemsCursor.moveToNext()) {
                int itemId = itemsCursor.getInt(itemsCursor.getColumnIndexOrThrow("item_id"));
                int quantity = itemsCursor.getInt(itemsCursor.getColumnIndexOrThrow("quantity"));
                double price = itemsCursor.getDouble(itemsCursor.getColumnIndexOrThrow("price"));

                orderRef.child("items").child(String.valueOf(itemId)).child("quantity").setValue(quantity);
                orderRef.child("items").child(String.valueOf(itemId)).child("price").setValue(price);
            }
            if (itemsCursor != null) itemsCursor.close();
            orderCursor.close();

            Log.d("FirebaseSync", "Order " + orderId + " pushed to Firebase");
        }
    }
}

