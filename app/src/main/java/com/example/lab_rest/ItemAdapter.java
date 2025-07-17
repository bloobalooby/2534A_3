package com.example.lab_rest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

    private int selectedPosition = -1; // no selection by default


    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.itemImage.setImageResource(item.getImageResId());
        holder.itemName.setText(item.getItemName());
        holder.itemPrice.setText(String.format("RM %.2f", item.getPrice()));
        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged(); // refresh all to uncheck others
        });

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Item getSelectedItem() {
        if (selectedPosition >= 0 && selectedPosition < itemList.size()) {
            return itemList.get(selectedPosition);
        }
        return null;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice;
        CheckBox checkBox;

        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            radioButton = itemView.findViewById(R.id.radioButtonSelect);
        }

    }

    private int getImageForItem(int itemId) {
        switch (itemId) {
            case 1: return R.drawable.ic_plastic;
            case 2: return R.drawable.ic_paper;
            case 3: return R.drawable.ic_cardboard;
            case 4: return R.drawable.ic_aluminum;
            case 5: return R.drawable.ic_glass;
            case 6: return R.drawable.ic_oil;
            default: return R.drawable.ic_launcher_foreground;
        }
    }
}
