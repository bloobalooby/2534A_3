package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
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

        loadRequests(); // Call to fetch request data from API
    }

    private void loadRequests() {
        // Get the user and token from shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        if (user == null || user.getToken() == null || user.getToken().isEmpty()) {
            Toast.makeText(this, "Error: No valid token found. Please log in again.", Toast.LENGTH_LONG).show();
            // Optionally redirect to login page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String token = "Bearer " + user.getToken(); // Token format

        // Debug: Show the token for verification
        Toast.makeText(this, "Token: " + token, Toast.LENGTH_LONG).show();

        // Make API call
        userService.getAllRequests(token).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Request> requestList = response.body();
                    adapter = new RequestAdapter(requestList);
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(RequestActivity.this, "Loaded " + requestList.size() + " requests.", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(RequestActivity.this, "Unauthorized: Token expired or invalid. Please log in again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RequestActivity.this, "No requests found or server error. Code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(RequestActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
