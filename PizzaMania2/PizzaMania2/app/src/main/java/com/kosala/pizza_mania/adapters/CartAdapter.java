package com.kosala.pizza_mania.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kosala.pizza_mania.R;
import com.kosala.pizza_mania.models.Pizza;
import com.kosala.pizza_mania.utils.CartDatabaseHelper;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private final List<Pizza> cartList;
    private final CartDatabaseHelper dbHelper;
    private final Runnable refreshCallback;

    public CartAdapter(Context context, List<Pizza> cartList, CartDatabaseHelper dbHelper, Runnable refreshCallback) {
        this.context = context;
        this.cartList = cartList;
        this.dbHelper = dbHelper;
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pizza pizza = cartList.get(position);

        holder.tvName.setText(pizza.getName());
        holder.tvQuantity.setText(String.valueOf(pizza.getQuantity()));
        holder.tvPrice.setText("Rs " + (pizza.getPrice() * pizza.getQuantity()));

        holder.btnIncrease.setOnClickListener(v -> {
            int qty = pizza.getQuantity() + 1;
            dbHelper.updateQuantity(pizza.getName(), qty);
            refreshCallback.run(); // Reload from DB
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int qty = pizza.getQuantity() - 1;
            if (qty <= 0) {
                dbHelper.removeItem(pizza.getName());
            } else {
                dbHelper.updateQuantity(pizza.getName(), qty);
            }
            refreshCallback.run();
        });

        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.removeItem(pizza.getName());
            Toast.makeText(context, pizza.getName() + " removed", Toast.LENGTH_SHORT).show();
            refreshCallback.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice;
        Button btnIncrease, btnDecrease, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPizzaName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPizzaPrice);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
