package com.kosala.pizza_mania;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import com.kosala.pizza_mania.utils.CartDatabaseHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocationSelectActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQ_LOCATION_PERMISSION = 1001;

    private GoogleMap mMap;
    private Marker currentMarker;
    private LatLng selectedLatLng;
    private Button btnConfirm;
    private ImageButton btnMyLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private CartDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        dbHelper = new CartDatabaseHelper(this);

        // Initialize Places SDK (make sure API key is set in Manifest)
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key)); // or use manifest meta-data already set
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Add SupportMapFragment into the container
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commitAllowingStateLoss();

        mapFragment.getMapAsync(this);

        // Setup Places Autocomplete fragment (search bar)
        setupPlacesAutocomplete();

        // Confirm button action
        btnConfirm.setOnClickListener(v -> {
            if (selectedLatLng == null) {
                Toast.makeText(LocationSelectActivity.this, "Please choose a location!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Clear cart now (you wanted cart cleared after payment)
            try {
                dbHelper.clearCart();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(LocationSelectActivity.this,
                    "Payment and location saved ✅\nLat: " + selectedLatLng.latitude +
                            "\nLng: " + selectedLatLng.longitude,
                    Toast.LENGTH_LONG).show();

            // Return to MainActivity (or you can start Delivery flow)
            Intent i = new Intent(LocationSelectActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("delivery_lat", selectedLatLng.latitude);
            i.putExtra("delivery_lng", selectedLatLng.longitude);
            startActivity(i);
            finish();
        });

        // My location button
        btnMyLocation.setOnClickListener(v -> goToMyLocation());
    }

    private void setupPlacesAutocomplete() {
        AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
            autocompleteFragment.setHint("Search address or place...");

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Move camera to the selected place and set marker
                    LatLng latLng = place.getLatLng();
                    if (latLng != null && mMap != null) {
                        moveCameraAndPlaceMarker(latLng, place.getAddress() != null ? place.getAddress() : place.getName());
                    }
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Toast.makeText(LocationSelectActivity.this, "Place error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // If permission granted, enable my-location and try to center map on user location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocationAndCenter();
        } else {
            // Request runtime permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMISSION);
        }

        // Map click: place marker
        mMap.setOnMapClickListener(latLng -> {
            moveCameraAndPlaceMarker(latLng, "Selected Location");
        });

        // Marker drag listener (if user drags pin)
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override public void onMarkerDragStart(Marker marker) { }
            @Override public void onMarkerDrag(Marker marker) { }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                selectedLatLng = marker.getPosition();
                // Optionally reverse-geocode to show address (async)
                String addr = getAddressFromLatLng(selectedLatLng);
                if (addr != null && !addr.isEmpty()) {
                    marker.setTitle(addr);
                    marker.showInfoWindow();
                }
            }
        });
    }

    private void enableMyLocationAndCenter() {
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        goToMyLocation();
    }

    private void goToMyLocation() {
        // Try to get last known location and animate camera to it
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && mMap != null) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                moveCameraAndPlaceMarker(loc, "Your Location");
            } else {
                Toast.makeText(LocationSelectActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(LocationSelectActivity.this, "Location error", Toast.LENGTH_SHORT).show();
        });
    }

    // Move camera smoothly and place a draggable marker
    private void moveCameraAndPlaceMarker(LatLng latLng, String title) {
        if (mMap == null || latLng == null) return;

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        selectedLatLng = latLng;

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions mo = new MarkerOptions()
                .position(latLng)
                .title(title)
                .draggable(true);

        currentMarker = mMap.addMarker(mo);
        if (currentMarker != null) currentMarker.showInfoWindow();
    }

    // Reverse geocode to text (simple sync version, ok for short)
    private String getAddressFromLatLng(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (list != null && !list.isEmpty()) {
                Address addr = list.get(0);
                StringBuilder out = new StringBuilder();
                if (addr.getThoroughfare() != null) out.append(addr.getThoroughfare()).append(" ");
                if (addr.getLocality() != null) out.append(addr.getLocality()).append(" ");
                if (addr.getCountryName() != null) out.append(addr.getCountryName());
                return out.toString().trim();
            }
        } catch (IOException ignored) { }
        return null;
    }

    // Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    enableMyLocationAndCenter();
                }
            } else {
                Toast.makeText(this, "Location permission required to show your position.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
