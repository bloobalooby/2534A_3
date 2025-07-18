package com.example.lab_rest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.RequestAdapter;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.RequestService;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRequestActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private UserService userService;
    private RequestService requestService;
    private List<Request> requestList;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        // 🔧 Initialize RecyclerView
        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));

        // 🌐 Initialize API services
        userService = ApiUtils.getUserService();
        requestService = ApiUtils.getRequestService();

        // 👤 Retrieve logged-in user from Shared Preferences
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        token = user.getToken(); // stored for reuse
        Log.d("DEBUG_USER", "User ID: " + user.getId());

        // 📡 Load user's requests from API
        loadUserRequests(user.getId());
    }

    /**
     * Loads requests made by a specific user and displays them in a RecyclerView.
     */
    private void loadUserRequests(int userId) {
        requestService.getRequestsByUser(token, userId).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    requestList = response.body();

                    // 📊 Sort requests: show "Pending" first
                    Collections.sort(requestList, new Comparator<Request>() {
                        @Override
                        public int compare(Request r1, Request r2) {
                            return r1.getStatus().compareToIgnoreCase(r2.getStatus());
                        }
                    });

                    // 📦 Set adapter and bind delete handler
                    adapter = new RequestAdapter(requestList, "user");
                    rvRequests.setAdapter(adapter);

                    adapter.setOnDeleteClickListener((requestId, position) -> {
                        deleteRequest(requestId, position);
                    });

                } else {
                    Toast.makeText(UserRequestActivity.this, "No requests found.", Toast.LENGTH_SHORT).show();
                    Log.e("DEBUG_REQUESTS", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(UserRequestActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                Log.e("DEBUG_REQUESTS", "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Deletes a request and updates the list.
     */
    private void deleteRequest(int requestId, int position) {
        requestService.deleteRequest(token, requestId).enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeleteResponse deleteResponse = response.body();

                    if (deleteResponse.getStatus() == 200){
                        Toast.makeText(UserRequestActivity.this, "Request deleted", Toast.LENGTH_SHORT).show();
                        requestList.remove(position);
                        adapter.notifyItemRemoved(position);
                    } else {
                        Toast.makeText(UserRequestActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                        Log.e("DELETE_FAIL", "Delete status: " + deleteResponse.getStatus());
                    }
                } else {
                    Toast.makeText(UserRequestActivity.this, "Delete Fail", Toast.LENGTH_SHORT).show();
                    Log.e("DELETE_FAIL", "HTTP Code: " + response.code());

                    try {
                        if (response.errorBody() != null) {
                            Log.e("DELETE_FAIL", "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(UserRequestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DELETE_FAIL", "onFailure: " + t.getMessage());
            }
        });
    }

}

