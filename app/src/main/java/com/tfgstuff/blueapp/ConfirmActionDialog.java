package com.tfgstuff.blueapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmActionDialog extends DialogFragment {
    public static final int CONNECTION_CODE = 112;
    public static final int DISCONNECTION_CODE = 8;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle mArgs = getArguments();
        if (mArgs != null) {
            builder.setMessage(mArgs.getString("confirm_action_dialog_message"))
                    .setPositiveButton(getString(R.string.accept), (dialog, id) -> {
                        switch (mArgs.getInt("type")) {
                            case CONNECTION_CODE:
                                Intent connection = new Intent(getContext(),DataActivity.class);
                                BluetoothDevice device = mArgs.getParcelable("object");
                                connection.putExtra("object", device);
                                startActivity(connection);
                                break;
                            case DISCONNECTION_CODE:

                                break;
                        }
                        dialog.dismiss();
                    }).setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        }
        return builder.create();
    }
}
