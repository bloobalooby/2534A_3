package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.FailLogin;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private ProgressBar progressBar;
    private CheckBox checkboxShowPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get references to form elements
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        //show and hide password
        checkboxShowPassword = findViewById(R.id.checkboxShowPassword);

        checkboxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            edtPassword.setSelection(edtPassword.getText().length()); // keep cursor at end
        });

        //progress bar after input username and password
        progressBar = findViewById(R.id.progressBar);

    }

    /**
     * Login button action handler
     */
    public void loginClicked(View view) {

        // get username and password entered by user
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        // validate form, make sure it is not empty
        if (validateLogin(username, password)) {
            // if not empty, login using REST API
            doLogin(username, password);
        }

    }

    /**
     * Call REST API to login
     *
     * @param username username
     * @param password password
     */
    private void doLogin(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);  // Show spinner

        UserService userService = ApiUtils.getUserService();
        Call<User> call;

        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call call, Response response) {
                progressBar.setVisibility(View.GONE); // ⬅️ Hide spinner after receiving response

                if (response.isSuccessful()) {
                    User user = (User) response.body();
                    if (user != null && user.getToken() != null) {
                        displayToast("Login successful");
                        displayToast("Token: " + user.getToken());

                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        Intent intent;
                        String role = user.getRole();

                        if ("admin".equalsIgnoreCase(role) || "superadmin".equalsIgnoreCase(role)) {
                            intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                        } else {
                            intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                        }

                        finish();
                        startActivity(intent);
                    } else {
                        displayToast("Login error");
                    }
                } else {
                    try {
                        String errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("MyApp:", e.toString());
                        displayToast("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE); // ⬅️ Hide spinner on failure too
                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
                Log.e("MyApp:", t.toString());
            }
        });
    }


    /**
     * Validate value of username and password entered. Client side validation.
     * @param username
     * @param password
     * @return
     */

    // if the user forgot any of username/password
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Display a Toast message
     * @param message message to be displayed inside toast
     */
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}