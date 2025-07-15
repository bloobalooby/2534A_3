package com.example.lab_rest.remote;

import com.example.lab_rest.model.Profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfileService {

    @FormUrlEncoded
    @POST("profiles") // or "profiles.php" depending on your backend
    Call<Void> saveProfile(
            @Field("user_id") int userId,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("api_key") String apiKey
    );


    // 📥 Get profile by user ID with api_key
    @GET("profiles")
    Call<List<Profile>> getProfileByUserId(
            @Query("user_id[in]") int userId,
            @Query("api_key") String apiKey
    );

}
