package com.example.lab_rest.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Item;
import com.example.lab_rest.model.Request;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.RequestService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<Request> requestList;
    private RequestService requestService;
    private OnDeleteClickListener onDeleteClickListener;
    private String userRole;

    public interface OnDeleteClickListener {
        void onDeleteClicked(int requestId, int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public RequestAdapter(List<Request> requestList, String userRole) {
        this.requestList = requestList;
        this.userRole = userRole;
        this.requestService = ApiUtils.getRequestService();
    }

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requestList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvItemName.setText("Item: " + getItemNameById(request.getItem_id()));
        holder.tvStatus.setText("Status: " + request.getStatus());
        holder.tvDate.setText("Date: " + request.getRequest_date());
        holder.tvNotes.setText("Notes: " + request.getNotes());

        // Hide everything by default
        holder.spinnerStatus.setVisibility(View.GONE);
        holder.tvDelete.setVisibility(View.GONE);
        holder.editTextWeight.setVisibility(View.GONE);
        holder.textViewWeight.setVisibility(View.GONE);
        holder.tvPrice.setVisibility(View.GONE);

        double weight = request.getWeight();
        Item item = request.getItem();

        if (item != null) {
            double pricePerKg = item.getPrice();
            double totalPrice = pricePerKg * weight;
            holder.tvPrice.setText(String.format("Price: RM %.2f", totalPrice));
        } else {
            holder.tvPrice.setText("Price: N/A");
            Log.e("RequestAdapter", "Item is null for request ID: " + request.getRequest_id());
        }

        if ("admin".equalsIgnoreCase(userRole) || "superadmin".equalsIgnoreCase(userRole)) {
            // Admin/superadmin views
            holder.spinnerStatus.setVisibility(View.VISIBLE);
            holder.editTextWeight.setVisibility(View.VISIBLE);
            holder.editTextWeight.setEnabled(true);
            holder.editTextWeight.setText(String.valueOf(weight));

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    context, R.array.status_array, android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerStatus.setAdapter(adapter);

            int statusPosition = adapter.getPosition(request.getStatus());
            if (statusPosition != -1) {
                holder.spinnerStatus.setSelection(statusPosition);
            }

            // Spinner selection update
            holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean firstCall = true;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (firstCall) {
                        firstCall = false;
                        return;
                    }

                    String newStatus = parent.getItemAtPosition(pos).toString();
                    if (!newStatus.equals(request.getStatus())) {
                        SharedPrefManager spm = new SharedPrefManager(context);
                        String token = spm.getUser().getToken();

                        requestService.updateRequestStatus(token, request.getRequest_id(), newStatus)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();
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
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // Weight change listener
            holder.editTextWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String weightStr = s.toString();
                    if (!weightStr.isEmpty()) {
                        try {
                            double newWeight = Double.parseDouble(weightStr);
                            if (newWeight != request.getWeight()) {
                                SharedPrefManager spm = new SharedPrefManager(context);
                                String token = spm.getUser().getToken();

                                requestService.updateRequestStatusAndWeight(token, request.getRequest_id(), request.getStatus(), newWeight)
                                        .enqueue(new Callback<Request>() {
                                            @Override
                                            public void onResponse(Call<Request> call, Response<Request> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(context, "Weight updated", Toast.LENGTH_SHORT).show();
                                                    request.setWeight(newWeight);
                                                } else {
                                                    Toast.makeText(context, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Request> call, Throwable t) {
                                                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Invalid weight", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            if ("superadmin".equalsIgnoreCase(userRole)) {
                holder.tvDelete.setVisibility(View.VISIBLE);
                holder.tvDelete.setText("Delete");

                holder.tvDelete.setOnClickListener(v -> {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClicked(request.getRequest_id(), holder.getAdapterPosition());
                    }
                });
            }

        } else {
            // Regular user
            holder.textViewWeight.setVisibility(View.VISIBLE);
            holder.textViewWeight.setText(String.format("Weight: %.2f kg", weight));

            if (item != null) {
                double totalPrice = item.getPrice() * weight;
                holder.tvPrice.setText(String.format("Price: RM %.2f", totalPrice));
            } else {
                holder.tvPrice.setText("Price: N/A");
                Log.e("RequestAdapter", "Item is null for request ID: " + request.getRequest_id());
            }
            holder.tvPrice.setVisibility(View.VISIBLE);

            if ("Pending".equalsIgnoreCase(request.getStatus())) {
                holder.tvDelete.setVisibility(View.VISIBLE);
                holder.tvDelete.setText("Delete");

                holder.tvDelete.setOnClickListener(v -> {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClicked(request.getRequest_id(), holder.getAdapterPosition());
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvStatus, tvDate, tvNotes, tvDelete, tvPrice;
        Spinner spinnerStatus;
        TextView textViewWeight;
        EditText editTextWeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
            textViewWeight = itemView.findViewById(R.id.textViewWeight);
            editTextWeight = itemView.findViewById(R.id.edittextWeight);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
