package com.example.lab_rest.model;

public class Request {
    private int request_id;
    private int user_id;
    private int item_id;
    private String address;
    private String request_date;
    private String status;
    private double weight;
    private double total_price;
    private String notes;

    // setter and getter
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
}
