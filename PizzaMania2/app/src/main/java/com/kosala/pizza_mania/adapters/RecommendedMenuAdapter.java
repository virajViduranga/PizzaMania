package com.kosala.pizza_mania.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.kosala.pizza_mania.R;
import com.kosala.pizza_mania.models.Pizza;
import com.google.firebase.firestore.DocumentReference;
import java.util.List;

public class RecommendedMenuAdapter extends RecyclerView.Adapter<RecommendedMenuAdapter.ViewHolder> {

    private final Context context;
    private final List<Pizza> pizzaList;

    public RecommendedMenuAdapter(Context context, List<Pizza> pizzaList) {
        this.context = context;
        this.pizzaList = pizzaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pizza_with_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pizza pizza = pizzaList.get(position);

        holder.tvName.setText(pizza.getName());
        holder.tvPrice.setText("Rs " + pizza.getPrice());

        // Load image from DocumentReference
        DocumentReference imgRef = pizza.getImageUrl();
        if (imgRef != null) {
            imgRef.get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String url = doc.getString("url"); // Firestore field for image URL
                            Glide.with(context)
                                    .load(url)
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.ivPizza);
                        } else {
                            holder.ivPizza.setImageResource(R.drawable.placeholder);
                        }
                    })
                    .addOnFailureListener(e -> holder.ivPizza.setImageResource(R.drawable.placeholder));
        } else {
            holder.ivPizza.setImageResource(R.drawable.placeholder);
        }

        // Add to Cart button click
        holder.btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(context, pizza.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
            // You can add actual cart logic here
        });
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPizza;
        TextView tvName, tvPrice;
        Button btnAddToCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPizza = itemView.findViewById(R.id.ivPizza);
            tvName = itemView.findViewById(R.id.tvPizzaName);
            tvPrice = itemView.findViewById(R.id.tvPizzaPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
