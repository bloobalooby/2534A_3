package com.example.lab_rest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;

import java.util.List;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.ViewHolder> {

    private List<Item> selectedItems;

    public SelectedItemAdapter(List<Item> selectedItems) {
        this.selectedItems = selectedItems;
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
        Item item = selectedItems.get(position);
        holder.tvItemName.setText(item.getItemName());
        holder.tvItemQuantity.setText("Qty: " + item.getQuantity());
        double total = item.getQuantity() * item.getPrice();
        holder.tvItemTotal.setText(String.format("RM %.2f", total));
        holder.imgItem.setImageResource(item.getImageResId()); // Optional if you show image

    // Remove logic
        holder.tvRemove.setOnClickListener(v -> {
        int pos = holder.getAdapterPosition();
        if (pos != RecyclerView.NO_POSITION) {
            selectedItems.remove(pos);
            notifyItemRemoved(pos);
        }
    });
}

    @Override
    public int getItemCount() {
        return selectedItems != null ? selectedItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemQuantity, tvItemTotal;
        ImageView imgItem;
        TextView tvRemove = itemView.findViewById(R.id.tvRemove);


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvRemove = itemView.findViewById(R.id.tvRemove);

        }
    }
}
