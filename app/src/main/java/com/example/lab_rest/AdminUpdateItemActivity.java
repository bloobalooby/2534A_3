package com.example.lab_rest;

import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ItemService;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUpdateItemActivity extends AppCompatActivity {
    private EditText txtItemId;
    private EditText txtItemName;
    private EditText txtPrice;
    private ItemService itemService;
    private Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve item id from intent
        // get item id sent by UserItemListActivity
        Intent intent = getIntent();
        int itemId = intent.getIntExtra("item_id", -1);

        if (itemId == -1) {
            Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // get references to the form fields in layout
        txtItemId = findViewById(R.id.txtItemId);
        txtItemName = findViewById(R.id.txtItemName);
        txtPrice = findViewById(R.id.txtPrice);

        txtItemId.setEnabled(false); // make it read only

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get item service instance
        itemService = ApiUtils.getItemService();

        // execute the API query
        itemService.getItems(token, itemId).enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server returns success
                    // get item object from response
                    item = response.body();

                    // set values into forms
                    txtItemId.setText(String.valueOf(item.getItemId()));
                    txtItemName.setText(item.getItemName());
                    txtPrice.setText(String.valueOf(item.getPrice()));
                }
                else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session, please login again.", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable throwable) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    /**
     * Update item info in database when the user click Update Item button
     * @param view
     */
    public void updateItem(View view) {
        // get values in form
        String name = txtItemName.getText().toString();
        String price = txtPrice.getText().toString();

        Log.d("MyApp:", "Old Item Info: " + item.toString());

        item.setItemName(name);
        item.setPrice(Double.parseDouble(price));

        Log.d("MyApp:", "New Item Info: " + item.toString());

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // send request to update item record to the REST API
        ItemService itemService = ApiUtils.getItemService();
        Call<Item> call = itemService.updateItem(user.getToken(), item.getItemId(),
                item.getItemName(), String.valueOf(item.getPrice()));

        // execute
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                // for debug purpose
                Log.d("MyApp:", "Update Request Response: " + response.raw().toString());

                if (response.code() == 200) {
                    Item updatedItem = response.body();

                    displayUpdateSuccess(updatedItem.getItemName() + " updated successfully.");

                }
                else if (response.code() == 401) {
                    // unauthorized error. invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    // server return other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable throwable) {
                displayAlert("Error [" + throwable.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + throwable.getCause().getMessage());

            }
        });

    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // end this activity and go back to previous activity, UserItemListActivity
                        finish();

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}