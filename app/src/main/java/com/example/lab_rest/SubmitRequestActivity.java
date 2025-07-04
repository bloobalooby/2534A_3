package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubmitRequestActivity extends AppCompatActivity {

    RecyclerView rvItems;
    Button btnDone;
    List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);

        rvItems = findViewById(R.id.rvItems);
        btnDone = findViewById(R.id.btnDone);

        rvItems.setLayoutManager(new GridLayoutManager(this, 2));

        itemList = Arrays.asList(
                new Item(1, "Plastic Bottles", 0.50, R.drawable.ic_plastic),
                new Item(2, "Paper", 0.30, R.drawable.ic_paper),
                new Item(3, "Cardboard", 0.40, R.drawable.ic_cardboard),
                new Item(4, "Aluminum Cans", 1.50, R.drawable.ic_aluminum),
                new Item(5, "Glass Containers", 0.20, R.drawable.ic_glass),
                new Item(6, "Used Cooking Oil", 1.00, R.drawable.ic_oil)
        );

        ItemAdapter adapter = new ItemAdapter(this, itemList);
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
        });
    }
}
