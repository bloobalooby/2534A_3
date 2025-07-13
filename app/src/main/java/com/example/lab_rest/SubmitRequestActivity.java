package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubmitRequestActivity extends AppCompatActivity implements ItemAdapter.OnItemQuantityChangeListener {

    RecyclerView rvItems;
    Button btnDone, btnClear;
    TextView tvTotalPrice;
    List<Item> itemList;
    ItemAdapter adapter;
    double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);

        // ✅ Setup MaterialToolbar
        MaterialToolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rvItems = findViewById(R.id.rvItems);
        btnDone = findViewById(R.id.btnDone);
        btnClear = findViewById(R.id.btnClear);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        rvItems.setLayoutManager(new GridLayoutManager(this, 2));

        itemList = Arrays.asList(
                new Item(1, "Plastic Bottles", 0.50, R.drawable.ic_plastic),
                new Item(2, "Paper", 0.30, R.drawable.ic_paper),
                new Item(3, "Cardboard", 0.40, R.drawable.ic_cardboard),
                new Item(4, "Aluminum Cans", 1.50, R.drawable.ic_aluminum),
                new Item(5, "Glass Containers", 0.20, R.drawable.ic_glass),
                new Item(6, "Used Cooking Oil", 1.00, R.drawable.ic_oil)
        );

        adapter = new ItemAdapter(this, itemList, this);
        rvItems.setAdapter(adapter);

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

            Snackbar.make(rvItems, "Proceeding to confirmation...", Snackbar.LENGTH_SHORT).show();
        });

        btnClear.setOnClickListener(v -> {
            for (Item item : itemList) {
                item.setQuantity(0);
            }
            adapter.notifyDataSetChanged();
            updateTotalPrice();
            Toast.makeText(this, "Selections cleared", Toast.LENGTH_SHORT).show();
        });

        updateTotalPrice();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

