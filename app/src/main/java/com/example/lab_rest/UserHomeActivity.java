package com.example.lab_rest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.RequestService;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity {

    // UI components
    private TextView tvWelcome, tvAnnouncements;

    private Button btnSubmitRequest, btnViewRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme from preferences before layout is rendered
        SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        String theme = prefs.getString("app_theme", "light");

        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bind views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvAnnouncements = findViewById(R.id.tvAnnouncements);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        View mapClickableArea = findViewById(R.id.mapClickableArea);

        // Navigate to SubmitRequest screen
        btnSubmitRequest.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserItemListActivity.class))
        );

        // Navigate to ViewRequests screen
        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserRequestActivity.class))
        );

        // Open Google Maps on HQ coordinates
        mapClickableArea.setOnClickListener(v -> {
            String hqLocation = "https://www.google.com/maps/search/?api=1&query=3.1390,101.6869";
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(hqLocation));
            intent.setPackage("com.google.android.apps.maps");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(hqLocation)));
            }
        });

        // Get logged-in user
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Display welcome message
        User user = spm.getUser();
        tvWelcome.setText("Welcome, " + user.getUsername() + "!");

        // Load announcements and user badge progress
        loadUserAnnouncements(user.getToken(), user.getId());
    }



    /**
     * Load recent user-related announcements based on request statuses.
     */
    private void loadUserAnnouncements(String token, int userId) {
        RequestService api = ApiUtils.getRequestService();
        api.getRequestsByUser(token, userId).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Get the most recent request
                    Request latest = response.body().get(0); // assumes list is sorted (latest first)
                    String status = latest.getStatus();
                    String note = latest.getNotes();
                    String message;
                    int color;

                    // Set message and background color based on status
                    switch (status) {
                        case "Pending":
                            message = "🟡 Your recycle request is pending.";
                            color = getResources().getColor(android.R.color.holo_orange_light);
                            break;
                        case "Accepted":
                            message = "✅ Your request has been accepted. Thank you!";
                            color = getResources().getColor(android.R.color.holo_green_light);
                            break;
                        case "Declined":
                            message = "❌ Your recycle request was declined.";
                            color = getResources().getColor(android.R.color.holo_red_light);
                            break;
                        case "Completed":
                            message = "🎉 Your request has been completed. Well done!";
                            color = getResources().getColor(android.R.color.holo_green_dark);
                            break;
                        case "Weighing Scheduled":
                            message = "📅 Scheduled: " + (note != null ? note : "between 8 AM–5 PM.");
                            color = getResources().getColor(android.R.color.holo_blue_light);
                            break;
                        default:
                            message = "🔔 Status: " + status;
                            color = getResources().getColor(android.R.color.darker_gray);
                            break;
                    }

                    // Update the view
                    tvAnnouncements.setText(message);
                    tvAnnouncements.setBackgroundColor(color);
                } else {
                    tvAnnouncements.setText("📭 You haven't submitted any requests yet.");
                    tvAnnouncements.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                tvAnnouncements.setText("⚠️ Failed to load status. Please try again.");
                tvAnnouncements.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        });
    }



    /**
     * Inflate options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
        return true;
    }

    /**
     * Handle toolbar item selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            startActivity(new Intent(this, UserProfileInfoActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show logout confirmation dialog.
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
