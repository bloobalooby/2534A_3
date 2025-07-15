package com.example.lab_rest;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmRequestActivity extends AppCompatActivity {

    private RecyclerView rvSelectedItems;
    private EditText edtNotes;
    private Button btnSubmitRequest;
    private List<Item> selectedItems;
    private UserService userService;
    private TextView tvOverlayMap, tvTotalPrice, tvNotePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        rvSelectedItems = findViewById(R.id.rvSelectedItems);
        edtNotes = findViewById(R.id.edtNotes);
        btnSubmitRequest = findViewById(R.id.btnStickySubmit);
        tvOverlayMap = findViewById(R.id.tvOverlayMap);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvNotePreview = findViewById(R.id.tvNotePreview);

        rvSelectedItems.setLayoutManager(new LinearLayoutManager(this));

        // Toolbar
        MaterialToolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        // 🧠 Get selected items from intent
        String json = getIntent().getStringExtra("selectedItems");
        Type listType = new TypeToken<List<Item>>() {}.getType();
        selectedItems = new Gson().fromJson(json, listType);

        SelectedItemAdapter adapter = new SelectedItemAdapter(selectedItems, updatedList -> {
            selectedItems = updatedList; // update local list
            updateTotalPrice(updatedList);
        });
        rvSelectedItems.setAdapter(adapter);


        // 💰 Calculate total price
        double total = 0;
        for (Item item : selectedItems) {
            total += item.getPrice(); // Make sure your Item model has getPrice()
        }
        tvTotalPrice.setText("Total: RM" + String.format("%.2f", total));

        // 🧾 Live update note preview
        edtNotes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String note = s.toString().trim();
                if (!note.isEmpty()) {
                    tvNotePreview.setText("Note: " + note);
                    tvNotePreview.setVisibility(TextView.VISIBLE);
                } else {
                    tvNotePreview.setVisibility(TextView.GONE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });


        // 🌍 Open HQ map
        tvOverlayMap.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=Recycle+HQ,+Melaka"));
            intent.setPackage("com.google.android.apps.maps");

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Google Maps not installed.", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Submit request
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        int userId = user.getId();

        userService = ApiUtils.getUserService();

        btnSubmitRequest.setOnClickListener(v -> {
            String notes = edtNotes.getText().toString().trim();
            String fullAddress = "Walk-in to HQ";

            for (Item item : selectedItems) {
                Call<Void> call = userService.submitRequest(userId, item.getItemId(), fullAddress, notes);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        // Optional: check response.isSuccessful()
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ConfirmRequestActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            new AlertDialog.Builder(this)
                    .setTitle("Success")
                    .setMessage("Request submitted!")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(this, UserHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity();
                    })
                    .show();
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Back button pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateTotalPrice(List<Item> items) {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText("Total: RM" + String.format("%.2f", total));
    }

}
