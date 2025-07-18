package com.example.lab_rest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.example.lab_rest.model.Profile;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ProfileService;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * UserProfileInfoActivity:
 * Displays user profile (first name, last name, image) and lets user choose app theme.
 */
public class UserProfileInfoActivity extends AppCompatActivity {

    // UI Components
    private EditText edtFirstName, edtLastName;
    private ImageView imgProfile;
    private Button btnLight, btnDark, btnSaveTheme;

    // Theme & API
    private String selectedTheme = "light"; // Default theme
    private String token;                   // Auth token
    private ProfileService profileService;
    private Profile loadedProfile = null;   // Current user's profile data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        // Setup toolbar with back navigation
        MaterialToolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize UI elements
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        btnLight = findViewById(R.id.btnLight);
        btnDark = findViewById(R.id.btnDark);
        btnSaveTheme = findViewById(R.id.btnSaveTheme);
        imgProfile = findViewById(R.id.imgProfile);

        // Retrieve logged-in user and token
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        int userId = user.getId();
        token = user.getToken();

        // Initialize API service
        profileService = ApiUtils.getProfileService();

        // Fetch profile by user ID
        profileService.getProfileByUserId(userId, token).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    loadedProfile = response.body().get(0);

                    // Set name values and disable editing
                    edtFirstName.setText(loadedProfile.getFirst_name());
                    edtLastName.setText(loadedProfile.getLast_name());
                    edtFirstName.setEnabled(false);
                    edtLastName.setEnabled(false);

                    // Load profile image (if available) using Glide
                    if (loadedProfile.getImage() != null && !loadedProfile.getImage().isEmpty()) {
                        String imageUrl = "https://178.128.220.20/2534A_3/api/" + loadedProfile.getImage();
                        Glide.with(UserProfileInfoActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .into(imgProfile);
                    }

                    // Set current theme selection
                    selectedTheme = loadedProfile.getTheme_bg();
                    if ("dark".equalsIgnoreCase(selectedTheme)) {
                        btnDark.setAlpha(1f);
                        btnLight.setAlpha(0.5f);
                    } else {
                        btnLight.setAlpha(1f);
                        btnDark.setAlpha(0.5f);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to load profile", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error loading profile", Toast.LENGTH_LONG).show();
            }
        });

        // Theme switching: Light Mode
        btnLight.setOnClickListener(v -> {
            selectedTheme = "light";
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Light mode
            saveThemePreference("light");
            btnLight.setAlpha(1f);
            btnDark.setAlpha(0.5f);
            recreate(); // Refresh UI
        });

        // Theme switching: Dark Mode
        btnDark.setOnClickListener(v -> {
            selectedTheme = "dark";
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Dark mode
            saveThemePreference("dark");
            btnDark.setAlpha(1f);
            btnLight.setAlpha(0.5f);
            recreate(); // Refresh UI
        });

        // Save theme button
        btnSaveTheme.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("ThemePrefs", MODE_PRIVATE).edit();
            editor.putString("app_theme", selectedTheme);
            editor.apply();
            Toast.makeText(UserProfileInfoActivity.this, "Theme saved locally!", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Stores theme preference locally in SharedPreferences.
     */
    private void saveThemePreference(String theme) {
        SharedPreferences.Editor editor = getSharedPreferences("ThemePrefs", MODE_PRIVATE).edit();
        editor.putString("app_theme", theme);
        editor.apply();
    }
}
