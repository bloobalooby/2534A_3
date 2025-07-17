package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.RequestAdapter;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.RequestService;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private RequestService requestService; // 🟢 Using RequestService now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🟢 Get token from shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String apiKey = user.getToken();  // NO "Bearer" prefix

        // 🟢 Initialize the RequestService
        requestService = ApiUtils.getRequestService();

        // 🟢 Load data from API
        loadRequests(apiKey);
    }

    private void loadRequests(String apiKey) {
        requestService.getAllRequests(apiKey).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Request> requestList = response.body();
                    adapter = new RequestAdapter(requestList);
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(AdminRequestActivity.this, "Loaded " + requestList.size() + " requests", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminRequestActivity.this, "Failed: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(AdminRequestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
