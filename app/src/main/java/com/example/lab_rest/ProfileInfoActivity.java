package com.example.lab_rest;

import static androidx.core.graphics.drawable.DrawableCompat.applyTheme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.lab_rest.ThemeManager;
import com.example.lab_rest.model.Profile;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileInfoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgProfile;
    private EditText edtFirstName, edtLastName, edtSecret;
    private Button btnChooseImage, btnSave;
    private Uri imageUri = null;

    private UserService userService;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        imgProfile = findViewById(R.id.imgProfile);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtSecret = findViewById(R.id.edtSecret);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSaveProfileInfo);

        userService = ApiUtils.getUserService();
        spm = new SharedPrefManager(this);
        int userId = spm.getUser().getId();

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> {
            String fname = edtFirstName.getText().toString().trim();
            String lname = edtLastName.getText().toString().trim();
            String secret = edtSecret.getText().toString().trim();
            String imagePath = (imageUri != null) ? imageUri.toString() : "";

            Profile profile = new Profile(0);
            profile.setUser_id(userId);
            profile.setFirst_name(fname);
            profile.setLast_name(lname);
            profile.setSecret(secret);
            profile.setImage(imagePath);

            Call<Void> call = userService.saveProfile(profile);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(ProfileInfoActivity.this, "Profile info saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProfileInfoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Load existing profile
        loadProfile(userId);



// Theme selection
        Button btnLight = findViewById(R.id.btnLight);
        Button btnDark = findViewById(R.id.btnDark);
        Button btnBlue = findViewById(R.id.btnBlue);
        Button btnSaveTheme = findViewById(R.id.btnSaveTheme);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String[] selectedTheme = { prefs.getString("theme", ThemeManager.THEME_LIGHT) };

// Apply current theme immediately
        applyTheme(selectedTheme[0]);

        btnLight.setOnClickListener(v -> {
            selectedTheme[0] = ThemeManager.THEME_LIGHT;
            applyTheme(selectedTheme[0]);
        });

        btnDark.setOnClickListener(v -> {
            selectedTheme[0] = ThemeManager.THEME_DARK;
            applyTheme(selectedTheme[0]);
        });

        btnBlue.setOnClickListener(v -> {
            selectedTheme[0] = ThemeManager.THEME_BLUE;
            applyTheme(selectedTheme[0]);
        });

        btnSaveTheme.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("theme", selectedTheme[0]);
            editor.apply();
            Toast.makeText(ProfileInfoActivity.this, "Theme saved!", Toast.LENGTH_SHORT).show();
        });

    }

    private void loadProfile(int userId) {
        Call<Profile> call = userService.getProfileByUserId(userId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profile profile = response.body();
                    edtFirstName.setText(profile.getFirst_name());
                    edtLastName.setText(profile.getLast_name());
                    edtSecret.setText(profile.getSecret());

                    if (profile.getImage() != null && !profile.getImage().isEmpty()) {
                        imgProfile.setImageURI(Uri.parse(profile.getImage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Toast.makeText(ProfileInfoActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void applyTheme(String theme) {
        findViewById(android.R.id.content).getRootView()
                .setBackgroundColor(ThemeManager.getBackgroundColor(theme));
    }

}
