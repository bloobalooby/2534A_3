package com.example.lab_rest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;
import com.example.lab_rest.sharedpref.UpdateItemActivity;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    // create ViewHolder class to bind list item view
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvItemId;
        public TextView tvItemName;
        public TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    } // close ViewHolder class

    // adapter class definition
    private List<Item> itemListData; // list of item objects
    private Context mContext; // activity context

    public ItemAdapter(Context context, List<Item> listData) {
        itemListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout
        View view = inflater.inflate(R.layout.recyclable_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item m = itemListData.get(position);
        holder.tvItemId.setText(String.valueOf(m.getItemId()));
        holder.tvItemName.setText(m.getItemName());
        holder.tvPrice.setText(String.format("RM %.2f", m.getPrice()));
    }

    @Override
    public int getItemCount() {
        return itemListData.size();
    }
}
