package com.example.lab_rest.remote;

import com.example.lab_rest.model.Profile;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {

    // 🔐 Login using username
    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );

    // 🔐 Login using email
    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(
            @Field("email") String email,
            @Field("password") String password
    );

    // 📥 Submit a single item request
    @FormUrlEncoded
    @POST("requests")
    Call<Void> submitRequest(
            @Field("user_id") int userId,
            @Field("item_id") int itemId,
            @Field("address") String address,
            @Field("notes") String notes
    );

    // 📄 Get all requests for a specific user
    @GET("requests/user/{user_id}")
    Call<List<Request>> getRequestsByUser(@Path("user_id") int userId);

    // 📄 Admin or general usage: get all requests
    @GET("requests")
    Call<List<Request>> getAllRequests();

    @GET("profile/{userId}")
    Call<Profile> getProfile(@Path("userId") int userId);

    @Multipart
    @POST("profile/update")
    Call<Void> updateProfile(
            @Part("user_id") RequestBody userId,
            @Part("first_name") RequestBody firstName,
            @Part("last_name") RequestBody lastName,
            @Part("secret") RequestBody secret,
            @Part("theme_bg") RequestBody themeBg,
            @Part("theme_col") RequestBody themeCol,
            @Part MultipartBody.Part image
    );

    @POST("/profile/save")
    Call<Void> saveProfile(@Body Profile profile);

    @GET("/profile/{user_id}")
    Call<Profile> getProfileByUserId(@Path("user_id") int userId);

    @POST("/api/profile/theme")
    Call<Void> updateThemeColors(@Body Profile profile); // Keep this one if your backend matches

}