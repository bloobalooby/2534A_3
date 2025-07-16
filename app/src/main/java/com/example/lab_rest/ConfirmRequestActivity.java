package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ConfirmRequestActivity extends AppCompatActivity {

    private RecyclerView rvSelectedItems;
    private EditText edtNotes;
    private TextView tvTotalPrice, tvNotePreview;
    private List<Item> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        MaterialToolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar); // Optional but good if using menu items

        toolbar.setNavigationOnClickListener(v -> {
            finish(); // 👈 This will go back to the previous screen
        });

        rvSelectedItems = findViewById(R.id.rvSelectedItems);
        edtNotes = findViewById(R.id.edtNotes);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvNotePreview = findViewById(R.id.tvNotePreview);

        rvSelectedItems.setLayoutManager(new LinearLayoutManager(this));
        Button btnProceed = findViewById(R.id.btnProceed);

        btnProceed.setOnClickListener(v -> {
            String json = new Gson().toJson(selectedItems);
            Intent intent = new Intent(this, DeliveryOptionActivity.class);
            intent.putExtra("selectedItems", new Gson().toJson(selectedItems));
            intent.putExtra("note", edtNotes.getText().toString().trim());
            startActivity(intent);
        });


        // Toolbar setup
        MaterialToolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        // 🧠 Get selected items
        String json = getIntent().getStringExtra("selectedItems");
        Type listType = new TypeToken<List<Item>>() {}.getType();
        selectedItems = new Gson().fromJson(json, listType);

        SelectedItemAdapter adapter = new SelectedItemAdapter(selectedItems, updatedList -> {
            selectedItems = updatedList;
            updateTotalPrice(updatedList);
        });
        rvSelectedItems.setAdapter(adapter);

        updateTotalPrice(selectedItems);

        // Live note preview
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
    }

    private void updateTotalPrice(List<Item> items) {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText("Total: RM" + String.format("%.2f", total));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

