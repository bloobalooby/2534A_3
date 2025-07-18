package com.example.lab_rest;

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
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminRequestActivity
 * Displays a list of all user requests for admin view.
 */
public class AdminRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // Retrieve user from Shared Preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String role = user.getRole();
        String apiKey = user.getToken();

        // 🔒 Check if user is admin or superadmin
        if (!"admin".equalsIgnoreCase(role) && !"superadmin".equalsIgnoreCase(role)) {
            Toast.makeText(this, "Access denied. You are not authorized.", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if unauthorized
            return;
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Retrofit service
        requestService = ApiUtils.getRequestService();

        // Fetch and load all requests
        loadRequests(apiKey, role);
    }

    /**
     * Loads all user requests from the backend and binds them to the RecyclerView.
     *
     * @param apiKey The API key/token for authentication.
     * @param role
     */
    private void loadRequests(String apiKey, String role) {
        requestService.getAllRequests(apiKey).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Populate RecyclerView with fetched data
                    List<Request> requestList = response.body();
                    adapter = new RequestAdapter(requestList,"admin");
                    recyclerView.setAdapter(adapter);

                    Toast.makeText(AdminRequestActivity.this,
                            "Loaded " + requestList.size() + " requests",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminRequestActivity.this,
                            "Failed to load requests. Code: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                // Display error on failure
                Toast.makeText(AdminRequestActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
