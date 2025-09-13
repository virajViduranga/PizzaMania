package com.kosala.pizza_mania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnSignOut, btnManageUsers, btnManageOrders, btnManageMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        mAuth = FirebaseAuth.getInstance();

        // Welcome Text
        TextView tv = findViewById(R.id.tvAdminWelcome);
        tv.setText("👑 Welcome Admin! You are logged in.");

        // Buttons
        btnSignOut = findViewById(R.id.btnSignOut);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnManageMenu = findViewById(R.id.btnManageMenu);

        // Events
        btnSignOut.setOnClickListener(v -> signOutAdmin());

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class));
        });

//        btnManageOrders.setOnClickListener(v -> {
//            startActivity(new Intent(AdminDashboardActivity.this, ManageOrdersActivity.class));
//        });
//
        btnManageMenu.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageMenuActivity.class));
        });
    }

    private void signOutAdmin() {
        mAuth.signOut();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
