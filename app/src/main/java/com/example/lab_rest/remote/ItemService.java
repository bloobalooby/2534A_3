package com.example.lab_rest.remote;

import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.DeleteResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ItemService {
    @GET("recyclable_items")
    Call<List<Item>> getAllItems(@Header("api-key") String api_key);

    @GET("recyclable_items/{id}")
    Call<Item> getItems(@Header("api-key") String api_key, @Path("id") int id);

    @FormUrlEncoded
    @POST("recyclable_items/{id}")
    Call<Item> updateItem(@Header("api-key") String apiKey, @Path("id") int id,
                          @Field("item_name") String name, @Field("price_per_kg") String price);

    @DELETE("recyclable_items/{id}")
    Call<DeleteResponse> deleteItem(@Header ("api-key") String apiKey, @Path("id") int id);
}