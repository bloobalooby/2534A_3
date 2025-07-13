package com.example.lab_rest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmRequestActivity extends AppCompatActivity {

    private RecyclerView rvSelectedItems;
    private EditText edtAddress, edtPostalCode, edtNotes;
    private Spinner spinnerCity;
    private Button btnSubmitRequest;
    private List<Item> selectedItems;
    private UserService userService;

    private final String[] cities = {
            "Alor Gajah", "Ayer Keroh", "Ayer Molek", "Batu Berendam", "Bemban",
            "Bukit Baru", "Bukit Rambai", "Jasin", "Klebang Besar", "Kuala Sungai Baru",
            "Masjid Tanah", "Melaka", "Pulau Sebang", "Sungai Udang"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        rvSelectedItems = findViewById(R.id.rvSelectedItems);
        edtAddress = findViewById(R.id.edtAddress);
        edtPostalCode = findViewById(R.id.edtPostcode);
        edtNotes = findViewById(R.id.edtNotes);
        spinnerCity = findViewById(R.id.spinnerCity);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        rvSelectedItems.setLayoutManager(new LinearLayoutManager(this));

        // Spinner setup
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        spinnerCity.setAdapter(cityAdapter);

        // Get selected items
        String json = getIntent().getStringExtra("selectedItems");
        Type listType = new TypeToken<List<Item>>() {}.getType();
        selectedItems = new Gson().fromJson(json, listType);

        SelectedItemAdapter adapter = new SelectedItemAdapter(selectedItems);
        rvSelectedItems.setAdapter(adapter);

        // User session
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        int userId = user.getId();

        userService = ApiUtils.getUserService();

        btnSubmitRequest.setOnClickListener(v -> {
            String address = edtAddress.getText().toString().trim();
            String city = spinnerCity.getSelectedItem().toString();
            String state = "Melaka";
            String postalCode = edtPostalCode.getText().toString().trim();
            String notes = edtNotes.getText().toString().trim();

            if (TextUtils.isEmpty(address)) {
                edtAddress.setError("Address required");
                return;
            }
            if (TextUtils.isEmpty(postalCode)) {
                edtPostalCode.setError("Postal code required");
                return;
            }
            if (!TextUtils.isDigitsOnly(postalCode)) {
                edtPostalCode.setError("Only numbers allowed");
                return;
            }

            String fullAddress = address + ", " + postalCode + " " + city + ", " + state;

            for (Item item : selectedItems) {
                Call<Void> call = userService.submitRequest(userId, item.getItemId(), fullAddress, notes);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {}
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
}

