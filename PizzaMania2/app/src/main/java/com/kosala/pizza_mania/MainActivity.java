package com.kosala.pizza_mania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnMenu, btnCart, btnBranches, btnOrders, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnMenu = findViewById(R.id.btnMenu);
        btnCart = findViewById(R.id.btnCart);
        btnBranches = findViewById(R.id.btnBranches);
        btnOrders = findViewById(R.id.btnOrders);
        btnProfile = findViewById(R.id.btnProfile);

        // ✅ Navigate to MenuActivity
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        // ✅ Navigate to MapsActivity (Branches)
        btnBranches.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        // 🔜 Other buttons (placeholders)
        btnCart.setOnClickListener(v -> { /* TODO: Open CartActivity */ });
        btnOrders.setOnClickListener(v -> { /* TODO: Open OrdersActivity */ });
        btnProfile.setOnClickListener(v -> { /* TODO: Open ProfileActivity */ });
    }
}
