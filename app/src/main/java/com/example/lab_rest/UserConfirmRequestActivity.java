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
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSubmit = findViewById(R.id.btnSubmitRequest);

        // Initialize shared preferences and API service
        sharedPrefManager = new SharedPrefManager(this);
        userService = ApiUtils.getUserService();

        // Get cart items from static cart
        cartItems = com.example.lab_rest.UserTempCart.getCartItems();

        // Display the cart in RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ConfirmCartAdapter(this, cartItems));

        // Handle submit button click
        btnSubmit.setOnClickListener(view -> {
            String address = editTextAddress.getText().toString().trim();
            String notes = editTextNotes.getText().toString().trim();

            // Validate input
            if (address.isEmpty()) {
                editTextAddress.setError("Address is required");
                return;
            }

            if (cartItems == null || cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirm submission
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Confirm Submission")
                    .setMessage("Submit " + cartItems.size() + " item(s)?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int userId = sharedPrefManager.getUser().getId();

                        for (Item item : cartItems) {
                            sendRequest(userId, item.getItemId(), address, notes);
                        }

                        // Clear cart after sending
                        com.example.lab_rest.UserTempCart.clearCart();

                        Toast.makeText(this,
                                "All requests submitted.", Toast.LENGTH_LONG).show();
                        finish(); // Close activity
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void sendRequest(int userId, int itemId, String address, String notes) {
        Call<ResponseBody> call = userService.createRequest(userId, itemId, address, notes);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserConfirmRequestActivity.this,
                            "Submitted for item ID: " + itemId,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserConfirmRequestActivity.this,
                            "Failed for item ID: " + itemId,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UserConfirmRequestActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
