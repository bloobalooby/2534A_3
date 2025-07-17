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
 * RecyclerView Adapter for displaying a list of items in the admin interface.
 * Supports long-click selection.
 */
public class AdminItemAdapter extends RecyclerView.Adapter<AdminItemAdapter.ViewHolder> {

    private List<Item> itemListData;   // List of items to display
    private Context mContext;          // Context from activity
    private int currentPos;            // Position of the item that was long-clicked

    // Constructor
    public AdminItemAdapter(Context context, List<Item> listData) {
        this.itemListData = listData;
        this.mContext = context;
    }

    // Helper method to return context
    private Context getmContext() {
        return mContext;
    }

    /**
     * ViewHolder class that holds references to the item views.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tvItemId;
        public TextView tvItemName;
        public TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize view components
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            // Register long click listener
            itemView.setOnLongClickListener(this);
        }

        /**
         * Capture long press event to track the selected position.
         */
        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    /**
     * Inflate the layout for a single item in the RecyclerView.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate custom layout
        View view = inflater.inflate(R.layout.recyclable_list_item, parent, false);

        // Return a new ViewHolder instance
        return new ViewHolder(view);
    }

    /**
     * Bind data to views in each row of the RecyclerView.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemListData.get(position);

        holder.tvItemId.setText(String.valueOf(item.getItemId()));
        holder.tvItemName.setText(item.getItemName());
        holder.tvPrice.setText(String.format("RM %.2f", item.getPrice())); // 2 decimal places
    }

    /**
     * Return total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return itemListData.size();
    }

    /**
     * Returns the item that was last long-pressed.
     */
    public Item getSelectedItem() {
        if (currentPos >= 0 && itemListData != null && currentPos < itemListData.size()) {
            return itemListData.get(currentPos);
        }
        return null;
    }
}
