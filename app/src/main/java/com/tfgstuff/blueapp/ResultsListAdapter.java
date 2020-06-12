package com.tfgstuff.blueapp;

import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ViewHolder> {

    private OnResultClickListener mListener;
    private ArrayList<ScanResult> results;

    public ResultsListAdapter(ArrayList<ScanResult> results, OnResultClickListener mListener) {
        this.results = results;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ResultsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_recycler_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsListAdapter.ViewHolder holder, int position) {
        if (results.get(position).getDevice().getName() != null) {
            holder.name.setText(results.get(position).getDevice().getName());
        } else {
            holder.name.setText(R.string.unknown_device);
        }
        holder.address.setText(results.get(position).getDevice().getAddress());
        holder.rssi.setText(String.valueOf(results.get(position).getRssi()));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, rssi;
        ImageButton infoButton;

        public ViewHolder(@NonNull View itemView, OnResultClickListener mListener) {
            super(itemView);

            name = itemView.findViewById(R.id.result_name);
            address = itemView.findViewById(R.id.result_address);
            rssi = itemView.findViewById(R.id.result_rssi);
            infoButton = itemView.findViewById(R.id.result_info);

            itemView.setOnClickListener(v -> mListener.onResultClick(getAdapterPosition()));
            infoButton.setOnClickListener(v -> mListener.onResultInfoClick(getAdapterPosition()));


        }
    }

    public interface OnResultClickListener {
        void onResultClick(int position);
        void onResultInfoClick(int position);
    }
}
