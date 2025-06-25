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
import com.example.lab_rest.model.User;

public class UserHomeActivity extends AppCompatActivity {

    TextView tvWelcome, tvTotalRecycled, tvTotalEarned, tvCompletedRequests;
    ImageView badgeBronze, badgeSilver, badgeGold;
    Button btnSubmitRequest, btnViewRequests, btnViewAnnouncement, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // UI references
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotalRecycled = findViewById(R.id.tvTotalRecycled);
        tvTotalEarned = findViewById(R.id.tvTotalEarned);
        tvCompletedRequests = findViewById(R.id.tvCompletedRequests);
        badgeBronze = findViewById(R.id.badgeBronze);
        badgeSilver = findViewById(R.id.badgeSilver);
        badgeGold = findViewById(R.id.badgeGold);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnViewAnnouncement = findViewById(R.id.btnViewAnnouncement);
        btnLogout = findViewById(R.id.btnLogout);

        // Load user info
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        User user = spm.getUser();
        tvWelcome.setText("Welcome, " + user.getUsername());

        // Placeholder stats (can be loaded via API later)
        double totalWeight = 12.5;
        double earned = totalWeight * 0.30;
        int completed = 5;

        tvTotalRecycled.setText("♻️ Total Recycled: " + totalWeight + " kg");
        tvTotalEarned.setText("💰 Total Earned: RM " + earned);
        tvCompletedRequests.setText("✅ Completed Requests: " + completed);

        if (totalWeight >= 5) badgeBronze.setVisibility(View.VISIBLE);
        if (totalWeight >= 10) badgeSilver.setVisibility(View.VISIBLE);
        if (totalWeight >= 20) badgeGold.setVisibility(View.VISIBLE);

        // Buttons
        btnSubmitRequest.setOnClickListener(v ->
                startActivity(new Intent(this, SubmitRequestActivity.class)));

        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(this, MyRequestActivity.class)));

        btnViewAnnouncement.setOnClickListener(v ->
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            spm.logout();
            Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
