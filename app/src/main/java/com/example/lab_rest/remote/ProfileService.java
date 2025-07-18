package com.example.lab_rest.remote;

import com.example.lab_rest.model.Profile;

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

public interface ProfileService {


    //  Get profile by user ID with api_key
    @GET("profiles")
    Call<List<Profile>> getProfileByUserId(
            @Query("user_id[in]") int userId,
            @Query("api_key") String apiKey
    );


}
