package com.example.lab_rest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public interface OnItemQuantityChangeListener {
        void onQuantityChanged();
    }

    private Context context;
    private List<Item> itemList;
    private OnItemQuantityChangeListener listener;

    public ItemAdapter(Context context, List<Item> itemList, OnItemQuantityChangeListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        double unitPrice = item.getPrice();
        int quantity = item.getQuantity();
        double total = unitPrice * quantity;

        holder.itemImage.setImageResource(item.getImageResId());
        holder.itemName.setText(item.getItemName());
        holder.itemPrice.setText(String.format("RM %.2f", total));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnPlus.setOnClickListener(v -> {
            int qty = item.getQuantity();
            item.setQuantity(qty + 1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

            double updatedTotal = item.getPrice() * item.getQuantity();
            holder.itemPrice.setText(String.format("RM %.2f", updatedTotal));

            if (listener != null) listener.onQuantityChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            int qty = item.getQuantity();
            if (qty > 0) {
                item.setQuantity(qty - 1);
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

                double updatedTotal = item.getPrice() * item.getQuantity();
                holder.itemPrice.setText(String.format("RM %.2f", updatedTotal));

                if (listener != null) listener.onQuantityChanged();
            }
        });
    }

    // ✅ This should be outside of onBindViewHolder
    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice, tvQuantity;
        ImageButton btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);

        }
    }
}

