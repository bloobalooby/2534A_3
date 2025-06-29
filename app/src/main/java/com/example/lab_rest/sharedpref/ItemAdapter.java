package com.example.lab_rest.sharedpref;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

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
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        Intent intent = new Intent(context, ConfirmRequestActivity.class);
        intent.putExtra("item_id", item.getItemId());
        intent.putExtra("item_name", item.getItemName());
        intent.putExtra("quantity", item.getQuantity());
        context.startActivity(intent);


        holder.itemImage.setImageResource(item.getImageResId());
        holder.itemName.setText(item.getItemName());
        holder.itemPrice.setText("RM " + item.getPrice());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnPlus.setOnClickListener(v -> {
            int qty = item.getQuantity();
            item.setQuantity(qty + 1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        });

        holder.btnMinus.setOnClickListener(v -> {
            int qty = item.getQuantity();
            if (qty > 0) {
                item.setQuantity(qty - 1);
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice, tvQuantity;
        Button btnPlus, btnMinus;

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
