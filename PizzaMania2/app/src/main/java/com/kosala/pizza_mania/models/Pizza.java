package com.kosala.pizza_mania.models;

import com.google.firebase.firestore.DocumentReference;

public class Pizza {
    private String name;
    private double price;
    private DocumentReference imageUrl; // store Firestore reference

    public Pizza() { }

    public Pizza(String name, double price, DocumentReference imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public DocumentReference getImageUrl() { return imageUrl; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(DocumentReference imageUrl) { this.imageUrl = imageUrl; }
}
