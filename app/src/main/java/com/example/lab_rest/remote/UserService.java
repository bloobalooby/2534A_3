package com.example.lab_rest.remote;

import com.example.lab_rest.model.User;
import com.example.lab_rest.model.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(@Field("username") String username,
                     @Field("password") String password);

    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(@Field("email") String email,
                          @Field("password") String password);

    @GET("requests")
    Call<List<Request>> getAllRequests();


    @GET("requests/user/{user_id}")
    Call<List<Request>> getRequestsByUser(@Path("user_id") int userId);
}
