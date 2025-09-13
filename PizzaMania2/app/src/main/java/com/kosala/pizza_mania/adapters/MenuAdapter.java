package com.kosala.pizza_mania.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kosala.pizza_mania.R;
import com.kosala.pizza_mania.models.Pizza;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<Pizza> pizzaList;
    private FirebaseFirestore db;

    public MenuAdapter(Context context, List<Pizza> pizzaList, FirebaseFirestore db) {
        this.context = context;
        this.pizzaList = pizzaList;
        this.db = db;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Pizza pizza = pizzaList.get(position);

        holder.tvName.setText(pizza.getName());
        holder.tvPrice.setText("Rs " + pizza.getPrice());

        Glide.with(context).load(pizza.getImageUrl()).into(holder.ivPizza);

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Pizza")
                    .setMessage("Are you sure you want to delete this pizza?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.collection("branches")
                                .document(pizza.getId().split("_")[0]) // optional branch id handling
                                .collection("menu")
                                .document(pizza.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    pizzaList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Pizza deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivPizza;
        ImageButton btnDelete;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivPizza = itemView.findViewById(R.id.ivPizza);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
