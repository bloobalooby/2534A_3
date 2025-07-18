package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.ItemAdapter;
import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ItemService;
import com.example.lab_rest.remote.RequestService;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserItemListActivity extends AppCompatActivity {

    // UI components
    private RecyclerView rvItemList;
    private Button btnDone, btnClear;
    private EditText etAddress, etNotes;
    private FloatingActionButton fab;

    // Data & services
    private List<Item> itemList = new ArrayList<>();
    private ItemAdapter adapter;
    private ItemService itemService;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Setup top toolbar
        MaterialToolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(v -> finish());

        // Handle full screen insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Bind views
        rvItemList = findViewById(R.id.rvItemList);
        btnDone = findViewById(R.id.btnDone);
        btnClear = findViewById(R.id.btnClear);
        etAddress = findViewById(R.id.etAddress);
        etNotes = findViewById(R.id.etNotes);

        // Initialize services
        itemService = ApiUtils.getItemService();
        requestService = ApiUtils.getRequestService();

        // Get logged in user details
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();
        int userId = user.getId();

        // Fetch all available items from server
        itemService.getAllItems(token).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList = response.body();

                    // Set image resources for each item
                    for (Item item : itemList) {
                        int resId = getImageResIdFromName(item.getItemName());
                        item.setImageResId(resId);
                    }

                    // Setup RecyclerView
                    adapter = new ItemAdapter(getApplicationContext(), itemList);
                    rvItemList.setAdapter(adapter);
                    rvItemList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvItemList.addItemDecoration(new DividerItemDecoration(rvItemList.getContext(), DividerItemDecoration.VERTICAL));
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Server error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("UserItemList", "Error: ", t);
            }
        });

        // Handle submit request action
        btnDone.setOnClickListener(v -> {
            Item selectedItem = adapter.getSelectedItem();

            if (selectedItem == null) {
                Toast.makeText(this, "Please select an item first.", Toast.LENGTH_SHORT).show();
                return;
            }

            String address = etAddress.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (address.isEmpty()) {
                etAddress.setError("Address is required");
                etAddress.requestFocus();
                return;
            }

            // Submit request to server
            Call<com.example.lab_rest.model.Request> call = requestService.createRequest(
                    token,
                    userId,
                    selectedItem.getItemId(),
                    address,
                    notes
            );

            call.enqueue(new Callback<com.example.lab_rest.model.Request>() {
                @Override
                public void onResponse(Call<com.example.lab_rest.model.Request> call, Response<com.example.lab_rest.model.Request> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UserItemListActivity.this, "Request submitted!", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity after submission
                    } else {
                        Toast.makeText(UserItemListActivity.this, "Submit failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.lab_rest.model.Request> call, Throwable t) {
                    Toast.makeText(UserItemListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Handle clear/reset button
        btnClear.setOnClickListener(v -> {
            for (Item item : itemList) {
                item.setSelected(false);
            }
            adapter.notifyDataSetChanged();
            etAddress.setText("");
            etNotes.setText("");
        });
    }

    // Clear session data and redirect to login screen
    private void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Map item names to drawable resources
    private int getImageResIdFromName(String itemName) {
        switch (itemName.toLowerCase()) {
            case "plastic bottles": return R.drawable.ic_plastic;
            case "paper": return R.drawable.ic_paper;
            case "cardboard": return R.drawable.ic_cardboard;
            case "aluminum cans": return R.drawable.ic_aluminum;
            case "glass containers": return R.drawable.ic_glass;
            case "used cooking oil": return R.drawable.ic_oil;
            case "old clothes": return R.drawable.ic_cardboard;
            case "metal scraps": return R.drawable.ic_metalscraps;
            default: return R.drawable.ic_launcher_foreground;
        }
    }
}
