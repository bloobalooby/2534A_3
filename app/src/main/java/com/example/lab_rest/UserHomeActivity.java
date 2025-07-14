package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity {

    // ───── UI elements ─────
    private TextView tvWelcome, tvTotalRecycled, tvTotalEarned, tvCompletedRequests, tvAnnouncements, tvBadgeName;
    private ImageView badgeBronze, badgeSilver, badgeGold;
    private Button btnSubmitRequest, btnViewRequests;

    // ───── Lifecycle ─────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // Bind UI
        tvWelcome        = findViewById(R.id.tvWelcome);
        tvAnnouncements  = findViewById(R.id.tvAnnouncements);
        badgeBronze      = findViewById(R.id.badgeBronze);
        badgeSilver      = findViewById(R.id.badgeSilver);
        badgeGold        = findViewById(R.id.badgeGold);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnViewRequests  = findViewById(R.id.btnViewRequests);
        tvBadgeName      = findViewById(R.id.tvBadgeName);
        View mapClickableArea = findViewById(R.id.mapClickableArea);

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



        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);

        // Check login
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Populate welcome message and stats
        User user = spm.getUser();
        tvWelcome.setText("Welcome, " + user.getUsername() + "!");
        loadUserAnnouncements(user.getId());

        // Dummy data for now
        double totalWeight = 12.5;
        double earned      = totalWeight * 0.30;
        int completed      = 5;

        TextView tvTotalRecycledBig = findViewById(R.id.tvTotalRecycledBig);
        TextView tvTotalEarnedBig   = findViewById(R.id.tvTotalEarnedBig);
        TextView tvTotalRequests    = findViewById(R.id.tvTotalRequests);

        tvTotalRecycledBig.setText(totalWeight + " kg");
        tvTotalEarnedBig.setText("RM " + String.format("%.2f", earned));
        tvTotalRequests.setText(String.valueOf(completed));

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

        // Buttons
        btnSubmitRequest.setOnClickListener(v ->
                startActivity(new Intent(this, SubmitRequestActivity.class)));

        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(this, MyRequestActivity.class)));
    }


    // ───── Options Menu (logout) ─────
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
            spm.logout();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ───── Load announcements ─────
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
