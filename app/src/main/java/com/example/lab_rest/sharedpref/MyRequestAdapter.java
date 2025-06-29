package com.example.lab_rest.sharedpref;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Request;

import java.util.List;

public class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.ViewHolder> {
    private Context context;
    private List<Request> requestList;

    public interface CancelListener {
        void onCancel(Request request);
    }

    private CancelListener listener;

    public MyRequestAdapter(Context context, List<Request> requestList, CancelListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.tvItemName.setText("Item ID: " + request.getItem_id());
        holder.tvStatus.setText("Status: " + request.getStatus());
        holder.tvDate.setText("Date: " + request.getRequest_date());
        holder.tvNotes.setText("Notes: " + request.getNotes());

        if ("Pending".equalsIgnoreCase(request.getStatus())) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> listener.onCancel(request));
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvStatus, tvDate, tvNotes;
        Button btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
