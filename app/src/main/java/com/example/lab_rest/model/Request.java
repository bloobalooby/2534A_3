package com.example.lab_rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class representing a recycling request.
 */
public class Request {

    @SerializedName("request_id")
    private int request_id;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("item_id")
    private int item_id;

    @SerializedName("address")
    private String address;

    @SerializedName("request_date")
    private String request_date;

    @SerializedName("status")
    private String status;

    @SerializedName("weight")
    private double weight;

    @SerializedName("total_price")
    private double total_price;

    @SerializedName("notes")
    private String notes;

    @SerializedName("item")  // 🔥 This assumes your backend returns the full item object as "item"
    private Item item;

    // Constructor
    public Request(int request_id, int user_id, int item_id, String address, String request_date,
                   String status, double weight, double total_price, String notes, Item item) {
        this.request_id = request_id;
        this.user_id = user_id;
        this.item_id = item_id;
        this.address = address;
        this.request_date = request_date;
        this.status = status;
        this.weight = weight;
        this.total_price = total_price;
        this.notes = notes;
        this.item = item;
    }

    // Getters and Setters
    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    // String representation for debugging/logging
    @Override
    public String toString() {
        return "Request{" +
                "request_id=" + request_id +
                ", user_id=" + user_id +
                ", item_id=" + item_id +
                ", address='" + address + '\'' +
                ", request_date='" + request_date + '\'' +
                ", status='" + status + '\'' +
                ", weight=" + weight +
                ", total_price=" + total_price +
                ", notes='" + notes + '\'' +
                ", item=" + item +
                '}';
    }
}
