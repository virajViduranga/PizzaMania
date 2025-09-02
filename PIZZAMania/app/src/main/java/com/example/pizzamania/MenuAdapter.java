package com.example.pizzamania;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuItemModel> menuList;
    private Context context;

    public MenuAdapter(List<MenuItemModel> menuList, Context context) {
        this.menuList = menuList;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItemModel item = menuList.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDescription());
        holder.price.setText("Rs " + item.getPrice());

        Glide.with(context).load(item.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, price;
        ImageView image;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.menuName);
            desc = itemView.findViewById(R.id.menuDesc);
            price = itemView.findViewById(R.id.menuPrice);
            image = itemView.findViewById(R.id.menuImage);
        }
    }
}

