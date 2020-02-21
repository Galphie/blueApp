package com.tfgstuff.blueapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    boolean showingBtnText = false;
    TextView temperatura, intensidad, co2, personas, status, textViewPaired;
    EditText datos;
    ImageButton boton;
    Button datosButton;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVER_BT = 0;

    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothSocket bluetoothSocket;
    private static BluetoothDevice bluetoothDevice;

    static final UUID myUUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    static final UUID serviceUUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");

    private final static String MAC = "A4:CF:12:9A:1D:5A";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        textViewPaired = (TextView) findViewById(R.id.textViewPaired);
        status = (TextView) findViewById(R.id.status);
        temperatura = (TextView) findViewById(R.id.temperatura);
        intensidad = (TextView) findViewById(R.id.iLuminica);
        co2 = (TextView) findViewById(R.id.co2level);
        personas = (TextView) findViewById(R.id.personas);
        datos = (EditText) findViewById(R.id.datos);
        boton = (ImageButton) findViewById(R.id.boton);
        datosButton = (Button) findViewById(R.id.datosButton);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            status.setText("Bluetooth no habilitado, conectando...");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            status.setText("Conectado");
        }

        textViewPaired.setText("");
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            if (device.getAddress().equals(MAC)) {
                textViewPaired.append(device.getName());
            }
        }

        try {
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();

        } catch (IOException ex) {
            Toast toast = Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG);
            Log.e("Excepcion rara", ex.toString());
            toast.show();
        }

        final Button btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingBtnText == true) {
                    btnScan.setText("Escanear");
                    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : devices) {
                        if (device.getName().equals("ESP32test")) {

                            textViewPaired.setText(device.getName());
                        }
                    }
                    showingBtnText = false;
                    status.setText("Scan complete");
                } else {
                    btnScan.setText("Detener escaneo");
                    showingBtnText = true;
                    status.setText("Scanning...");
                }
            }
        });
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
                startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0);


                return true;
            case R.id.ajustes:
                /*Intent sett = new Intent(this, AboutUsActivity.class);
                startActivity(sett);*/
                Toast.makeText(this, "Esto abrirá 'Ajustes'", Toast.LENGTH_LONG).show();
                return true;
            case R.id.graphics:
                Toast.makeText(this, "Esto abrirá 'Gráficas'", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void recibirDatos() {

    }


}






