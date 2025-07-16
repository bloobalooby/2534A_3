package com.example.lab_rest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_rest.model.RecyclableItem;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ItemService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity {

    private EditText etItemName, etPrice;
    private Button btnAdd;
    private ItemService itemService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etItemName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etPrice);
        btnAdd = findViewById(R.id.btnAddItem);

        itemService = ApiUtils.getItemService();

        btnAdd.setOnClickListener(v -> {
            String itemName = etItemName.getText().toString().trim();
            String priceText = etPrice.getText().toString().trim();

            if (itemName.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
            User user = spm.getUser();
            String token = user.getToken();

            Call<RecyclableItem> call = itemService.addItem(token, itemName, price);
            call.enqueue(new Callback<RecyclableItem>() {
                @Override
                public void onResponse(Call<RecyclableItem> call, Response<RecyclableItem> response) {
                    if (response.isSuccessful() || response.code() == 201) {
                        Toast.makeText(AddItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (response.code() == 401) {
                        Toast.makeText(AddItemActivity.this, "Unauthorized (401): Invalid API Key", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddItemActivity.this, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RecyclableItem> call, Throwable t) {
                    Toast.makeText(AddItemActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
