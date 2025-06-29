package com.example.lab_rest.model;

public class Item {
    private int itemId;
    private String itemName;
    private double price;
    //new
    private int imageResId;
    private int quantity = 0;


    public Item(int itemId, String itemName, double price, int imageResId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = 0;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

