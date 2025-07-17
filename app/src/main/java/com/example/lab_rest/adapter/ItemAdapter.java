package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Item> itemList;
    private int selectedPosition = -1;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView txtItemName, txtPrice;
        CheckBox cbSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.txtItemName.setText(item.getItemName());
        holder.txtPrice.setText("RM " + item.getPrice());
        holder.imgItem.setImageResource(item.getImageResId());
        holder.cbSelect.setChecked(holder.getAdapterPosition() == selectedPosition);

        holder.cbSelect.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            if (selectedPosition == adapterPosition) {
                selectedPosition = -1;
            } else {
                selectedPosition = adapterPosition;
            }
            notifyDataSetChanged(); // refresh all rows
        });

        holder.itemView.setOnClickListener(v -> {
            holder.cbSelect.performClick(); // makes full row clickable
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Item getSelectedItem() {
        if (selectedPosition != -1) {
            return itemList.get(selectedPosition);
        }
        return null;
    }
}
