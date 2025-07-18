package com.example.lab_rest.remote;

import com.example.lab_rest.model.Profile;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for user-related API endpoints.
 */
public interface UserService {

    // Login using username and password
    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );

    // Login using email and password
    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(
            @Field("email") String email,
            @Field("password") String password
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

    // Update status of an existing request
    @FormUrlEncoded
    @PUT("requests/{id}/status")
    Call<Void> updateRequestStatus(
            @Path("id") int requestId,
            @Header("Authorization") String token,
            @Field("status") String status
    );

    // Upload a file
    @Multipart
    @POST("files")
    Call<okhttp3.ResponseBody> uploadFile(
            @Header("api-key") String apiKey,
            @Part MultipartBody.Part file
    );

    // Get all requests submitted by a specific user
    @GET("requests")
    Call<List<Request>> getRequestsByUser(
            @Header("api-key") String apiKey,
            @Query("user_id") int userId
    );

    // Get all requests (admin/general)
    @GET("requests")
    Call<List<Request>> getAllRequests(
            @Header("Authorization") String token
    );

    //  Fetch profile by user ID
    @GET("/profile/{user_id}")
    Call<Profile> getProfileByUserId(
            @Path("user_id") int userId
    );
}
