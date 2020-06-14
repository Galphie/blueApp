package com.tfgstuff.blueapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity implements ResultsListAdapter.OnResultClickListener {

    private final static int REQUEST_ENABLE_BT = 69;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int SIGNAL_STRENGTH = -75;

    private static boolean scanning = false;
    ConstraintLayout parent;
    RecyclerView recyclerView;
    FloatingActionButton startScanButton, stopScanButton;

    ArrayList<ScanResult> results = new ArrayList<>();
    ArrayList<BluetoothDevice> devices = new ArrayList<>();

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        getWindow().setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.colorAccentDark, null));
        parent = findViewById(R.id.parent_activity_scan);
        recyclerView = findViewById(R.id.scanner_recycler_view);
        startScanButton = findViewById(R.id.start_scan_button);
        stopScanButton = findViewById(R.id.stop_scan_button);

        initRecyclerView();
        startScanButton.setOnClickListener(v -> {
            start();
        });

        stopScanButton.setOnClickListener(v -> {
            stop();
        });

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = Objects.requireNonNull(btManager).getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.coarse_location_access_title);
            builder.setMessage(R.string.coarse_location_access_message);
            builder.setPositiveButton(R.string.concede, null);
            builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION));
            builder.show();
        }

    }

    private void start() {
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            stop();
        } else {
            startScanButton.setVisibility(View.INVISIBLE);
            stopScanButton.setVisibility(View.VISIBLE);
            devices.clear();
            results.clear();
            recyclerView.getAdapter().notifyDataSetChanged();
            scan(true);
        }
    }

    private void stop() {
        btScanner.stopScan(leScanCallback);
    }

    private void scan(final boolean enable) {

        if (enable && !scanning) {
            Handler handler = new Handler();
            final Runnable runnable = () -> {
                scanning = false;
                Snackbar.make(parent, "Encontrados " + results.size() + " dispotivos.", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("", null)
                        .setBackgroundTint(ResourcesCompat.getColor(ScanActivity.this.getResources(), R.color.colorAccentDark, null))
                        .show();
                startScanButton.setVisibility(View.VISIBLE);
                stopScanButton.setVisibility(View.INVISIBLE);
                AsyncTask.execute(() -> btScanner.stopScan(leScanCallback));
            };
            handler.postDelayed(runnable, 7500);
            scanning = true;
            btScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            btScanner.stopScan(leScanCallback);
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ResultsListAdapter adapter = new ResultsListAdapter(results, this);
        recyclerView.setAdapter(adapter);
    }

    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!devices.contains(result.getDevice()) && result.getRssi() > SIGNAL_STRENGTH) {
                new Handler().post(() -> {
                    Log.d("ScanActivity", "onScanResult: "
                            + result.getDevice().getAddress() + "\nNombre: " + result.getDevice().getName());
                    results.add(result);
                    devices.add(result.getDevice());
                    recyclerView.getAdapter().notifyDataSetChanged();
                });
            }
        }
    };

    @Override
    public void onResultClick(int position) {
        BluetoothDevice device = results.get(position).getDevice();
        DialogFragment dialogFragment = new ConfirmActionDialog();
        Bundle args = new Bundle();
        args.putString("confirm_action_dialog_message",
                "¿Conectar con el dispositivo con dirección " + device.getAddress() + "?");
        args.putInt("type", ConfirmActionDialog.CONNECTION_CODE);
        args.putParcelable("object", device);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "Confirm connection");
    }

    @Override
    public void onResultInfoClick(int position) {
        DialogFragment dialogFragment = new InfoDialog();
        Bundle args = new Bundle();
        args.putParcelable("object", results.get(position).getDevice());
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(),"InfoDialog");
    }
}