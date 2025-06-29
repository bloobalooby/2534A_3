package com.example.lab_rest.sharedpref;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestActivity extends AppCompatActivity {

    RecyclerView rvRequests;
    UserService api;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        user = spm.getUser();

        api = ApiUtils.getUserService();

        loadRequests();
    }

    private void loadRequests() {
        api.getRequestsByUser(user.getId()).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyRequestAdapter adapter = new MyRequestAdapter(MyRequestActivity.this, response.body(), request -> {
                        cancelRequest(request.getRequest_id());
                    });
                    rvRequests.setAdapter(adapter);
                } else {
                    Toast.makeText(MyRequestActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(MyRequestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelRequest(int requestId) {
        // you can modify this to send cancel request via API
        Toast.makeText(this, "Cancel request ID: " + requestId, Toast.LENGTH_SHORT).show();
        // TODO: call cancel API if available
    }
}
