package com.example.lab_rest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.RequestAdapter;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRequestActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        // 🔄 Initialize RecyclerView and service
        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        userService = ApiUtils.getUserService();

        // 👤 Get current user info from shared preferences
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        Log.d("DEBUG_USER", "User ID: " + user.getId());

        // 🚀 Load user's requests from API
        loadUserRequests(user.getToken(), user.getId());
    }

    /**
     * Fetches request data made by a specific user from the server and displays it in a RecyclerView
     */
    private void loadUserRequests(String token, int userId) {
        userService.getRequestsByUser(token, userId).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Request> requestList = response.body();

                    Log.d("DEBUG_REQUESTS", "Total request: " + requestList.size());
                    for (Request r : requestList) {
                        Log.d("DEBUG_REQUESTS", "Item ID: " + r.getItem_id() + " | Status: " + r.getStatus());
                    }

                    // ✅ Set adapter with retrieved request data
                    adapter = new RequestAdapter(requestList);
                    rvRequests.setAdapter(adapter);
                } else {
                    // ❌ API call succeeded but no data or bad response
                    Log.e("DEBUG_REQUESTS", "Request null or fail. Code: " + response.code());
                    Toast.makeText(UserRequestActivity.this, "No requests found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                // ❌ Network error or unexpected failure
                Log.e("DEBUG_REQUESTS", "Error: " + t.getMessage());
                Toast.makeText(UserRequestActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


