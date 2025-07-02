package com.example.lab_rest.sharedpref;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmRequestActivity extends AppCompatActivity {

    private RecyclerView rvSelectedItems;
    private EditText edtAddress, edtNotes;
    private Button btnSubmitRequest;
    private List<Item> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        // UI references
        rvSelectedItems = findViewById(R.id.rvSelectedItems);
        edtAddress = findViewById(R.id.edtAddress);
        edtNotes = findViewById(R.id.edtNotes);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        rvSelectedItems.setLayoutManager(new LinearLayoutManager(this));

        // Get user session
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        int userId = user.getId();

        // Receive selected items from intent
        String json = getIntent().getStringExtra("selectedItems");
        Type listType = new TypeToken<List<Item>>() {}.getType();
        selectedItems = new Gson().fromJson(json, listType);

        // Set up adapter
        SelectedItemAdapter adapter = new SelectedItemAdapter(selectedItems);
        rvSelectedItems.setAdapter(adapter);

        // Handle submission
        btnSubmitRequest.setOnClickListener(v -> {
            String address = edtAddress.getText().toString().trim();
            String notes = edtNotes.getText().toString().trim();

            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Submit each selected item as individual request
            UserService api = ApiUtils.getUserService();
            for (Item item : selectedItems) {
                Call<Void> call = api.submitRequest(
                        userId,
                        item.getItemId(),
                        address,
                        notes
                );

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ConfirmRequestActivity.this, "Request submitted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ConfirmRequestActivity.this, "Submit failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ConfirmRequestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            Toast.makeText(this, "Request submitted!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MyRequestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }
}
