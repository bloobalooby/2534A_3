package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.adapter.AdminItemAdapter;
import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ItemService;
import com.example.lab_rest.LoginActivity;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.example.lab_rest.UpdateItemActivity;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvAdminItemList = findViewById(R.id.rvAdminItemList);

        // register for update item menu
        registerForContextMenu(rvAdminItemList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // fetch and update item list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get item service instance
        itemService = ApiUtils.getItemService();

        // execute the call
        itemService.getAllItems(token).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // get list of item object from response
                    List<Item> items = response.body();

                    //initialize adapter
                    adapter = new AdminItemAdapter(getApplicationContext(), items);

                    // set adapter to the RecyclerView
                    rvAdminItemList.setAdapter(adapter);

                    // set layout to the recycler view
                    rvAdminItemList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvAdminItemList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvAdminItemList.addItemDecoration(dividerItemDecoration);
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to login page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_item_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        Item selectedItem = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedItem.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_update_item) {    // user clicked details contextual menu
            // user clicked details contextual menu
            doViewUpdate(selectedItem);
        }

        return super.onContextItemSelected(item);
    }

    private void doViewUpdate(Item selectedItem) {
        Log.d("MyApp:", "updating item: " + selectedItem.toString());
        // forward user to UpdateItemActivity, passing the selected item id
        Intent intent = new Intent(getApplicationContext(), UpdateItemActivity.class);
        intent.putExtra("item_id", selectedItem.getItemId());
        startActivity(intent);
    }

}