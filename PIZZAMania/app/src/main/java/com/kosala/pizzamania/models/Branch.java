package com.kosala.pizzamania.models;

public class Branch {
    public int branchId;
    public String name;
    public double latitude;
    public double longitude;

    public Branch(int branchId, String name, double latitude, double longitude) {
        this.branchId = branchId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
