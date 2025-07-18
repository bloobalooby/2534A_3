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

    // Default selected theme
    private String selectedTheme = UserThemeManager.THEME_LIGHT;

    // Layout and buttons
    private LinearLayout themeLayout;
    private Button btnLight, btnDark, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);

        // Bind UI elements
        themeLayout = findViewById(R.id.themeLayout);
        btnLight = findViewById(R.id.btnLight);
        btnDark = findViewById(R.id.btnDark);
        btnSave = findViewById(R.id.btnSaveTheme);

        // Load saved theme from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        selectedTheme = prefs.getString("theme", UserThemeManager.THEME_LIGHT);
        applyTheme(selectedTheme); // Apply the loaded theme

        // Handle Light theme selection
        btnLight.setOnClickListener(v -> {
            selectedTheme = UserThemeManager.THEME_LIGHT;
            applyTheme(selectedTheme);
        });

        // Handle Dark theme selection
        btnDark.setOnClickListener(v -> {
            selectedTheme = UserThemeManager.THEME_DARK;
            applyTheme(selectedTheme);
        });

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("theme", selectedTheme);
            editor.apply(); // Save selected theme
            Toast.makeText(this, "Theme saved: " + selectedTheme, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Apply the selected theme by updating background and text colors.
     *
     * @param theme The selected theme string (light/dark)
     */
    private void applyTheme(String theme) {
        int bgColor = UserThemeManager.getBackgroundColor(theme);
        int textColor = UserThemeManager.getTextColor(theme);

        // Set background color
        themeLayout.setBackgroundColor(bgColor);

        // Update text color for all buttons
        btnLight.setTextColor(textColor);
        btnDark.setTextColor(textColor);
        btnSave.setTextColor(textColor);

        // Dynamically set background tint based on selected theme
        btnLight.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                theme.equals(UserThemeManager.THEME_LIGHT) ? Color.parseColor("#DDDDDD") : Color.parseColor("#555555")));

        btnDark.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                theme.equals(UserThemeManager.THEME_DARK) ? Color.parseColor("#555555") : Color.parseColor("#DDDDDD")));

        btnSave.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));

        // Update title text color
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setTextColor(textColor);
    }
}
