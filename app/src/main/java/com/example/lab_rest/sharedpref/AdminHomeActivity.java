package com.example.lab_rest.sharedpref;

import android.content.Intent;
import android.graphics.Insets;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.R;
import com.example.lab_rest.sharedpref.RequestActivity;
import com.example.lab_rest.model.User;


public class AdminHomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnRequests, btnItem, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // UI references
        tvWelcome = findViewById(R.id.tvWelcome);
        btnRequests = findViewById(R.id.btnRequests);
        btnItem = findViewById(R.id.btnItem);
        btnLogout = findViewById(R.id.btnLogout);

        // Load user info
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            finish();
            // forward to login page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            User user = spm.getUser();
            tvWelcome.setText("Hello " + user.getUsername());
        }
    }

    public void logoutClicked(View view) {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    public void itemListClicked(View view) {
        // forward user to ItemListActivity
        Intent intent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(intent);
    }

    public void allRequestsClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
        startActivity(intent);
    }

    }

