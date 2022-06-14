package com.example.fernando.smartcarcontrol;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class Reconocimiento extends AppCompatActivity {

    String app_id = "1da557f2";
    String api_key = "ee5b35e9ddbca381883a4dc7f7ae0563";

    KairosListener detListener, identlistener;

    Button enroll, identify;
    ImageView imageView;
    Bitmap image;
    Kairos myKairos;
    String galleryId = "people";

    TextView info;
    EditText name;
    String minHeadScale = "0.1";
    String multipleFaces = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocimiento);

        myKairos = new Kairos();
        Reconocimiento.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        imageView = findViewById(R.id.imageview);
        //enroll = findViewById(R.id.enroll);
        identify = findViewById(R.id.identify);
        info = findViewById(R.id.info);

        myKairos.setAuthentication(this, app_id, api_key);
        name = findViewById(R.id.name);

        detListener = new KairosListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Se registró correctamente", Toast.LENGTH_SHORT).show();
                info.setText("Bien!");
            }

            @Override
            public void onFail(String response) {
                Toast.makeText(getApplicationContext(), "Falló " + response, Toast.LENGTH_SHORT).show();
                info.setText("Falló " + response);
            }
        };

        identlistener = new KairosListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Success! You are: " + readJSONForName(response), Toast.LENGTH_SHORT).show();
                info.setText(readJSONForName(response));
                if(readJSONForName(response).equals("Jhan") || readJSONForName(response).equals("Erixon") || readJSONForName(response).equals("Fernando")) {
                    Intent nuevoFrom = new Intent(Reconocimiento.this, Principal.class);
                    startActivity(nuevoFrom);
                }else{
                    Toast.makeText(getApplicationContext(), "USUARIO INCORRECTO", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(String response) {
                Toast.makeText(getApplicationContext(), "Falló " + response, Toast.LENGTH_SHORT).show();
                info.setText("Falló " + response);
            }
        };

        /*enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getApplicationContext(), "Ingresar un nombre antes de tomar la foto", Toast.LENGTH_LONG).show();
                    }else{
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,2);
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error Message: " + e.getMessage() + ". Cause: " + e.getCause(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Reconocimiento.this, new String[]{Manifest.permission.CAMERA}, 100);
                    }else{
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,1);
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error Message: " + e.getMessage() + ", Cause: " + e.getCause(), Toast.LENGTH_SHORT).show();
                    info.setText("Error Message: " + e.getMessage() + ", Cause: " + e.getCause());
                }
            }
        });
    }

    private String readJSONForName(String response){
        String match = "";
        int location = response.indexOf("subject_id");
        match = response.substring(location + 13);
        String name = "";

        for(int i = 0; i < match.length(); i++){
            if(match.charAt(i) == '"')
                break;
            else
                name += Character.toString(match.charAt(i));
        }

        return name;
    }

    private void identifyImage() throws UnsupportedEncodingException, JSONException{
        String selector = "FULL";

        myKairos.recognize(image, galleryId, selector, null, minHeadScale, null, identlistener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.info){
            new AlertDialog.Builder(Reconocimiento.this)
                    .setTitle("About This App")
                    .setMessage("This app makes use of the Kairos for Android to implement facial recognition")
                    .setCancelable(true)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    }).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void enrollImage() throws UnsupportedEncodingException, JSONException {
        if(name.getText().toString() == null){
            Toast.makeText(getApplicationContext(), "Nombe vacío", Toast.LENGTH_SHORT).show();
        }else{
            String subjectId = name.getText().toString();
            String selector = "FULL";
            myKairos.enroll(image, subjectId, galleryId, selector, multipleFaces, minHeadScale, detListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK){
            image = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(image);

            try{
                enrollImage();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == 1 && resultCode == RESULT_OK){
            image = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(image);

            try{
                identifyImage();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
