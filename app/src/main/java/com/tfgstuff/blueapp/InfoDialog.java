package com.tfgstuff.blueapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class InfoDialog extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.info_dialog, null);

        TextView name = view.findViewById(R.id.info_device_name);
        TextView mac = view.findViewById(R.id.info_device_mac);
        TextView uuids = view.findViewById(R.id.info_device_uuids);

        BluetoothDevice device = getArguments().getParcelable("object");
        if (device.getName().equals("")) {
            name.setText(getString(R.string.unknown_device));
        } else {
            name.setText(device.getName());
        }
        mac.setText(device.getAddress());
        ParcelUuid[] parcelUuids = device.getUuids();
        if (parcelUuids != null) {
            for (ParcelUuid uuid : parcelUuids) {
                Utils.toast(getContext(),uuid.toString());
            }
        }

        builder.setView(view)
                .setPositiveButton(getString(R.string.connect), (dialog, which) -> {
                    Intent connection = new Intent(getContext(), DataActivity.class);
                    connection.putExtra("object", device);
                    startActivity(connection);
                });
        return builder.create();
    }
}
