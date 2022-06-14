package com.example.fernando.smartcarcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Lunas extends AppCompatActivity {
    String estado1 = "abrir";
    String estado2 = "abrir";
    String estado3 = "abrir";
    String estado4 = "abrir";
    Button btnPiloto, btnCopiloto, btnIzqPost, btnDerPost;
    boolean conexion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunas);

        Bundle datos = getIntent().getExtras();
        conexion = datos.getBoolean("conexionBT");
        btnPiloto = (Button)findViewById(R.id.btnPiloto);
        btnCopiloto = (Button)findViewById(R.id.btnCopiloto);
        btnIzqPost = (Button)findViewById(R.id.btnIzqPost);
        btnDerPost = (Button)findViewById(R.id.btnDerPost);
    }

    public void lunaPiloto(View view){
        if(conexion) {
            if (estado1 == "cerrar") {
                btnPiloto.setText("ABRIR");
                estado1 = "abrir";
                Principal.getConnectedThread().enviar("piloto0");
            } else {
                btnPiloto.setText("CERRAR");
                estado1 = "cerrar";
                Principal.getConnectedThread().enviar("piloto1");
            }
        }
    }

    public void lunaCopiloto(View view){
        if(conexion) {
            if (estado2 == "cerrar") {
                btnCopiloto.setText("ABRIR");
                estado2 = "abrir";
                Principal.getConnectedThread().enviar("cop0");
            } else {
                btnCopiloto.setText("CERRAR");
                estado2 = "cerrar";
                Principal.getConnectedThread().enviar("cop1");
            }
        }
    }

    public void lunaIzqPost(View view){
        if(conexion) {
            if (estado3 == "cerrar") {
                btnIzqPost.setText("ABRIR");
                estado3 = "abrir";
                Principal.getConnectedThread().enviar("izqpost0");
            } else {
                btnIzqPost.setText("CERRAR");
                estado3 = "cerrar";
                Principal.getConnectedThread().enviar("izqpost1");
            }
        }
    }

    public void lunaDerPost(View view){
        if(conexion) {
            if (estado4 == "cerrar") {
                btnDerPost.setText("ABRIR");
                estado4 = "abrir";
                Principal.getConnectedThread().enviar("derpost0");
            } else {
                btnDerPost.setText("CERRAR");
                estado4 = "cerrar";
                Principal.getConnectedThread().enviar("derpost1");
            }
        }
    }
}
