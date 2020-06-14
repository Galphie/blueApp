package com.tfgstuff.blueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {

    private static final int ALL_TYPE = 82;
    private static final int TEMPERATURE_TYPE = 13;
    private static final int CO2_TYPE = 12;
    private static final int LUX_TYPE = 5;
    private static final int PEOPLE_TYPE = 14;

    private ArrayList<Registro> registros = new ArrayList<>();

    private TabLayout tabLayout;
    private LineChart lineChart;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference deviceRef;
    private String macAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        macAddress = getIntent().getExtras().getString("mac_address");
        deviceRef = database.getReference().child(macAddress);
        deviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registros.clear();
                for (DataSnapshot year : dataSnapshot.getChildren()) {
                    for (DataSnapshot month : year.getChildren()) {
                        for (DataSnapshot day : month.getChildren()) {
                            for (DataSnapshot hour : day.getChildren()) {
                                for (DataSnapshot register : hour.getChildren()) {
                                    Datos datos = new Datos(
                                            register.getValue(Datos.class).getTemperature(),
                                            register.getValue(Datos.class).getCo2(),
                                            register.getValue(Datos.class).getPeople(),
                                            register.getValue(Datos.class).getLux()
                                    );
                                    String fecha = year.getKey()
                                            + "/" + month.getKey()
                                            + "/" + day.getKey()
                                            + "/" + hour.getKey()
                                            + "/" + register.getKey();
                                    Registro registro = new Registro(datos, fecha);
                                    registros.add(registro);
                                }
                            }
                        }
                    }
                }
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        setData(ALL_TYPE);
                        break;
                    case 1:
                        setData(TEMPERATURE_TYPE);
                        break;
                    case 2:
                        setData(CO2_TYPE);
                        break;
                    case 3:
                        setData(LUX_TYPE);
                        break;
                    case 4:
                        setData(PEOPLE_TYPE);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        lineChart = findViewById(R.id.chart_activity_line_chart);
        tabLayout = findViewById(R.id.chart_tab_layout);
        lineChart.setScaleYEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        setData(ALL_TYPE);
                        break;
                    case 1:
                        setData(TEMPERATURE_TYPE);
                        break;
                    case 2:
                        setData(CO2_TYPE);
                        break;
                    case 3:
                        setData(LUX_TYPE);
                        break;
                    case 4:
                        setData(PEOPLE_TYPE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setData(int type) {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();

        LineDataSet temperatureData = new LineDataSet(lineChartDataSet(TEMPERATURE_TYPE), "Temperatura");
        temperatureData.setColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        LineDataSet co2Data = new LineDataSet(lineChartDataSet(CO2_TYPE), "CO2");
        co2Data.setColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
        LineDataSet luxData = new LineDataSet(lineChartDataSet(LUX_TYPE), "Intensidad lumínica");
        luxData.setColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));
        LineDataSet peopleData = new LineDataSet(lineChartDataSet(PEOPLE_TYPE), "Personas");
        peopleData.setColor(ResourcesCompat.getColor(getResources(), R.color.purple, null));

        switch (type) {
            case ALL_TYPE:
                iLineDataSets.add(temperatureData);
                iLineDataSets.add(co2Data);
                iLineDataSets.add(luxData);
                iLineDataSets.add(peopleData);
                break;
            case TEMPERATURE_TYPE:
                iLineDataSets.add(temperatureData);
                break;
            case CO2_TYPE:
                iLineDataSets.add(co2Data);
                break;
            case LUX_TYPE:
                iLineDataSets.add(luxData);
                break;
            case PEOPLE_TYPE:
                iLineDataSets.add(peopleData);
                break;

        }
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private ArrayList<Entry> lineChartDataSet(int type) {
        ArrayList<Entry> dataSet = new ArrayList<>();
        switch (type) {
            case TEMPERATURE_TYPE:
                for (int i = 0; i < registros.size(); i++) {
                    dataSet.add(new Entry(i, Integer.parseInt(registros.get(i).getDatos().getTemperature())));
                }
                break;
            case CO2_TYPE:
                for (int i = 0; i < registros.size(); i++) {
                    dataSet.add(new Entry(i, Integer.parseInt(registros.get(i).getDatos().getCo2())));
                }
                break;
            case LUX_TYPE:
                for (int i = 0; i < registros.size(); i++) {
                    dataSet.add(new Entry(i, Integer.parseInt(registros.get(i).getDatos().getLux())));
                }
                break;
            case PEOPLE_TYPE:
                for (int i = 0; i < registros.size(); i++) {
                    dataSet.add(new Entry(i, Integer.parseInt(registros.get(i).getDatos().getPeople())));
                }
                break;
        }
        return dataSet;
    }
}