package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;

import java.util.List;

/**
 * Adapter to display a list of cart items in the confirmation screen.
 */
public class ConfirmCartAdapter extends RecyclerView.Adapter<ConfirmCartAdapter.ViewHolder> {

    private Context context;
    private List<Item> cartItems;

    // Constructor
    public ConfirmCartAdapter(Context context, List<Item> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    /**
     * Inflates the row layout for each item in the RecyclerView.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to each item in the RecyclerView.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = cartItems.get(position);
        holder.textViewItemName.setText(item.getItemName());
        holder.textViewPrice.setText("RM " + item.getPrice());
    }

    /**
     * Returns the number of items in the cart.
     */
    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    /**
     * ViewHolder class to hold references to item views for each row.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItemName, textViewPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.itemName);
            textViewPrice = itemView.findViewById(R.id.itemPrice);
        }
    }
}
