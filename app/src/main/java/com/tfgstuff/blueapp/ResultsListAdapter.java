package com.tfgstuff.blueapp;

import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ViewHolder> {
    private ArrayList<ScanResult> results;

    public ResultsListAdapter(ArrayList<ScanResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ResultsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsListAdapter.ViewHolder holder, int position) {
        if (results.get(position).getDevice().getName() != null) {
            holder.name.setText(results.get(position).getDevice().getName());
        } else {
            holder.name.setText("Desconocido");
        }
        holder.address.setText(results.get(position).getDevice().getAddress());
        holder.rssi.setText(String.valueOf(results.get(position).getRssi()));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, rssi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.result_name);
            address = itemView.findViewById(R.id.result_address);
            rssi = itemView.findViewById(R.id.result_rssi);

        }
    }
}
