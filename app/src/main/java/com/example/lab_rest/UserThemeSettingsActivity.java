package com.example.lab_rest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;



public class UserThemeSettingsActivity extends AppCompatActivity {

    private String selectedTheme = UserThemeManager.THEME_LIGHT; // Default
    private LinearLayout themeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);

        themeLayout = findViewById(R.id.themeLayout);
        Button btnLight = findViewById(R.id.btnLight);
        Button btnDark = findViewById(R.id.btnDark);
        Button btnBlue = findViewById(R.id.btnBlue);
        Button btnSave = findViewById(R.id.btnSaveTheme);

        // Load saved theme
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        selectedTheme = prefs.getString("theme", UserThemeManager.THEME_LIGHT);
        applyTheme(selectedTheme);

        btnLight.setOnClickListener(v -> {
            selectedTheme = UserThemeManager.THEME_LIGHT;
            applyTheme(selectedTheme);
        });

        btnDark.setOnClickListener(v -> {
            selectedTheme = UserThemeManager.THEME_DARK;
            applyTheme(selectedTheme);
        });

        btnBlue.setOnClickListener(v -> {
            selectedTheme = UserThemeManager.THEME_BLUE;
            applyTheme(selectedTheme);
        });

        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("theme", selectedTheme);
            editor.apply();
            Toast.makeText(this, "Theme saved: " + selectedTheme, Toast.LENGTH_SHORT).show();
        });
    }

    private void applyTheme(String theme) {
        themeLayout.setBackgroundColor(UserThemeManager.getBackgroundColor(theme));
        // Optional: change text/button color here too
    }
}