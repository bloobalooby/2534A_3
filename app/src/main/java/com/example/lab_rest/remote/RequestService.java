package com.example.lab_rest.remote;

import com.example.lab_rest.adapter.RequestAdapter;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.RequestStatusBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    // Update status of an existing request
    @FormUrlEncoded
    @POST("requests/{id}")
    Call<Void> updateRequestStatus(
            @Header("api-key") String apiKey,
            @Path("id") int requestId,
            @Field("status") String status
    );


    // Get all requests submitted by a specific user
    @GET("requests")
    Call<List<Request>> getRequestsByUser(
            @Header("api-key") String apiKey,
            @Query("user_id") int userId
    );

    // Submit a new item request
    @FormUrlEncoded
    @POST("requests")
    Call<Request> createRequest(
            @Header("api-key") String apiKey,
            @Field("user_id") int userId,
            @Field("item_id") int itemId,
            @Field("address") String address,
            @Field("notes") String notes
    );

}
