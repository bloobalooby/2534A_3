package com.example.lab_rest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity {

    private TextView tvWelcome, tvAnnouncements, tvBadgeName;
    private ImageView badgeBronze, badgeSilver, badgeGold;
    private Button btnSubmitRequest, btnViewRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved theme before UI draws
        SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        String theme = prefs.getString("app_theme", "light");

        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        MaterialToolbar toolbar = findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bind views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvAnnouncements = findViewById(R.id.tvAnnouncements);
        badgeBronze = findViewById(R.id.badgeBronze);
        badgeSilver = findViewById(R.id.badgeSilver);
        badgeGold = findViewById(R.id.badgeGold);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        tvBadgeName = findViewById(R.id.tvBadgeName);
        View mapClickableArea = findViewById(R.id.mapClickableArea);


        btnSubmitRequest.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserItemListActivity.class))
        );

        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserRequestActivity.class))
        );

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

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        User user = spm.getUser();
        tvWelcome.setText("Welcome, " + user.getUsername() + "!");
        loadUserAnnouncements(user.getId());

        UserService api = ApiUtils.getUserService();
        api.getRequestsByUser(user.getId()).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double totalWeight = 0;
                    double totalEarnings = 0;
                    int completedRequests = 0;

                    for (Request r : response.body()) {
                        if ("Accepted".equalsIgnoreCase(r.getStatus())) {
                            totalWeight += r.getWeight();
                            totalEarnings += r.getTotal_price();
                            completedRequests++;
                        }
                    }

                    // Badge logic
                    if (totalWeight >= 20) {
                        tvBadgeName.setText("Gold Badge");
                        badgeGold.setVisibility(View.VISIBLE);
                    } else if (totalWeight >= 10) {
                        tvBadgeName.setText("Silver Badge");
                        badgeSilver.setVisibility(View.VISIBLE);
                    } else if (totalWeight >= 5) {
                        tvBadgeName.setText("Bronze Badge");
                        badgeBronze.setVisibility(View.VISIBLE);
                    } else {
                        tvBadgeName.setText("No badge yet");
                    }

                } else {
                    Toast.makeText(UserHomeActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(UserHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            startActivity(new Intent(this, UserProfileInfoActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void loadUserAnnouncements(int userId) {
        UserService api = ApiUtils.getUserService();
        api.getRequestsByUser(userId).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder updates = new StringBuilder();
                    for (Request r : response.body()) {
                        switch (r.getStatus()) {
                            case "Pending":
                                updates.append("🟡 Your recycle request has been successfully sent!\n\n"); break;
                            case "Accepted":
                                updates.append("✅ Your request has been accepted. Thank you for recycling!\n\n"); break;
                            case "Weighing Scheduled":
                                String note = (r.getNotes() != null) ? r.getNotes() : "between 8 AM–5 PM";
                                updates.append("📅 Appointment scheduled: ").append(note).append("\n\n"); break;
                            case "Declined":
                                updates.append("❌ Your recycle request was declined.\n\n"); break;
                            case "Completed":
                                updates.append("🎉 Your request has been completed. Thanks for your contribution!\n\n"); break;
                            default:
                                updates.append("🔔 Status: ").append(r.getStatus()).append("\n\n");
                        }
                    }
                    tvAnnouncements.setText(updates.toString());
                } else {
                    tvAnnouncements.setText("No recent updates.");
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                tvAnnouncements.setText("Failed to load announcements.");
            }
        });
    }
}

