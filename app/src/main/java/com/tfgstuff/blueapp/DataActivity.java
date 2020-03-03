package com.tfgstuff.blueapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DataActivity extends AppCompatActivity {

    private TextView name, address;

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
            } else {
                name.setText(extras.getString("Name"));
                address.setText(extras.getString("Address"));
            }
        } else {
            name.setText((String) savedInstanceState.getSerializable("Name"));
            address.setText((String) savedInstanceState.getSerializable("Address"));
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
