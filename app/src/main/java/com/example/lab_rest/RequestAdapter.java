package com.example.lab_rest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.model.Request;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<Request> requestList;
    private String getItemNameById(int id) {
        switch (id) {
            case 1: return "Plastic Bottle";
            case 2: return "Glass Container";
            case 3: return "Paper";
            case 4: return "Cardboard";
            case 5: return "Aluminium Cans";
            case 6: return "Used Cooking Oils";
            case 7: return "Old Clothes";
            default: return "Unknown Item";
        }
    }


    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.tvItemName.setText("Item: " + getItemNameById(request.getItem_id()));
        holder.tvStatus.setText("Status: " + request.getStatus());
        holder.tvDate.setText("Date: " + request.getRequest_date());
        holder.tvNotes.setText("Notes: " + request.getNotes());
        holder.tvQty.setText("Quantity: " + request.getWeight() + "kg");

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvStatus, tvDate, tvNotes, tvQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvQty = itemView.findViewById(R.id.tvQty);
        }
    }
}

