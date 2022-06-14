package com.example.fernando.smartcarcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Puertas extends AppCompatActivity {
    Button btnPiloto;
    String estado = "cerrar";

    boolean conexion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puertas);

        Bundle datos = getIntent().getExtras();
        conexion = datos.getBoolean("conexionBT");
        btnPiloto = (Button)findViewById(R.id.btnPuertas);
    }


    public void estadoPuertas(View view){
        if(conexion) {
            if (estado.equals("cerrar")) {
                btnPiloto.setText("Abrir");
                estado = "abrir";
                Principal.getConnectedThread().enviar("puertas0");
            } else {
                btnPiloto.setText("Cerrar");
                estado = "cerrar";
                Principal.getConnectedThread().enviar("puertas1");
            }
        }
    }

}
