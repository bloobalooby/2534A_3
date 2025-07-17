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

public interface RequestService {
    @GET("requests")
    Call<List<Request>> getAllRequests(@Header("api-key") String api_key);

    @GET("requests/{id}")
    Call<Request> getRequests(@Header("api-key") String api_key, @Path("id") int id);






}
