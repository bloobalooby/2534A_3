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
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> requestList;

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

        // Setup Spinner
        Context context = holder.itemView.getContext();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.status_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        // Set current status as selected
        int statusPosition = adapter.getPosition(request.getStatus());
        if (statusPosition != -1) {
            holder.spinnerStatus.setSelection(statusPosition);
        }

        // Handle spinner change
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstCall = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (firstCall) {
                    firstCall = false;
                    return; // Skip first automatic call
                }

                String newStatus = parent.getItemAtPosition(pos).toString();

                // Only update if changed
                if (!newStatus.equals(request.getStatus())) {
                    SharedPrefManager spm = new SharedPrefManager(context);
                    User user = spm.getUser();
                    String token = "Bearer " + user.getToken();

                    UserService userService = ApiUtils.getUserService();
                    userService.updateRequestStatus(request.getRequest_id(), token, newStatus).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                                request.setStatus(newStatus); // Update local list
                                notifyItemChanged(holder.getAdapterPosition());
                            } else {
                                Toast.makeText(context, "Failed to update status. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Update failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvStatus, tvDate, tvNotes, tvQty;
        Spinner spinnerStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvQty = itemView.findViewById(R.id.tvQty);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
        }
    }

}