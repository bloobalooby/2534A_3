package com.example.lab_rest.remote;

import com.example.lab_rest.model.Item;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ItemService {
    @GET("recyclable_items")
    Call<List<Item>> getAllItems(@Header("api-key") String api_key);
}