package com.kosala.pizza_mania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    TextView txtBranchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtBranchName = findViewById(R.id.txtBranchName);

        // Get branch name from MapsActivity
        String branchName = getIntent().getStringExtra("branch_name");
        if (branchName == null) branchName = "Colombo";

        txtBranchName.setText("Menu - " + branchName);

        // TODO: RecyclerView for pizza list
    }
}
