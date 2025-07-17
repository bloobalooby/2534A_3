package com.example.lab_rest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {
    @SerializedName("item_id")
    private int itemId;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("price_per_kg")
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

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
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

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", price='" + price + '\'' +
                ", imageResId='" + imageResId + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
