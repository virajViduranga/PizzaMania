package com.kosala.pizzamania.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.kosala.pizzamania.R;
import com.kosala.pizzamania.database.CartDAO;
import com.kosala.pizzamania.services.OrderService;

public class OrderActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private OrderService orderService;
    private CartDAO cartDAO;
    private int userId = 1; // ⚡ later replace with logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize services
        orderService = new OrderService(this);
        cartDAO = new CartDAO(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Button listener
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(v -> checkLocationPermissionAndPlaceOrder());
    }

    private void checkLocationPermissionAndPlaceOrder() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getUserLocationAndPlaceOrder();
        }
    }

    private void getUserLocationAndPlaceOrder() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // Permission still not granted
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            handleOrderWithLocation(location);
                        } else {
                            Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (SecurityException e) {
            Log.e("OrderActivity", "Location permission not granted!", e);
            Toast.makeText(this, "Location access denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleOrderWithLocation(Location location) {
        Cursor cartItems = cartDAO.getCartItems(userId);
        if (cartItems == null || cartItems.getCount() == 0) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        double userLat = location.getLatitude();
        double userLng = location.getLongitude();

        int branchId = orderService.getNearestBranchWithStock(userLat, userLng, cartItems);

        if (branchId == -1) {
            Toast.makeText(this, "No branch with enough stock nearby!", Toast.LENGTH_LONG).show();
        } else {
            orderService.placeOrder(userId, branchId, "Cash");
            Toast.makeText(this, "Order placed at branch " + branchId, Toast.LENGTH_LONG).show();
            Log.d("OrderActivity", "Order placed at branch: " + branchId);
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationAndPlaceOrder();
            } else {
                Toast.makeText(this, "Location permission required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
