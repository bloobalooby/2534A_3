package com.example.lab_rest.sharedpref;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity {

    TextView tvWelcome, tvTotalRecycled, tvTotalEarned, tvCompletedRequests, tvAnnouncements;
    ImageView badgeBronze, badgeSilver, badgeGold;
    Button btnSubmitRequest, btnViewRequests, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // UI references
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotalRecycled = findViewById(R.id.tvTotalRecycled);
        tvTotalEarned = findViewById(R.id.tvTotalEarned);
        tvCompletedRequests = findViewById(R.id.tvCompletedRequests);
        tvAnnouncements = findViewById(R.id.tvAnnouncements); // ✅ FIX: declare it
        badgeBronze = findViewById(R.id.badgeBronze);
        badgeSilver = findViewById(R.id.badgeSilver);
        badgeGold = findViewById(R.id.badgeGold);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        User user = spm.getUser();
        tvWelcome.setText("Welcome, " + user.getUsername());

        // 🟢 Call the announcements loader
        loadUserAnnouncements(user.getId());

        // Example stats
        double totalWeight = 12.5;
        double earned = totalWeight * 0.30;
        int completed = 5;

        tvTotalRecycled.setText("♻️ Total Recycled: " + totalWeight + " kg");
        tvTotalEarned.setText("💰 Total Earned: RM " + earned);
        tvCompletedRequests.setText("✅ Completed Requests: " + completed);

        if (totalWeight >= 5) badgeBronze.setVisibility(View.VISIBLE);
        if (totalWeight >= 10) badgeSilver.setVisibility(View.VISIBLE);
        if (totalWeight >= 20) badgeGold.setVisibility(View.VISIBLE);

        btnSubmitRequest.setOnClickListener(v ->
                startActivity(new Intent(this, SubmitRequestActivity.class)));

        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(this, MyRequestActivity.class)));

        btnLogout.setOnClickListener(v -> {
            spm.logout();
            Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // ✅ Announcement logic based on request status
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
                                updates.append("🟡 Your recycle request has been successfully sent!\n\n");
                                break;
                            case "Accepted":
                                updates.append("✅ Your request has been accepted. Thank you for recycling!\n\n");
                                break;
                            case "Weighing Scheduled":
                                String note = (r.getNotes() != null) ? r.getNotes() : "between 8AM–5PM";
                                updates.append("📅 Appointment scheduled: ").append(note).append("\n\n");
                                break;
                            case "Declined":
                                updates.append("❌ Your recycle request was declined.\n\n");
                                break;
                            case "Completed":
                                updates.append("🎉 Your request has been completed. Thanks for your contribution!\n\n");
                                break;
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
