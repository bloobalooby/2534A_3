package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.RequestService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter class for displaying user recycling requests in a RecyclerView.
 * Allows the user to view request details, and delete pending requests.
 */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<Request> requestList;
    private RequestService requestService;
    private OnDeleteClickListener onDeleteClickListener;

    /**
     * Interface for delete click callback.
     */
    public interface OnDeleteClickListener {
        void onDeleteClicked(int requestId, int position);
    }

    /**
     * Set the listener for delete actions.
     */
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    /**
     * Constructor initializes request list and API service.
     */
    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
        this.requestService = ApiUtils.getRequestService(); // Retrofit instance
    }

    /**
     * Converts item ID into human-readable item name.
     */
    private String getItemNameById(int id) {
        switch (id) {
            case 10: return "Plastic Bottle";
            case 2: return "Glass Container";
            case 11: return "Paper";
            case 12: return "Cardboard";
            case 13: return "Aluminium Cans";
            case 14: return "Used Cooking Oils";
            case 7: return "Old Clothes";
            case 8: return "Metal Scraps";
            default: return "Unknown Item";
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each request card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requestList.get(position);
        Context context = holder.itemView.getContext();

        // Set request details to UI components
        holder.tvItemName.setText("Item: " + getItemNameById(request.getItem_id()));
        holder.tvStatus.setText("Status: " + request.getStatus());
        holder.tvDate.setText("Date: " + request.getRequest_date());
        holder.tvNotes.setText("Notes: " + request.getNotes());
        holder.tvQty.setText("Quantity: " + request.getWeight() + "kg");

        // Hide spinner for user view
        holder.spinnerStatus.setVisibility(View.GONE);

        // Handle delete button visibility and click for pending requests
        if ("Pending".equalsIgnoreCase(request.getStatus())) {
            holder.tvDelete.setVisibility(View.VISIBLE);
            holder.tvDelete.setText("Delete");

            holder.tvDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClicked(request.getRequest_id(), holder.getAdapterPosition());
                }
            });
        } else {
            holder.tvDelete.setVisibility(View.GONE);
        }

        // (Optional) Status Spinner setup — hidden for user, but kept in code
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.status_array, // example: ["Pending", "Approved", "Rejected"]
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        // Preselect current status
        int statusPosition = adapter.getPosition(request.getStatus());
        if (statusPosition != -1) {
            holder.spinnerStatus.setSelection(statusPosition);
        }

        // Spinner listener (inactive for user but retained for reuse)
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstCall = true; // prevent initial trigger

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (firstCall) {
                    firstCall = false;
                    return;
                }

                String newStatus = parent.getItemAtPosition(pos).toString();
                if (!newStatus.equals(request.getStatus())) {
                    SharedPrefManager spm = new SharedPrefManager(context);
                    User user = spm.getUser();
                    String token = user.getToken(); // API key

                    // API call to update status
                    requestService.updateRequestStatus(token, request.getRequest_id(), newStatus)
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                                        request.setStatus(newStatus);
                                        notifyItemChanged(holder.getAdapterPosition());
                                    } else {
                                        Toast.makeText(context, "Update failed. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    /**
     * ViewHolder class for each request item view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvStatus, tvDate, tvNotes, tvQty, tvDelete;
        Spinner spinnerStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
        }
    }
}

