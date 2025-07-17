package com.example.lab_rest.remote;

import android.telephony.mbms.FileInfo;

import com.example.lab_rest.model.Profile;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {

    //  Login using username
    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );

    //  Login using email
    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(
            @Field("email") String email,
            @Field("password") String password
    );

    //  Submit a single item request
    @FormUrlEncoded
    @POST("requests")
    Call<Request> createRequest(@Header("api-key") String apiKey,
            @Field("user_id") int userId,
            @Field("item_id") int itemId,
            @Field("address") String address,
            @Field("notes") String notes
    );

    @FormUrlEncoded
    @POST("requests/updateStatus")
    Call<Void> updateRequestStatus(
            @Field("request_id") int requestId,
            @Field("status") String token,
            @Header("Authorization") String status
    );


    @Multipart
    @POST("files")
    Call<FileInfo> uploadFile(@Header("api-key") String apiKey, @Part MultipartBody.Part file);





    //  Get all requests for a specific user
    @GET("requests/user/{user_id}")
    Call<List<Request>> getRequestsByUser(@Path("user_id") int userId);

    //  Admin or general usage: get all requests
    @GET("requests")
    Call<List<Request>> getAllRequests(@Header("Authorization") String token);


    @GET("/profile/{user_id}")
    Call<Profile> getProfileByUserId(@Path("user_id") int userId);





}