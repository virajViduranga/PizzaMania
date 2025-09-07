package com.kosala.pizza_mania;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kosala.pizza_mania.adapters.CartAdapter;
import com.kosala.pizza_mania.models.Pizza;
import com.kosala.pizza_mania.utils.CartDatabaseHelper;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartDatabaseHelper dbHelper;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        dbHelper = new CartDatabaseHelper(this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));

        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            dbHelper.clearCart();
            loadCartItems();
        });
    }

    private void loadCartItems() {
        List<Pizza> cartItems = dbHelper.getCartItems();
        adapter = new CartAdapter(this, cartItems, dbHelper, this::loadCartItems);
        rvCart.setAdapter(adapter);

        double total = dbHelper.getTotal();
        tvTotal.setText("Total: Rs " + total);
    }
}
