package com.example.lab_rest.sharedpref;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;

import java.util.List;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.ViewHolder> {

    private List<Item> itemList;

    // ✅ Make sure itemList is not null
    public SelectedItemAdapter(List<Item> itemList) {
        this.itemList = (itemList != null) ? itemList : new java.util.ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvItemName, tvItemQuantity, tvItemTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
        }
    }

    @NonNull
    @Override
    public SelectedItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_summary_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.imgItem.setImageResource(item.getImageResId());
        holder.tvItemName.setText(item.getItemName());
        holder.tvItemQuantity.setText("Qty: " + item.getQuantity());

        double total = item.getPrice() * item.getQuantity();
        holder.tvItemTotal.setText("RM " + String.format("%.2f", total));
    }

    @Override
    public int getItemCount() {
        return itemList.size(); // 🛠️ itemList is now guaranteed to be non-null
    }
}
