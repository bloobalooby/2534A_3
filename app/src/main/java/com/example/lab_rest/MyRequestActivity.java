package com.example.lab_rest;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        userService = ApiUtils.getUserService();

        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();

        loadUserRequests(user.getId());
    }

    private void loadUserRequests(int userId) {
        userService.getRequestsByUser(userId).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new RequestAdapter(response.body());
                    rvRequests.setAdapter(adapter);
                } else {
                    Toast.makeText(MyRequestActivity.this, "No requests found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(MyRequestActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

