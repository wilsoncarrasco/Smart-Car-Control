package com.example.fernando.smartcarcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Luces extends AppCompatActivity {

    boolean conexion = false;
    Button btnLucesEmergencia, btnLucesEstacionamiento;
    String estado1 = "encender";
    String estado2 = "encender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luces);

        Bundle datos = getIntent().getExtras();
        conexion = datos.getBoolean("conexionBT");
        btnLucesEmergencia = (Button)findViewById(R.id.btnLucesEmergencia);
        btnLucesEstacionamiento = (Button)findViewById(R.id.btnLucesEstacionamiento);
    }

    public void lucesEstacionamiento(View view){
        if(conexion){
            if(estado1.equals("encender")){
                btnLucesEmergencia.setText("APAGAR");
                estado1 = "apagar";
                Principal.getConnectedThread().enviar("estacionamiento1");
            }else{
                btnLucesEmergencia.setText("ENCENDER");
                estado1 = "encender";
                Principal.getConnectedThread().enviar("estacionamiento0");
            }
        }
    }

    public void lucesFaros(View view){
        if(conexion){
            if(estado2.equals("encender")){
                btnLucesEstacionamiento.setText("APAGAR");
                estado2 = "apagar";
                Principal.getConnectedThread().enviar("faros1");
            }else{
                btnLucesEstacionamiento.setText("ENCENDER");
                estado2 = "encender";
                Principal.getConnectedThread().enviar("faros0");
            }
        }
    }
}
