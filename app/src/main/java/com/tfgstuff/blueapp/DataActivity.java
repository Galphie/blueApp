package com.tfgstuff.blueapp;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.tfgstuff.blueapp.R.layout.activity_data;

public class DataActivity extends AppCompatActivity {

    private static final int GATT_INTERNAL_ERROR = 129;
    private static final String SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    private static final String CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
    private static final String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    private static boolean connected;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Datos data = new Datos();
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;

    private TextView name, address, choose, status, temperature, co2, iLum, people;
    private ImageView tempAlert, lumAlert, co2Alert;
    private CardView dataCard;
    private Button connectButton, registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        choose = findViewById(R.id.choose);

        temperature = findViewById(R.id.temperature);
        co2 = findViewById(R.id.co2);
        iLum = findViewById(R.id.iLum);
        people = findViewById(R.id.people);
        status = findViewById(R.id.status);
        tempAlert = findViewById(R.id.tempAlert);
        lumAlert = findViewById(R.id.lumAlert);
        co2Alert = findViewById(R.id.co2Alert);
        tempAlert.setVisibility(View.INVISIBLE);
        lumAlert.setVisibility(View.INVISIBLE);
        co2Alert.setVisibility(View.INVISIBLE);
        dataCard = findViewById(R.id.data_card);
        dataCard.setVisibility(View.INVISIBLE);
        connectButton = findViewById(R.id.button);
        registerButton = findViewById(R.id.activity_data_register_button);
        name = findViewById(R.id.nombre);
        address = findViewById(R.id.direccion);

        connectButton.setOnClickListener(v -> {
            if (connected) {
                bluetoothGatt.disconnect();
                connected = false;
                status.setText("Desconectado");
                status.setTextColor(Color.RED);
                connectButton.setText("Conectar de nuevo");


            } else {
                connect();
                connectButton.setText("Desconectar");
                status.setText("Conectado");
                status.setTextColor(Color.GREEN);
            }
        });
        registerButton.setOnClickListener(v -> register());


    }

    protected void onResume() {
        super.onResume();
        if (getIntent().getParcelableExtra("object") != null) {
            bluetoothDevice = getIntent().getParcelableExtra("object");

            choose.setVisibility(View.INVISIBLE);
            dataCard.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            name.setText(bluetoothDevice.getName());
            address.setText(bluetoothDevice.getAddress());

            connect();

            status.setText("Conectado");
            status.setTextColor(Color.GREEN);

        }

    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothGatt.disconnect();
    }

    public void showData() {

        if (!connected) {
            temperature.setText(R.string.empty_text);
            iLum.setText(R.string.empty_text);
            co2.setText(R.string.empty_text);
            people.setText(R.string.empty_text);
            lumAlert.setVisibility(View.INVISIBLE);
            co2Alert.setVisibility(View.INVISIBLE);
            tempAlert.setVisibility(View.INVISIBLE);

        } else {
            lumAlert.setVisibility(View.VISIBLE);
            co2Alert.setVisibility(View.VISIBLE);
            tempAlert.setVisibility(View.VISIBLE);

            temperature.setText(String.valueOf(data.getTemperature()));
            iLum.setText(String.valueOf(data.getLux()));
            co2.setText(String.valueOf(data.getCo2()));
            people.setText(String.valueOf(data.getPeople())) ;

            checkData();
        }
    }

    private void checkData() {
        if (data.getTemperature() >= 37) {
            tempAlert.setImageResource(R.drawable.ic_brightness_high_black_24dp);
        } else if (data.getTemperature() >= 30 && data.getTemperature() < 37) {
            tempAlert.setImageResource(R.drawable.ic_brightness_warning);
        } else if (data.getTemperature() >= 20 && data.getTemperature() < 30) {
            tempAlert.setImageResource(R.drawable.ic_done_black_24dp);
        } else if (data.getTemperature() < 20) {
            tempAlert.setImageResource(R.drawable.ic_ac_unit_black_24dp);
        }

        if (data.getLux() >= 1000) {
            lumAlert.setImageResource(R.drawable.ic_brightness_1_black_24dp);
        } else if (data.getLux() >= 650 && data.getLux() < 1000) {
            lumAlert.setImageResource(R.drawable.ic_brightness_2_black_24dp);
        } else if (data.getLux() < 650) {
            lumAlert.setImageResource(R.drawable.ic_brightness_3_black_24dp);
        }

        if (data.getCo2() >= 1000) {
            co2Alert.setImageResource(R.drawable.ic_wb_cloudy_black_24dp);
        } else if (data.getCo2() >= 900 && data.getCo2() < 1000) {
            co2Alert.setImageResource(R.drawable.ic_warning_black_24dp);
        } else if (data.getCo2() < 900) {
            co2Alert.setImageResource(R.drawable.ic_done_black_24dp);
        }
    }

    public void connect() {
        bluetoothGatt = bluetoothDevice.connectGatt(this, false, mGattCallback);
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
                final List<BluetoothGattService> services = bluetoothGatt.getServices();
                if (services != null) {
                    BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(SERVICE_UUID));
                    BluetoothGattCharacteristic characteristic = service
                            .getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
                    bluetoothGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                            UUID.fromString(DESCRIPTOR_UUID));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(descriptor);
                    bluetoothGatt.readCharacteristic(characteristic);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                String[] valores = new String(data).split("/");
                createDataObject(valores);
                runOnUiThread(() -> showData());
                register();
            } else if (status == GATT_INTERNAL_ERROR) {
                Log.e("Error de conexión", "Error en el proceso de descubrimiento de servicios.");
                gatt.disconnect();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            bluetoothGatt.readCharacteristic(characteristic);
        }
    };

    private void createDataObject(String[] valores) {
        for (int i = 0; i < valores.length; i++) {
            if (valores[i].startsWith("T")) {
                valores[i] = valores[i].replace("T", "");
                data.setTemperature(valores[i]);
            } else if (valores[i].startsWith("L")) {
                valores[i] = valores[i].replace("L", "");
                data.setLux(valores[i]);
            } else if (valores[i].startsWith("C")) {
                valores[i] = valores[i].replace("C", "");
                data.setCo2(valores[i]);
            } else if (valores[i].startsWith("P")) {
                valores[i] = valores[i].replace("P", "");
                data.setPeople(valores[i]);
            }
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
                Intent dev = new Intent(this, ScanActivity.class);
                startActivity(dev);
                return true;
            case R.id.graphics:
                Utils.toast(this, "Esto abrirá la actividad Gráficos.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void register() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm:ss");
        String now = LocalDateTime.now().format(formatter);
        DatabaseReference dataRef = database.getReference().child(now);
        dataRef.setValue(data);
    }
}

