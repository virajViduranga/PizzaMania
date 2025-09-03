package com.kosala.pizza_mania;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQ_LOC = 101;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;

    private final LatLng BRANCH_COLOMBO = new LatLng(6.9271, 79.8612);
    private final LatLng BRANCH_GALLE   = new LatLng(6.0535, 80.2210);

    private Marker colomboMarker, galleMarker;
    private LatLng nearestBranch;

    private Button btnMyLocation, btnNearest, btnDirections, btnOpenMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnNearest = findViewById(R.id.btnNearest);
        btnDirections = findViewById(R.id.btnDirections);
        btnOpenMenu = findViewById(R.id.btnOpenMenu); // new button

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) {
            GoogleMapOptions options = new GoogleMapOptions().zoomControlsEnabled(true);
            mapFragment = SupportMapFragment.newInstance(options);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment).commitNow();
        }
        mapFragment.getMapAsync(this);

        btnMyLocation.setOnClickListener(v -> enableMyLocation());

        btnNearest.setOnClickListener(v -> {
            if (lastKnownLocation == null) {
                toast("Getting your location… try again in a moment 🙏");
                fetchLastLocation();
                return;
            }
            nearestBranch = findNearestBranch(
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())
            );
            if (nearestBranch != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearestBranch, 14f));
                toast("Nearest branch selected ✅");
            }
        });

        btnDirections.setOnClickListener(v -> {
            if (nearestBranch == null) {
                toast("Select nearest branch first 🧭");
                return;
            }
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                    nearestBranch.latitude + "," + nearestBranch.longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        btnOpenMenu.setOnClickListener(v -> {
            if (nearestBranch == null) {
                toast("Select nearest branch first 🧭");
                return;
            }
            String branchName = nearestBranch.equals(BRANCH_COLOMBO) ? "Colombo" : "Galle";
            Intent intent = new Intent(MapsActivity.this, MenuActivity.class);
            intent.putExtra("branch_name", branchName);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        colomboMarker = mMap.addMarker(new MarkerOptions()
                .position(BRANCH_COLOMBO)
                .title("Pizza Mania - Colombo")
                .snippet("Open 10:00–22:00 • 011-1234567")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        galleMarker = mMap.addMarker(new MarkerOptions()
                .position(BRANCH_GALLE)
                .title("Pizza Mania - Galle")
                .snippet("Open 10:00–22:00 • 091-1234567")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(BRANCH_COLOMBO)
                .include(BRANCH_GALLE)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));

        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fetchLastLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOC);
        }
    }

    private void fetchLastLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    lastKnownLocation = location;
                    CameraPosition pos = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(13f).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
                }
            });
        } catch (SecurityException ignored) { }
    }

    private LatLng findNearestBranch(LatLng user) {
        float[] res1 = new float[1];
        float[] res2 = new float[1];
        Location.distanceBetween(user.latitude, user.longitude,
                BRANCH_COLOMBO.latitude, BRANCH_COLOMBO.longitude, res1);
        Location.distanceBetween(user.latitude, user.longitude,
                BRANCH_GALLE.latitude, BRANCH_GALLE.longitude, res2);
        return (res1[0] <= res2[0]) ? BRANCH_COLOMBO : BRANCH_GALLE;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                toast("Location permission denied. Some features will be limited ⚠️");
            }
        }
    }
}
