package com.example.lab_rest.remote;

import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.RecyclableItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit interface for managing recyclable items.
 */
public interface ItemService {

    // Get all recyclable items
    @GET("recyclable_items")
    Call<List<Item>> getAllItems(
            @Header("api-key") String apiKey
    );

    // Get a single recyclable item by its ID
    @GET("recyclable_items/{id}")
    Call<Item> getItems(
            @Header("api-key") String apiKey,
            @Path("id") int id
    );

    // Add a new recyclable item
    @FormUrlEncoded
    @POST("recyclable_items")
    Call<RecyclableItem> addItem(
            @Header("api-key") String apiKey,
            @Field("item_name") String name,
            @Field("price_per_kg") double price
    );

    // Update an existing recyclable item
    @FormUrlEncoded
    @POST("recyclable_items/{id}")
    Call<Item> updateItem(
            @Header("api-key") String apiKey,
            @Path("id") int id,
            @Field("item_name") String name,
            @Field("price_per_kg") String price
    );

    // Delete a recyclable item by ID
    @DELETE("recyclable_items/{id}")
    Call<DeleteResponse> deleteItem(
            @Header("api-key") String apiKey,
            @Path("id") int id
    );
}
