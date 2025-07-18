package com.example.lab_rest.model;

/**
 * Model class representing a recyclable item.
 */
public class RecyclableItem {

    // Fields matching recyclable_items table or API response
    private int id;
    private String name;
    private double price_per_kg;

    // Default constructor
    public RecyclableItem() {
    }

    // Full constructor
    public RecyclableItem(int id, String name, double price_per_kg) {
        this.id = id;
        this.name = name;
        this.price_per_kg = price_per_kg;
    }

    // Getter and Setter for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for Price per KG
    public double getPrice_per_kg() {
        return price_per_kg;
    }

    public void setPrice_per_kg(double price_per_kg) {
        this.price_per_kg = price_per_kg;
    }

    // Optional: Getter with simpler name for convenience
    public double getPrice() {
        return price_per_kg;
    }

    // toString() method for debugging/logging
    @Override
    public String toString() {
        return "RecyclableItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price_per_kg=" + price_per_kg +
                '}';
    }
}
