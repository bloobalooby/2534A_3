package com.example.lab_rest.sharedpref;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;

import java.util.Arrays;
import java.util.List;

public class SubmitRequestActivity extends AppCompatActivity {

    RecyclerView rvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);

        rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        List<Item> itemList = Arrays.asList(
                new Item(1, "Plastic Bottles", 0.50, R.drawable.ic_plastic),
                new Item(2, "Paper", 0.30, R.drawable.ic_paper),
                new Item(3, "Cardboard", 0.40, R.drawable.ic_cardboard),
                new Item(4, "Aluminum Cans", 1.50, R.drawable.ic_aluminum),
                new Item(5, "Glass Containers", 0.20, R.drawable.ic_glass),
                new Item(6, "Used Cooking Oil", 1.00, R.drawable.ic_oil)
        );

        ItemAdapter adapter = new ItemAdapter(this, itemList);
        rvItems.setAdapter(adapter);
    }
}
