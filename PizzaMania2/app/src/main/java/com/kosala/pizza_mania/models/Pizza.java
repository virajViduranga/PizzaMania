package com.kosala.pizza_mania.models;

public class Pizza {
    private String name;
    private double price;
    private String imageUrl; // 🔹 Now a String URL

    public Pizza() { }

    public Pizza(String name, double price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
