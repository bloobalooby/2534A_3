package com.example.lab_rest.model;

public class Item {
    private int itemId;
    private String itemName;
    private double price;

    public Item() {

    }
    public Item(int itemId, String itemName, double price) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
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
                ", itemId= '" + itemId + '\'' +
                ", itemName= '" + itemName + '\'' +
                ", price= '" + price + '\'' +
                '}';
    }
}
