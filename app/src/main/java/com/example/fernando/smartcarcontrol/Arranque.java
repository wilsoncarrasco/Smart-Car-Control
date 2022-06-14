package com.example.fernando.smartcarcontrol;

import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Arranque extends AppCompatActivity {

    boolean conexion = false;

    int contador = 0;

    Principal p = new Principal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arranque);

        Bundle datos = getIntent().getExtras();
        conexion = datos.getBoolean("conexionBT");
    }

    public void arrancarVehiculo(View view){
        contador++;
        if(conexion) {
            if (contador < 2) {
                Principal.getConnectedThread().enviar("arranque1");
                Toast.makeText(getApplicationContext(), "Encedió el auto", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "El vehiculo esta encendido", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No hay conexion", Toast.LENGTH_LONG).show();
        }
    }

    public void apagarVehiculo(View view){
        if (conexion){
            Principal.getConnectedThread().enviar("arranque0");
            contador = 0;
        }else{
            Toast.makeText(getApplicationContext(), "No hay conexion", Toast.LENGTH_LONG).show();
        }

        if(Principal.getEncendido() == false){
            Toast.makeText(getApplicationContext(), "Apago el auto", Toast.LENGTH_SHORT).show();
        }
    }
}


