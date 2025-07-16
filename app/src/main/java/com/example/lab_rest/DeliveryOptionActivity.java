package com.example.lab_rest;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_rest.model.Item;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryOptionActivity extends AppCompatActivity {

    private RadioGroup rgDeliveryOption;
    private LinearLayout layoutHomePickup;
    private EditText edtAddress, edtCity, edtPostcode;
    private Button btnConfirmDelivery;

    private List<Item> selectedItems;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_option);

        // UI Bindings
        rgDeliveryOption = findViewById(R.id.rgDeliveryOption);
        layoutHomePickup = findViewById(R.id.layoutHomePickup);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtCity);
        edtPostcode = findViewById(R.id.edtPostcode);
        btnConfirmDelivery = findViewById(R.id.btnConfirmDelivery);
        FrameLayout mapClickableArea = findViewById(R.id.mapClickableArea); // ✅ Correct type


        userService = ApiUtils.getUserService();

        //  Get selected items + note from intent
        String json = getIntent().getStringExtra("selectedItems");
        String notes = getIntent().getStringExtra("note");
        Type listType = new TypeToken<List<Item>>() {
        }.getType();
        selectedItems = new Gson().fromJson(json, listType);

        //  Toggle address form visibility
        rgDeliveryOption.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbHomePickup) {
                layoutHomePickup.setVisibility(LinearLayout.VISIBLE);
                mapClickableArea.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbDeliverToHQ) {
                layoutHomePickup.setVisibility(LinearLayout.GONE);
                mapClickableArea.setVisibility(View.VISIBLE);
            }
        });

        mapClickableArea.setOnClickListener(v -> {
            String hqLocation = "https://www.google.com/maps/search/?api=1&query=2.2004,102.2405";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hqLocation));
            intent.setPackage("com.google.android.apps.maps");

            try {
                startActivity(intent);
            } catch (Exception e) {
                // Fallback if Maps app not installed
                Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(hqLocation));
                startActivity(fallback);
            }
        });


        //  Submit Request
        btnConfirmDelivery.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you the user making this request?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Proceed with validation and submission
                        int selectedId = rgDeliveryOption.getCheckedRadioButtonId();
                        String address;

                        if (selectedId == R.id.rbHomePickup) {
                            String a = edtAddress.getText().toString().trim();
                            String c = edtCity.getText().toString().trim();
                            String p = edtPostcode.getText().toString().trim();

                            if (a.isEmpty() || c.isEmpty() || p.isEmpty()) {
                                Toast.makeText(this, "Please fill in all address fields.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            address = a + ", " + c + ", " + p;
                        } else if (selectedId == R.id.rbDeliverToHQ) {
                            address = "Deliver to HQ";
                        } else {
                            Toast.makeText(this, "Please select a delivery option", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        int userId = spm.getUser().getId();

                        String requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                .format(new java.util.Date());

                        for (Item item : selectedItems) {
                            if (item.getQuantity() > 0) {
                                double totalPrice = item.getQuantity() * item.getPrice();
                                int itemId = item.getItemId();

                                Call<Void> call = userService.submitRequest(userId, itemId, address, requestDate, totalPrice, notes);
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("API_RESPONSE", "Code: " + response.code());
                                        if (!response.isSuccessful()) {
                                            Log.e("API_RESPONSE", "Error: " + response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("API_ERROR", "Throwable: " + t.getMessage());
                                    }
                                });
                            }
                        }

                        new AlertDialog.Builder(this)
                                .setTitle("Success")
                                .setMessage("Your request has been submitted!")
                                .setCancelable(false)
                                .setPositiveButton("OK", (d, w) -> {
                                    Intent intent = new Intent(this, UserHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Go back to ConfirmRequestActivity
                        Intent backIntent = new Intent(this, ConfirmRequestActivity.class);
                        backIntent.putExtra("selectedItems", new Gson().toJson(selectedItems));
                        backIntent.putExtra("note", getIntent().getStringExtra("note"));
                        finish();
                        startActivity(backIntent);
                    })
                    .show();
        });

    }
}