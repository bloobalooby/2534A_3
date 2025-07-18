package com.example.lab_rest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.AdminItemAdapter;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ItemService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminItemListActivity extends AppCompatActivity {

    private ItemService itemService;
    private RecyclerView rvAdminItemList;
    private AdminItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_item_list);

        // Adjust layout for system UI (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        rvAdminItemList = findViewById(R.id.rvAdminItemList);

        // Register context menu for RecyclerView items (used for update/delete)
        registerForContextMenu(rvAdminItemList);

        // Floating action button to add new item
        FloatingActionButton fabAddItem = findViewById(R.id.fabAddItem);
        fabAddItem.setOnClickListener(v -> {
            // Navigate to AddItemActivity
            Intent intent = new Intent(AdminItemListActivity.this, AddItemActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list every time activity resumes
        updateRecyclerView();
    }

    /**
     * Fetch all items from the server and update the RecyclerView.
     */
    private void updateRecyclerView() {
        // Retrieve logged-in user's token from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // Get ItemService for API calls
        itemService = ApiUtils.getItemService();

        // API call to get all items
        itemService.getAllItems(token).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString()); // Debug

                if (response.code() == 200) {
                    // Response successful
                    List<Item> items = response.body();

                    // Initialize and set adapter
                    adapter = new AdminItemAdapter(getApplicationContext(), items);
                    rvAdminItemList.setAdapter(adapter);

                    // Set layout manager
                    rvAdminItemList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // Add item divider
                    rvAdminItemList.addItemDecoration(
                            new DividerItemDecoration(rvAdminItemList.getContext(), DividerItemDecoration.VERTICAL)
                    );
                } else if (response.code() == 401) {
                    // Unauthorized - session expired
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    // Other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                // Network or server error
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    /**
     * Handle deletion of an item using API call.
     * @param selectedItem The item selected by the user
     */
    private void doDeleteItem(Item selectedItem) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // Prepare API call to delete item
        ItemService itemService = ApiUtils.getItemService();
        Call<DeleteResponse> call = itemService.deleteItem(user.getToken(), selectedItem.getItemId());

        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    displayAlert("Item successfully deleted");
                    updateRecyclerView(); // Refresh list after deletion
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable throwable) {
                displayAlert("Error [" + throwable.getMessage() + "]");
                Log.e("MyApp:", throwable.getMessage());
            }
        });
    }

    /**
     * Display a simple alert dialog with a message.
     * @param message The message to show in the dialog
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Clear user session and redirect to login page.
     */
    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout(); // Clear session
        finish(); // End current activity
        startActivity(new Intent(this, LoginActivity.class)); // Go to Login
    }

    /**
     * Inflate context menu (when long press on RecyclerView item).
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_item_menu, menu);
    }

    /**
     * Handle context menu item selected (Update or Delete).
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Item selectedItem = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedItem.toString()); // Debug

        if (item.getItemId() == R.id.menu_update_item) {
            doViewUpdate(selectedItem); // Update
        } else if (item.getItemId() == R.id.menu_delete) {
            doDeleteItem(selectedItem); // Delete
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Navigate to the update item screen.
     * @param selectedItem The item to be updated
     */
    private void doViewUpdate(Item selectedItem) {
        Log.d("MyApp:", "updating item: " + selectedItem.toString());
        Intent intent = new Intent(getApplicationContext(), AdminUpdateItemActivity.class);
        intent.putExtra("item_id", selectedItem.getItemId());
        startActivity(intent);
    }
}
