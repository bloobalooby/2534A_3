package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_rest.model.User;
import com.example.lab_rest.sharedpref.SharedPrefManager;

public class AdminHomeActivity extends AppCompatActivity {

    // Declare UI components
    TextView tvWelcome;
    Button btnRequests, btnItem, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Initialize UI components
        tvWelcome = findViewById(R.id.tvWelcome);
        btnRequests = findViewById(R.id.btnRequests);
        btnItem = findViewById(R.id.btnItem);
        btnLogout = findViewById(R.id.btnLogout);

        // Load user from shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());

        // If not logged in, redirect to login
        if (!spm.isLoggedIn()) {
            finish(); // End current activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Show welcome message with username
            User user = spm.getUser();
            tvWelcome.setText("Hello " + user.getUsername());
        }
    }

    // Called when Logout button is clicked
    public void logoutClicked(View view) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());

        // Clear session
        spm.logout();

        // Show logout message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // End this activity and go back to LoginActivity
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Called when Item List button is clicked
    public void itemListClicked(View view) {
        // Go to AdminItemListActivity
        Intent intent = new Intent(getApplicationContext(), AdminItemListActivity.class);
        startActivity(intent);
    }

    // Called when View All Requests button is clicked
    public void allRequestsClicked(View view) {
        // Go to AdminRequestActivity
        Intent intent = new Intent(getApplicationContext(), AdminRequestActivity.class);
        startActivity(intent);
    }
}
