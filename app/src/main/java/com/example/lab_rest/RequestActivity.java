package com.example.lab_rest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.RequestAdapter;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userService = ApiUtils.getUserService();

        loadRequests();
    }

    private void loadRequests() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = "Bearer " + user.getToken(); // Adjust this based on your token format

        userService.getAllRequests(token).enqueue(new Callback<List<Request>>() {

            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // 🔍 Debug logs to see what's returned from the API
                    Log.d("RequestActivity", "Response Code: " + response.code());
                    Log.d("RequestActivity", "Response Body: " + new Gson().toJson(response.body()));

                    adapter = new RequestAdapter(response.body());
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.e("RequestActivity", "Response Error Code: " + response.code());
                    Toast.makeText(RequestActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Log.e("RequestActivity", "API Call Failed: " + t.getMessage());
                Toast.makeText(RequestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
