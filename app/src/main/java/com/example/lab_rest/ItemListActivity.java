package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ItemService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemListActivity extends AppCompatActivity implements ItemAdapter.OnItemQuantityChangeListener {

    private ItemService itemService;
    private RecyclerView rvItemList;
    private Button btnDone, btnClear;
    private TextView tvTotalPrice;
    private List<Item> itemList = new ArrayList<>();
    private ItemAdapter adapter;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        MaterialToolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar); // Optional but good if using menu items

        toolbar.setNavigationOnClickListener(v -> {
            finish(); // 👈 This will go back to the previous screen
        });

        // Apply safe area insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references
        rvItemList = findViewById(R.id.rvItemList);
        btnDone = findViewById(R.id.btnDone);
        btnClear = findViewById(R.id.btnClear);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // Load items from API
        itemService = ApiUtils.getItemService();
        itemService.getAllItems(token).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList = response.body();

                    // 🔄 Assign image resource to each item
                    for (Item item : itemList) {
                        int resId = getImageResIdFromName(item.getItemName());
                        item.setImageResId(resId);
                    }

                    adapter = new ItemAdapter(getApplicationContext(), itemList, ItemListActivity.this);
                    rvItemList.setAdapter(adapter);
                    rvItemList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvItemList.addItemDecoration(new DividerItemDecoration(rvItemList.getContext(), DividerItemDecoration.VERTICAL));
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });

        // ✅ Done: send selected items
        btnDone.setOnClickListener(v -> {
            ArrayList<Item> selectedItems = new ArrayList<>();
            for (Item item : itemList) {
                if (item.getQuantity() > 0) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Please select at least one item.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, ConfirmRequestActivity.class);
            intent.putExtra("selectedItems", new Gson().toJson(selectedItems));
            startActivity(intent);
        });

        // ❌ Clear all quantities
        btnClear.setOnClickListener(v -> {
            for (Item item : itemList) {
                item.setQuantity(0);
            }
            adapter.notifyDataSetChanged();
            updateTotalPrice();
        });
    }

    @Override
    public void onQuantityChanged() {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (Item item : itemList) {
            totalPrice += item.getQuantity() * item.getPrice();
        }
        tvTotalPrice.setText(String.format("Total Estimated Price: RM %.2f", totalPrice));
    }

    private void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    // 🖼️ Get image resource ID based on item name
    private int getImageResIdFromName(String itemName) {
        switch (itemName.toLowerCase()) {
            case "plastic bottles": return R.drawable.ic_plastic;
            case "paper": return R.drawable.ic_paper;
            case "cardboard": return R.drawable.ic_cardboard;
            case "aluminum cans": return R.drawable.ic_aluminum;
            case "glass containers": return R.drawable.ic_glass;
            case "used cooking oil": return R.drawable.ic_oil;
            case "old clothes": return R.drawable.ic_cardboard;
            default: return R.drawable.ic_launcher_foreground;
        }
    }
}
