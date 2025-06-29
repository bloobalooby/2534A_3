package com.example.lab_rest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Request;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<Request> requestList;

    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request req = requestList.get(position);
        holder.txtStatus.setText("Status: " + req.getStatus());
        holder.txtAddress.setText("Address: " + req.getAddress());
        holder.txtWeight.setText("Weight: " + req.getWeight() + " kg");
        holder.txtPrice.setText("Total: RM" + req.getTotal_price());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView txtStatus, txtAddress, txtWeight, txtPrice;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtWeight = itemView.findViewById(R.id.txtWeight);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
