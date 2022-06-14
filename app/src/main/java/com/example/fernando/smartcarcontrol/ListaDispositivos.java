package com.example.fernando.smartcarcontrol;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class ListaDispositivos extends ListActivity {

    private BluetoothAdapter bluetoothAdapter2 = null;

    static String DIRECCION_MAC = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        bluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivosApareados = bluetoothAdapter2.getBondedDevices();

        if(dispositivosApareados.size() > 0){
            for(BluetoothDevice dispositivo : dispositivosApareados){
                String nombreBT = dispositivo.getName();
                String direccionMAC = dispositivo.getAddress();
                ArrayBluetooth.add(nombreBT + "\n" + direccionMAC);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String informacionGeneral = ((TextView) v).getText().toString();

        String direccionMAC = informacionGeneral.substring(informacionGeneral.length()-17);

        Intent retornaMac = new Intent();
        retornaMac.putExtra(DIRECCION_MAC, direccionMAC);
        setResult(RESULT_OK, retornaMac);
        finish();
    }

}
