package com.example.fernando.smartcarcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.UUID;

public class Principal extends AppCompatActivity{
    Button btnConectarBT;
    private static final int SOLICITA_ACTIVACION = 1;
    private static final int SOLICITA_CONEXION = 2;
    private static final int MESSAGE_READ = 3;
    public static ConnectedThread connectedThread;
    Handler mHandler;
    StringBuilder datosBluetooth = new StringBuilder();
    public static boolean encendido = false;
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothDevice bluetoothDevice = null;
    BluetoothSocket bluetoothSocket = null;
    boolean conexion =  false;
    private static String MAC = null;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        btnConectarBT = (Button)findViewById(R.id.btnBT);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Su dispositivo no posee bluetooth", Toast.LENGTH_LONG).show();
        }else if(!bluetoothAdapter.isEnabled()){
            Intent activaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activaBluetooth, SOLICITA_ACTIVACION);
        }
        btnConectarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conexion){
                    try{
                        bluetoothSocket.close();
                        conexion = false;
                        btnConectarBT.setText("Conectar Bluetooth");
                        Toast.makeText(getApplicationContext(), "El Bluetooth fue desconectado", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Ocurrio un error" + e, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Intent abreLista = new Intent(Principal.this, ListaDispositivos.class);
                    startActivityForResult(abreLista, SOLICITA_CONEXION);
                }
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if(msg.what == MESSAGE_READ){
                    String recibidos = (String)msg.obj;
                    datosBluetooth.append(recibidos);
                    int informacionFinal = datosBluetooth.indexOf("}");
                    if(informacionFinal > 0){
                        String datosCompletos = datosBluetooth.substring(0, informacionFinal);
                        int tamInformacion = datosCompletos.length();
                        if(datosBluetooth.charAt(0) == '{'){
                            String datosFinales = datosBluetooth.substring(1, tamInformacion);
                            Log.d("Recibidos", datosFinales);

                            if(datosFinales.contains("ON")){
                                Toast.makeText(getApplicationContext(), "Encendido", Toast.LENGTH_LONG).show();
                                encendido = true;
                            }
                            if(datosFinales.contains("OFF")){
                                Toast.makeText(getApplicationContext(), "Apagado", Toast.LENGTH_LONG).show();
                                encendido = false;
                            }
                        }
                        datosBluetooth.delete(0, datosBluetooth.length());
                    }
                }

            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SOLICITA_ACTIVACION:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "El Bluetooth ha sido activado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "El Bluetooth no ha sido activado", Toast.LENGTH_LONG).show();
                    finish();;
                }
                break;
            case SOLICITA_CONEXION:
                if(resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListaDispositivos.DIRECCION_MAC);
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);

                    try{
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                        bluetoothSocket.connect();
                        conexion = true;
                        connectedThread = new ConnectedThread(bluetoothSocket);
                        connectedThread.start();
                        btnConectarBT.setText("Desconectar Bluetooth");
                        Toast.makeText(getApplicationContext(), "Usted ha sido conectado con: " + MAC, Toast.LENGTH_LONG).show();
                    }catch (IOException e){
                        conexion = false;
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error" + e, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Falta obtener la direccion MAC", Toast.LENGTH_LONG).show();
                }
        }
    }

    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            while(true){
                try{
                    bytes = mmInStream.read(buffer);
                    String dadosBt = new String(buffer, 0, bytes);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBt).sendToTarget();
                }catch (IOException e){
                    break;
                }
            }
        }

        public void enviar(String datos) {
            byte[] msgBuffer = datos.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }
    }

    public void irArranque(View view){
        if(conexion) {
            Intent arranqueFrom = new Intent(this, Arranque.class);
            arranqueFrom.putExtra("conexionBT", conexion);
            startActivity(arranqueFrom);
        }else{
            Toast.makeText(getApplicationContext(), "Sin conexión Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    public void irLuces(View view){
        if(conexion) {
            Intent lucesFrom = new Intent(this, Luces.class);
            lucesFrom.putExtra("conexionBT", conexion);
            startActivity(lucesFrom);
        }else{
            Toast.makeText(getApplicationContext(), "Sin conexión Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    public void irLunas(View view){
        if(conexion) {
            Intent lunasFrom = new Intent(this, Lunas.class);
            lunasFrom.putExtra("conexionBT", conexion);
            startActivity(lunasFrom);
        }else{
            Toast.makeText(getApplicationContext(), "Sin conexión Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    public void irPuertas(View view){
        if(conexion) {
            Intent puertasFrom = new Intent(this, Puertas.class);
            puertasFrom.putExtra("conexionBT", conexion);
            startActivity(puertasFrom);
        }else {
            Toast.makeText(getApplicationContext(), "Sin conexión Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    public static ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public static boolean getEncendido(){
        return encendido;
    }
}
