package com.kosala.pizza_mania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        mAuth = FirebaseAuth.getInstance();

        // Welcome Text
        TextView tv = findViewById(R.id.tvAdminWelcome);
        tv.setText("👑 Welcome Admin! You are logged in.");

        // Sign Out Button
        btnSignOut = findViewById(R.id.btnSignOut); // Add this button in XML
        btnSignOut.setOnClickListener(v -> signOutAdmin());
    }

    private void signOutAdmin() {
        mAuth.signOut();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // prevent back press
        startActivity(intent);
        finish();
    }
}
