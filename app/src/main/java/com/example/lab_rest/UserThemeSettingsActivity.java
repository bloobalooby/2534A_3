package com.example.lab_rest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class UserThemeSettingsActivity extends AppCompatActivity {

    private String selectedTheme = UserThemeManager.THEME_LIGHT; // Default
    private LinearLayout themeLayout;

    //  Declare buttons at class level so they can be used in applyTheme()
    private Button btnLight, btnDark, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);

        // Initialize layout and buttons
        themeLayout = findViewById(R.id.themeLayout);
        btnLight = findViewById(R.id.btnLight);
        btnDark = findViewById(R.id.btnDark);
        btnSave = findViewById(R.id.btnSaveTheme);

        // Load saved theme
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        selectedTheme = prefs.getString("theme", UserThemeManager.THEME_LIGHT);
        applyTheme(selectedTheme);

        // Theme switch logic
        btnLight.setOnClickListener(v -> {
            selectedTheme = UserThemeManager.THEME_LIGHT;
            applyTheme(selectedTheme);
        });

        btnDark.setOnClickListener(v -> {
            selectedTheme = UserThemeManager.THEME_DARK;
            applyTheme(selectedTheme);
        });

        // Save button logic
        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("theme", selectedTheme);
            editor.apply();
            Toast.makeText(this, "Theme saved: " + selectedTheme, Toast.LENGTH_SHORT).show();
        });
    }

    // Apply selected theme to layout and buttons
    private void applyTheme(String theme) {
        int bgColor = UserThemeManager.getBackgroundColor(theme);
        int textColor = UserThemeManager.getTextColor(theme);

        themeLayout.setBackgroundColor(bgColor);

        // Set text color
        btnLight.setTextColor(textColor);
        btnDark.setTextColor(textColor);
        btnSave.setTextColor(textColor);

        // Set background tint dynamically to match theme
        btnLight.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                theme.equals(UserThemeManager.THEME_LIGHT) ? Color.parseColor("#DDDDDD") : Color.parseColor("#555555")));
        btnDark.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                theme.equals(UserThemeManager.THEME_DARK) ? Color.parseColor("#555555") : Color.parseColor("#DDDDDD")));
        btnSave.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));

        // Apply text color to title
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setTextColor(textColor);
    }

}
