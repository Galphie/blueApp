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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.tfgstuff.blueapp.R.layout.activity_data;

public class DataActivity extends AppCompatActivity {

    private static final int GATT_INTERNAL_ERROR = 129;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRef = database.getReference("Datos");
    private static final String SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    private static final String CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
    private static final String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    private TextView name, address, choose, status, temperature, co2, iLum, people;
    private ImageView tempAlert, lumAlert, co2Alert;
    private CardView dataCard;
    private Button connectButton;
    private static boolean connected;
    private static ArrayList<Integer> datos = new ArrayList<Integer>();
    private static String data = "";
    private Datos dat = new Datos();
    public static String degrees,dioxide,light,person;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    boolean enabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        choose = (TextView) findViewById(R.id.choose);

        temperature = (TextView) findViewById(R.id.temperature);
        co2 = (TextView) findViewById(R.id.co2);
        iLum = (TextView) findViewById(R.id.iLum);
        people = (TextView) findViewById(R.id.people);
        status = (TextView) findViewById(R.id.status);
        tempAlert = (ImageView) findViewById(R.id.tempAlert);
        lumAlert = (ImageView) findViewById(R.id.lumAlert);
        co2Alert = (ImageView) findViewById(R.id.co2Alert);
        tempAlert.setVisibility(View.INVISIBLE);
        lumAlert.setVisibility(View.INVISIBLE);
        co2Alert.setVisibility(View.INVISIBLE);
        dataCard = (CardView) findViewById(R.id.data_card);
        dataCard.setVisibility(View.INVISIBLE);
        connectButton = (Button) findViewById(R.id.button);
        connectButton.setVisibility(View.INVISIBLE);
        name = (TextView) findViewById(R.id.nombre);
        address = (TextView) findViewById(R.id.direccion);

        Date currentTime = Calendar.getInstance().getTime();


    }

    protected void onResume() {
        super.onResume();
        if (getIntent().getParcelableExtra("object") != null) {
            bluetoothDevice = getIntent().getParcelableExtra("object");

            choose.setVisibility(View.INVISIBLE);
            dataCard.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.VISIBLE);
            name.setText(bluetoothDevice.getName());
            address.setText(bluetoothDevice.getAddress());

            connect();

            status.setText("Conectado");
            status.setTextColor(Color.GREEN);

        }

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

        if (!datos.isEmpty() || !connected) {
            datos.clear();
            temperature.setText(R.string.empty_text);
            iLum.setText(R.string.empty_text);
            co2.setText(R.string.empty_text);
            people.setText(R.string.empty_text);
            lumAlert.setVisibility(View.INVISIBLE);
            co2Alert.setVisibility(View.INVISIBLE);
            tempAlert.setVisibility(View.INVISIBLE);

        }
        try {
            if (data.equals("")) {
                throw new ArrayIndexOutOfBoundsException();
            } else if (connected) {
                String[] valores = data.split("/");
                for (int i = 0; i < valores.length; i++) {
                    if (valores[i].startsWith("T")) {
                        valores[i] = valores[i].replace("T", "");
                        dat.setTemperature(valores[i]);
                        temperature.setText(dat.getStringTemperature());
                        tempAlert.setVisibility(View.VISIBLE);
                        if (dat.getTemperature() >= 37) {
                            tempAlert.setImageResource(R.drawable.ic_brightness_high_black_24dp);
                        } else if (dat.getTemperature() >= 30 && dat.getTemperature() < 37) {
                            tempAlert.setImageResource(R.drawable.ic_brightness_warning);
                        } else if (dat.getTemperature() >= 20 && dat.getTemperature() < 30) {
                            tempAlert.setImageResource(R.drawable.ic_done_black_24dp);
                        } else if (dat.getTemperature() < 20) {
                            tempAlert.setImageResource(R.drawable.ic_ac_unit_black_24dp);
                        }
                    } else if (valores[i].startsWith("L")) {
                        valores[i] = valores[i].replace("L", "");
                        dat.setLux(valores[i]);
                        iLum.setText(dat.getStringLux());
                        lumAlert.setVisibility(View.VISIBLE);
                        if (dat.getLux() >= 1100) {
                            lumAlert.setImageResource(R.drawable.ic_brightness_1_black_24dp);
                        } else if (dat.getLux() >= 950 && dat.getLux() < 1100) {
                            lumAlert.setImageResource(R.drawable.ic_brightness_2_black_24dp);
                        } else if (dat.getLux() < 950) {
                            lumAlert.setImageResource(R.drawable.ic_brightness_3_black_24dp);
                        }
                    } else if (valores[i].startsWith("C")) {
                        valores[i] = valores[i].replace("C", "");
                        dat.setCo2(valores[i]);
                        co2.setText(dat.getStringCo2());
                        co2Alert.setVisibility(View.VISIBLE);
                        if (dat.getCo2() >= 1000) {
                            co2Alert.setImageResource(R.drawable.ic_wb_cloudy_black_24dp);
                        } else if (dat.getCo2() >= 900 && dat.getCo2() < 1000) {
                            co2Alert.setImageResource(R.drawable.ic_warning_black_24dp);
                        } else if (dat.getCo2() < 900) {
                            co2Alert.setImageResource(R.drawable.ic_done_black_24dp);
                        }
                    } else if (valores[i].startsWith("P")) {
                        valores[i] = valores[i].replace("P", "");
                        dat.setPeople(valores[i]);
                        people.setText(dat.getStringPeople());
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Utils.toast(getApplicationContext(),
                    "El dispositivo " + bluetoothDevice.getName() + " no está enviando datos.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
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
                    bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
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
                DataActivity.data = new String(data);
                runOnUiThread(() -> showData());
            } else if (status == GATT_INTERNAL_ERROR) {
                Log.e("Error de conexión", "Error en el proceso de descubrimiento de servicios.");
                gatt.disconnect();
                return;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            bluetoothGatt.readCharacteristic(characteristic);
        }
    };

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
//                Intent dev = new Intent(this, MainActivity.class);
                Intent dev = new Intent(this, ScanActivity.class);
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

    public void register(View view) {

    }
}
