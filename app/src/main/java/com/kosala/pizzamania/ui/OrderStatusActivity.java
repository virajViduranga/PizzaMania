package com.kosala.pizzamania.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosala.pizzamania.R;

public class OrderStatusActivity extends AppCompatActivity {

    private TextView txtOrderId, txtOrderStatus, txtOrderBranch, txtOrderPrice;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        txtOrderId = findViewById(R.id.txtOrderId);
        txtOrderStatus = findViewById(R.id.txtOrderStatus);
        txtOrderBranch = findViewById(R.id.txtOrderBranch);
        txtOrderPrice = findViewById(R.id.txtOrderPrice);

        // Get orderId passed from OrderActivity
        int orderId = getIntent().getIntExtra("orderId", -1);

        if (orderId == -1) {
            Toast.makeText(this, "Invalid Order ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtOrderId.setText("Order ID: " + orderId);

        // Firebase reference for this order
        orderRef = FirebaseDatabase.getInstance().getReference("orders").child(String.valueOf(orderId));

        // Listen for status changes
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.child("status").getValue(String.class);
                    Integer branchId = snapshot.child("branchId").getValue(Integer.class);
                    Double totalPrice = snapshot.child("totalPrice").getValue(Double.class);

                    txtOrderStatus.setText("Status: " + status);
                    txtOrderBranch.setText("Branch ID: " + branchId);
                    txtOrderPrice.setText("Total: Rs. " + totalPrice);
                } else {
                    Toast.makeText(OrderStatusActivity.this, "Order not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderStatusActivity.this, "Failed to load order!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
