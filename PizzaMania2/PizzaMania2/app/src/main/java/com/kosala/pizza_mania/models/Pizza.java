package com.kosala.pizza_mania.models;

public class Pizza {
    private String name;
    private double price;
    private String imageUrl;
    private int quantity; // new

    public Pizza() {
        this.quantity = 1;
    }

    public Pizza(String name, double price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = 1;
    }

    public String getName() {
        return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
