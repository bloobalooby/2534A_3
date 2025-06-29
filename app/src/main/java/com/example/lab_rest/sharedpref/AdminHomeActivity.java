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

public class AdminHomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnViewRequests, btnNewItem, btnUpdateItem, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // UI references
        tvWelcome = findViewById(R.id.tvWelcome);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnNewItem = findViewById(R.id.btnNewItem);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);
        btnLogout = findViewById(R.id.btnLogout);

        // Load user info
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        User user = spm.getUser();
        tvWelcome.setText("Welcome, " + user.getUsername());

        // Buttons
        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(this, RequestActivity.class)));

        btnNewItem.setOnClickListener(v ->
                startActivity(new Intent(this, AddItemActivity.class)));

        btnUpdateItem.setOnClickListener(view ->
                startActivity(new Intent(this, UpdateItemActivity.class)));

        btnLogout.setOnClickListener(v -> {
            spm.logout();
            Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}