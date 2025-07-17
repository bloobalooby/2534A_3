package com.example.lab_rest;

import com.example.lab_rest.model.Item;
import java.util.ArrayList;
import java.util.List;

public class UserTempCart {
    private static final List<Item> cartItems = new ArrayList<>();

    public static void addItem(Item item) {
        cartItems.add(item);
    }

    public static List<Item> getCartItems() {
        return cartItems; // ✅ return original, not a copy
    }


    public static void clearCart() {
        cartItems.clear();
    }
}
