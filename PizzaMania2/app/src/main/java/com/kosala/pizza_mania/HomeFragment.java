package com.kosala.pizza_mania;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kosala.pizza_mania.adapters.RecommendedMenuAdapter;
import com.kosala.pizza_mania.models.Branch;
import com.kosala.pizza_mania.models.Pizza;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_CODE = 1001;

    private RecyclerView rvRecommended;
    private RecommendedMenuAdapter adapter;
    private List<Pizza> pizzaList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView tvNearest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SearchView searchView;

    public HomeFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true); // enable menu in toolbar

        // Setup Toolbar (unchanged)
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        rvRecommended = view.findViewById(R.id.rvRecommended);
        progressBar = view.findViewById(R.id.progressBar);
        tvNearest = view.findViewById(R.id.tvNearest);
        searchView = view.findViewById(R.id.searchView);

        pizzaList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        rvRecommended.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecommendedMenuAdapter(getContext(), pizzaList);
        rvRecommended.setAdapter(adapter);

        requestLocationPermission();

        setupSearchBar(); // ✅ Only this changes the SearchView colors

        return view;
    }

    private void setupSearchBar() {
        // ✅ Typed letters black & hint gray
        int id = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(id);
        if (searchText != null) {
            searchText.setTextColor(getResources().getColor(android.R.color.black));
            searchText.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        // ✅ Magnifier icon black
        int searchMagId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        View searchIcon = searchView.findViewById(searchMagId);
        if (searchIcon != null && searchIcon instanceof android.widget.ImageView) {
            ((android.widget.ImageView) searchIcon).setColorFilter(
                    getResources().getColor(android.R.color.black));
        }

        // ✅ Close (x) icon black
        int closeBtnId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        View closeBtn = searchView.findViewById(closeBtnId);
        if (closeBtn != null && closeBtn instanceof android.widget.ImageView) {
            ((android.widget.ImageView) closeBtn).setColorFilter(
                    getResources().getColor(android.R.color.black));
        }

        searchView.setIconifiedByDefault(false);

        // Add filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPizzas(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPizzas(newText);
                return true;
            }
        });
    }

    private void filterPizzas(String query) {
        List<Pizza> filtered = new ArrayList<>();
        for (Pizza pizza : pizzaList) {
            if (pizza.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(pizza);
            }
        }
        adapter = new RecommendedMenuAdapter(getContext(), filtered);
        rvRecommended.setAdapter(adapter);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            detectNearestBranch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectNearestBranch();
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                tvNearest.setText("Enable location to see nearest branch");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void detectNearestBranch() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        findNearestBranch(location);
                    } else {
                        tvNearest.setText("Unable to get location");
                    }
                })
                .addOnFailureListener(e -> tvNearest.setText("Location error: " + e.getMessage()));
    }

    private void findNearestBranch(Location userLocation) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("branches")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    progressBar.setVisibility(View.GONE);

                    Branch nearestBranch = null;
                    float minDistance = Float.MAX_VALUE;

                    for (DocumentSnapshot doc : querySnapshot) {
                        Branch branch = doc.toObject(Branch.class);
                        if (branch != null) {
                            branch.setId(doc.getId());
                            Location branchLocation = new Location("");
                            branchLocation.setLatitude(branch.getLat());
                            branchLocation.setLongitude(branch.getLng());

                            float distance = userLocation.distanceTo(branchLocation);
                            if (distance < minDistance) {
                                minDistance = distance;
                                nearestBranch = branch;
                            }
                        }
                    }

                    if (nearestBranch != null) {
                        tvNearest.setText("Nearest branch: " + nearestBranch.getName());
                        loadMenu(nearestBranch.getId());
                    } else {
                        tvNearest.setText("No branches found");
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvNearest.setText("Error: " + e.getMessage());
                });
    }

    private void loadMenu(String branchId) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("branches")
                .document(branchId)
                .collection("menu")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    pizzaList.clear();

                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Pizza pizza = doc.toObject(Pizza.class);
                            if (pizza != null) pizzaList.add(pizza);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No menu found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(getContext(), CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
