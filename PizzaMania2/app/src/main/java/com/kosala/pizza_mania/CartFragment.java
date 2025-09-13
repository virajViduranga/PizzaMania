package com.kosala.pizza_mania;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kosala.pizza_mania.adapters.CartAdapter;
import com.kosala.pizza_mania.models.Pizza;
import com.kosala.pizza_mania.utils.CartDatabaseHelper;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartDatabaseHelper dbHelper;
    private CartAdapter adapter;

    public CartFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rvCart = view.findViewById(R.id.rvCart);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        dbHelper = new CartDatabaseHelper(requireContext());
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CheckoutActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadCartItems() {
        List<Pizza> cartItems = dbHelper.getCartItems();
        adapter = new CartAdapter(requireContext(), cartItems, dbHelper, this::loadCartItems);
        rvCart.setAdapter(adapter);

        double total = dbHelper.getTotal();
        tvTotal.setText("Total: Rs " + total);
    }
}
