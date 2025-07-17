package com.example.lab_rest;

import com.example.lab_rest.model.Item;
import java.util.ArrayList;
import java.util.List;

public class UserTempCart {
    private static List<Item> cartItems = new ArrayList<>();

    public static void setCartItems(List<Item> items) {
        cartItems = items;
    }

    public static List<Item> getCartItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
    }
}

