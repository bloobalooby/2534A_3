package com.example.lab_rest;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.sharedpref.SharedPrefManager;

public class UpdateItemActivity extends AppCompatActivity {
    private EditText txtItemId; // variable declaration
    private EditText txtItemName;
    private EditText txtPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get view objects references
        txtItemId = findViewById(R.id.txtItemId);
        txtItemName = findViewById(R.id.txtItemName);
        txtPrice = findViewById(R.id.txtPrice);

        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            String price = intent.getStringExtra("price");

            if (id != null) txtItemId.setText(id);
            if (name != null) txtItemName.setText(name);
            if (price != null) txtPrice.setText(price);
        }

    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}