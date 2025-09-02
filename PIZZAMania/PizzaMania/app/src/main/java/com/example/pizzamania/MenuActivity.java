package com.example.pizzamania;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MenuAdapter adapter;
    List<MenuItemModel> menuList;
    FirebaseFirestore db;  // Firestore reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuList = new ArrayList<>();
        adapter = new MenuAdapter(menuList, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch data from Firestore instead of using dummy data
        fetchMenuData();
    }

    private void fetchMenuData() {
        db.collection("menu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    menuList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        MenuItemModel item = doc.toObject(MenuItemModel.class);
                        menuList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching data", e);
                    Toast.makeText(MenuActivity.this, "Failed to load menu", Toast.LENGTH_SHORT).show();
                });
    }
}
