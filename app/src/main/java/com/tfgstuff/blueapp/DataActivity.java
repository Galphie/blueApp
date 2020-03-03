package com.tfgstuff.blueapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

public class DataActivity extends AppCompatActivity {

    private TextView name, address;
    private BTLE_Device btle_device;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGattCallback bluetoothGattCallback;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        name = (TextView) findViewById(R.id.nombre);
        address = (TextView) findViewById(R.id.direccion);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                name.setText(null);
                address.setText(null);
                btle_device = null;
            } else {
                String jDevice = (String) extras.get("Objeto");
                btle_device = gson.fromJson(jDevice,BTLE_Device.class);
                name.setText(btle_device.getRssi());

//                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address.toString());
//                BluetoothGatt gatt = device.connectGatt(getApplicationContext(),
//                        false, bluetoothGattCallback, TRANSPORT_LE);
            }
        } else {
            name.setText((String) savedInstanceState.getSerializable("Name"));
            address.setText((String) savedInstanceState.getSerializable("Address"));
//            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address.toString());
//            BluetoothGatt gatt = device.connectGatt(getApplicationContext(),
//                    false, bluetoothGattCallback, TRANSPORT_LE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.devices:
                Intent dev = new Intent(this, MainActivity.class);
                startActivity(dev);
                return true;
            case R.id.graphics:
                Utils.toast(this, "Esto abrirá la actividad Gráficos.");
//                Intent abo = new Intent(this, AboutUsActivity.class);
//                startActivity(abo);
                return true;
            case R.id.logout:
                Utils.toast(this, "Esto cerrará sesión.");
//                signOut();
//                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
