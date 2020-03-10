package com.tfgstuff.blueapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class DataActivity extends AppCompatActivity {

    private static final int GATT_INTERNAL_ERROR = 129;
    private static final String S_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    private static final String C_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
    private TextView name, address, choose, status, temperature, co2, iLum, people;
    private CardView dataCard;
    private Button button, button2;

    private static boolean connected;
    private static boolean actualizando = false;
    private static ArrayList<Integer> datos = new ArrayList<Integer>();
    private static String espData = "";
    private BTLE_Device btle_device;
    private BluetoothDevice device;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGatt mBluetoothGatt;
    private static String MAC_ADDRESS = "";
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        choose = (TextView) findViewById(R.id.choose);

        temperature = (TextView) findViewById(R.id.temperature);
        co2 = (TextView) findViewById(R.id.co2);
        iLum = (TextView) findViewById(R.id.iLum);
        people = (TextView) findViewById(R.id.people);
        status = (TextView) findViewById(R.id.status);
        dataCard = (CardView) findViewById(R.id.data_card);
        dataCard.setVisibility(View.INVISIBLE);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        name = (TextView) findViewById(R.id.nombre);
        address = (TextView) findViewById(R.id.direccion);

    }

    protected void onResume() {
        super.onResume();
        if (getIntent().getParcelableExtra("Objeto") != null) {
            device = getIntent().getParcelableExtra("Objeto");

            choose.setVisibility(View.INVISIBLE);
            dataCard.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
            name.setText(device.getName());
            address.setText(device.getAddress());
            MAC_ADDRESS = device.getAddress();

            connect();

            status.setText("Conectado");
            status.setTextColor(Color.GREEN);

            mostrarDatos();
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (connected) {
                    mBluetoothGatt.disconnect();
                    connected = false;
                    button.setText("Conectar de nuevo");
                    status.setText("Desconectado");
                    status.setTextColor(Color.RED);
                } else {
                    connect();
                    button.setText("Desconectar");
                    status.setText("Conectado");
                    status.setTextColor(Color.GREEN);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actualizando) {
                    button2.setText("Actualizar");
                    actualizando = false;
                } else {
                    mostrarDatos();
                    button2.setText("Detener");
                    actualizando = true;
                }

            }
        });
    }

    public void mostrarDatos() {

        if (!datos.isEmpty() || !connected) {
            datos.clear();
            Log.i("Array: ", String.valueOf(datos.size()));
            temperature.setText(R.string.empty_text);
            iLum.setText(R.string.empty_text);
            co2.setText(R.string.empty_text);
            people.setText(R.string.empty_text);
        }
        try {
            if (espData.equals("")) {
                throw new ArrayIndexOutOfBoundsException();
            } else if (connected) {
                datos = datosToList(espData);
                temperature.setText((datos.get(0)) + " ºC");
                iLum.setText(datos.get(1) + " lux");
                co2.setText(datos.get(2) + " ppm");
                people.setText(datos.get(3) + " personas");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Utils.toast(getApplicationContext(), "El dispositivo " + device.getName() + " no está enviando datos.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        count++;
        Log.i("Actualizado: ", String.valueOf(count));
        refresh(1000);


    }

    public void connect() {
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connected = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();
                connected = false;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                final List<BluetoothGattService> services = mBluetoothGatt.getServices();
                if (services != null) {
                    List<BluetoothGattCharacteristic> characteristics = mBluetoothGatt.getService(UUID.fromString(S_UUID)).getCharacteristics();
                    for (int i = 0; i < services.size(); i++) {
                        Log.i("Servicio " + i, String.valueOf(services.get(i)));
                    }
                    mBluetoothGatt.readCharacteristic(characteristics.get(0));

                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                String newC = new String(data);
                espData = newC;
            } else if (status == GATT_INTERNAL_ERROR) {
                Log.e("Error de conexión", "Service discovery failed");
                gatt.disconnect();
                return;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastupdate("ACTION_DATA_AVAILABLE", characteristic);
        }
    };
//   Pablo, calbo

    private void broadcastupdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, characteristic.getValue());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
                return true;
            case R.id.logout:
                Utils.toast(this, "Esto cerrará sesión.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void refresh(int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mostrarDatos();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

    public static ArrayList<Integer> datosToList(String sDatos) {
        String[] strings = sDatos.split("/");
        ArrayList<Integer> intArrayList = new ArrayList<Integer>();
        for (int i = 0; i < strings.length; i++) {
            intArrayList.add(Integer.parseInt(strings[i]));
        }
        return intArrayList;
    }

}
