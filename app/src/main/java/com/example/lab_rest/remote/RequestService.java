package com.example.lab_rest.remote;

import com.example.lab_rest.model.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit interface for handling request-related API operations.
 */
public interface RequestService {

    // 📋 Get a list of all requests (admin or general)
    @GET("requests")
    Call<List<Request>> getAllRequests(
            @Header("api-key") String apiKey
    );

    // 🔍 Get a single request by its ID
    @GET("requests/{id}")
    Call<Request> getRequests(
            @Header("api-key") String apiKey,
            @Path("id") int id
    );

}
