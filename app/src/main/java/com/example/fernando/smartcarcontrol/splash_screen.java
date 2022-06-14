package com.example.fernando.smartcarcontrol;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class splash_screen extends AppCompatActivity {

    public static final int segundos = 8;
    public static final int milisegundos = segundos*1000;
    public static final int delay = 2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = (ProgressBar) findViewById(R.id.progressB);
        progressBar.setMax(maximoProgreso());
        empezarAnimacion();
    }

    public void empezarAnimacion(){
        new CountDownTimer(milisegundos, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(establecerProgreso(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                Intent nuevoFrom = new Intent(splash_screen.this, Principal.class);
                startActivity(nuevoFrom);
                finish();
            }
        }.start();
    }

    public int establecerProgreso(long miliseconds){
        return (int)(milisegundos - miliseconds)/1000;
    }

    public int maximoProgreso(){
        return segundos - delay;
    }
}
