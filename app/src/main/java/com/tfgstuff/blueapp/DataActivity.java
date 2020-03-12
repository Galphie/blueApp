package com.tfgstuff.blueapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataActivity extends AppCompatActivity {

    private static final int GATT_INTERNAL_ERROR = 129;
    private static final String S_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    private static final String C_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
    private static final String D_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    private TextView name, address, choose, status, temperature, co2, iLum, people;
    private CardView dataCard;
    private Button button;

    private static boolean connected;
    private static boolean actualizando = false;
    private static ArrayList<Integer> datos = new ArrayList<Integer>();
    private static String espData = "";
    private BTLE_Device btle_device;
    private BluetoothDevice device;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    boolean enabled = true;
    private static String MAC_ADDRESS = "";
    int count = 0;
    int charCount = 0;
    int aux = charCount;
    int backCounter = 0;


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
        button.setVisibility(View.INVISIBLE);
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
                    status.setText("Desconectado");
                    status.setTextColor(Color.RED);
                    button.setText("Conectar de nuevo");

                } else {
                    connect();
                    button.setText("Desconectar");
                    status.setText("Conectado");
                    status.setTextColor(Color.GREEN);
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
        Log.i("Mensaje:", espData);
        checkChanges(1000);


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
                    BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(UUID.fromString(S_UUID)).getCharacteristic(UUID.fromString(C_UUID));

                    mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                            UUID.fromString(D_UUID));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);

                    mBluetoothGatt.readCharacteristic(characteristic);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                espData = new String(data);

            } else if (status == GATT_INTERNAL_ERROR) {
                Log.e("Error de conexión", "Service discovery failed");
                gatt.disconnect();
                return;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            charCount++;
            Log.i("Característica cambia:", charCount + " vez.");
            mBluetoothGatt.readCharacteristic(characteristic);

        }
    };
//   Pablo, calbo

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

    private void checkChanges(int milliseconds) {
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
