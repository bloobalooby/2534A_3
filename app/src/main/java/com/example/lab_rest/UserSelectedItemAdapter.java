package com.example.lab_rest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;

import java.util.List;

public class UserSelectedItemAdapter extends RecyclerView.Adapter<UserSelectedItemAdapter.ViewHolder> {

    private List<Item> selectedItems;
    private OnItemChangeListener itemChangeListener;

    // ✅ Constructor with listener
    public UserSelectedItemAdapter(List<Item> selectedItems, OnItemChangeListener listener) {
        this.selectedItems = selectedItems;
        this.itemChangeListener = listener;
    }

    @NonNull
    @Override
    public UserSelectedItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_summary_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = selectedItems.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemQuantity.setText("Qty: " + item.getQuantity());
        double total = item.getQuantity() * item.getPrice();
        holder.tvItemTotal.setText(String.format("RM %.2f", total));
        holder.imgItem.setImageResource(item.getImageResId());

        // ➕ Increase quantity
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(holder.getAdapterPosition());
            itemChangeListener.onItemListChanged(selectedItems);
        });

        // ➖ Decrease quantity
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(holder.getAdapterPosition());
                itemChangeListener.onItemListChanged(selectedItems);
            }
        });

        // 🗑 Remove item
        holder.tvRemove.setOnClickListener(v -> {
            int positionToRemove = holder.getAdapterPosition();
            if (positionToRemove != RecyclerView.NO_POSITION) {
                selectedItems.remove(positionToRemove);
                notifyItemRemoved(positionToRemove);
                notifyItemRangeChanged(positionToRemove, selectedItems.size());
                itemChangeListener.onItemListChanged(selectedItems);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedItems != null ? selectedItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemQuantity, tvItemTotal, tvRemove;
        ImageView imgItem;
        ImageButton btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            tvRemove = itemView.findViewById(R.id.tvRemove);
            imgItem = itemView.findViewById(R.id.imgItem);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }

    // ✅ Callback interface to notify activity
    public interface OnItemChangeListener {
        void onItemListChanged(List<Item> updatedList);
    }
}
