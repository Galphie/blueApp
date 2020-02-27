package com.tfgstuff.blueapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public final static String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_ENABLE_BT = 1;

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;

    private Button btn_scan;

    private BroadcastReceiver_BTState mBTStateUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);

        btn_scan = (Button) findViewById(R.id.btn_scan);
        findViewById(R.id.btn_scan).setOnClickListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();

        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();

        unregisterReceiver(mBTStateUpdateReceiver);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                Utils.toast(getApplicationContext(), "Por favor, habilita el Bluetooth");
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_scan:
                Utils.toast(getApplicationContext(),"Botón de escáner presionado");
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}






