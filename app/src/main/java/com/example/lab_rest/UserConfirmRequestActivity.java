package com.example.lab_rest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.ConfirmCartAdapter;
import com.example.lab_rest.model.Item;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConfirmRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextAddress, editTextNotes;
    private Button btnSubmit;
    private List<Item> cartItems;
    private UserService userService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        // Bind UI components
        recyclerView = findViewById(R.id.recyclerViewCart);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSubmit = findViewById(R.id.btnSubmitRequest);

        // Retrieve cart items from temporary cart
        cartItems = UserTempCart.getCartItems();

        // Set up RecyclerView with the cart items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ConfirmCartAdapter(this, cartItems));

        // Initialize shared preferences and API service
        sharedPrefManager = new SharedPrefManager(this);
        userService = ApiUtils.getUserService();

        // Handle submit button click
        btnSubmit.setOnClickListener(view -> {
            String address = editTextAddress.getText().toString().trim();
            String notes = editTextNotes.getText().toString().trim();

            // Validate required input
            if (address.isEmpty()) {
                editTextAddress.setError("Address is required");
                return;
            }

            // Show confirmation dialog before submitting
            new android.app.AlertDialog.Builder(UserConfirmRequestActivity.this)
                    .setTitle("Confirm Submission")
                    .setMessage("Are you sure you want to submit this request?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Get logged-in user's ID
                        int userId = sharedPrefManager.getUser().getId();

                        // Submit each cart item as a separate request
                        for (Item item : cartItems) {
                            sendRequest(userId, item.getItemId(), address, notes);
                        }

                        // Clear cart after submission
                        UserTempCart.clearCart();
                        Toast.makeText(UserConfirmRequestActivity.this,
                                "All requests submitted.", Toast.LENGTH_LONG).show();

                        // Optional: close this screen
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    /**
     * Sends a single request to the server for the given item and user details.
     */
    private void sendRequest(int userId, int itemId, String address, String notes) {
        Call<ResponseBody> call = userService.createRequest(userId, itemId, address, notes);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Request succeeded
                    Toast.makeText(UserConfirmRequestActivity.this,
                            "Request submitted for item ID: " + itemId,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Server returned an error
                    Toast.makeText(UserConfirmRequestActivity.this,
                            "Failed for item ID: " + itemId,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Network error or server not reachable
                Toast.makeText(UserConfirmRequestActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

