package com.kosala.pizzamania.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kosala.pizzamania.database.BranchDAO;   // ⚡️ new
import com.kosala.pizzamania.database.CartDAO;
import com.kosala.pizzamania.database.DBHelper;
import com.kosala.pizzamania.database.FirebaseHelper;
import com.kosala.pizzamania.database.OrderDAO;
import com.kosala.pizzamania.database.OrderItemDAO;
import com.kosala.pizzamania.database.PaymentDAO;
import com.kosala.pizzamania.models.Branch;         // ⚡️ new
import com.kosala.pizzamania.utils.LocationHelper; // ⚡️ new

import java.util.List;

public class OrderService {

    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private PaymentDAO paymentDAO;
    private DBHelper dbHelper;
    private FirebaseHelper firebaseHelper;
    private Context context; // ⚡️ needed for DAO

    public OrderService(Context context) {
        this.context = context; // ⚡️ save context
        cartDAO = new CartDAO(context);
        orderDAO = new OrderDAO(context);
        orderItemDAO = new OrderItemDAO(context);
        paymentDAO = new PaymentDAO(context);
        dbHelper = new DBHelper(context);
        firebaseHelper = new FirebaseHelper(context);
    }

    // ✅ New Method: Select Nearest Branch with Stock
    public int getNearestBranchWithStock(double userLat, double userLng, Cursor cartItems) {
        BranchDAO branchDAO = new BranchDAO(context);
        List<Branch> branches = branchDAO.getAllBranches();
        Branch nearestBranch = null;
        double minDistance = Double.MAX_VALUE;

        for (Branch branch : branches) {
            boolean hasAllStock = true;

            cartItems.moveToFirst();
            do {
                int itemId = cartItems.getInt(cartItems.getColumnIndexOrThrow("item_id"));
                int quantity = cartItems.getInt(cartItems.getColumnIndexOrThrow("quantity"));

                if (!branchDAO.hasStock(branch.branchId, itemId, quantity)) {
                    hasAllStock = false;
                    break;
                }
            } while (cartItems.moveToNext());

            if (hasAllStock) {
                double dist = LocationHelper.distance(userLat, userLng, branch.latitude, branch.longitude);
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestBranch = branch;
                }
            }
        }

        return nearestBranch != null ? nearestBranch.branchId : -1; // -1 if no branch found
    }

    // ✅ Existing Place Order Method
    public void placeOrder(int userId, int branchId, String paymentMethod) {
        Cursor cartItems = cartDAO.getCartItems(userId);
        if (cartItems == null || cartItems.getCount() == 0) {
            Log.e("OrderService", "Cart is empty!");
            return;
        }

        double totalPrice = 0;

        // Calculate total price
        while (cartItems.moveToNext()) {
            int quantity = cartItems.getInt(cartItems.getColumnIndexOrThrow("quantity"));
            double price = cartItems.getDouble(cartItems.getColumnIndexOrThrow("price"));
            totalPrice += price * quantity;
        }

        // 1️⃣ Create Order
        long orderId = orderDAO.addOrder(userId, branchId, totalPrice, "Pending");

        // 2️⃣ Add OrderItems
        cartItems.moveToFirst();
        do {
            int itemId = cartItems.getInt(cartItems.getColumnIndexOrThrow("item_id"));
            int quantity = cartItems.getInt(cartItems.getColumnIndexOrThrow("quantity"));
            double price = cartItems.getDouble(cartItems.getColumnIndexOrThrow("price"));
            orderItemDAO.addOrderItem((int) orderId, itemId, quantity, price);

            // Optional: update stock
            updateStock(branchId, itemId, quantity);

        } while (cartItems.moveToNext());

        cartItems.close();

        // 3️⃣ Process Payment
        paymentDAO.addPayment((int) orderId, totalPrice, paymentMethod, "Pending");

        // 4️⃣ Clear Cart
        cartDAO.clearCart(userId);

        // 5️⃣ Push to Firebase
        firebaseHelper.pushOrderToFirebase((int) orderId);

        Log.d("OrderService", "Order placed successfully! Order ID: " + orderId);
    }

    private void updateStock(int branchId, int itemId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE Stock SET quantity = quantity - " + quantity + " WHERE branch_id=" + branchId + " AND item_id=" + itemId);
        db.close();
    }
}
