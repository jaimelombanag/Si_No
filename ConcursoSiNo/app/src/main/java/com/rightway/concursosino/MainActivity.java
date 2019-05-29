package com.rightway.concursosino;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rightway.concursosino.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "SiNo";
    private final String ACTION_STRING_ACTIVITY = "ToActivity";
    private String file = "IP_Direccion.txt";
    private String file2 = "ID_Save.txt";
    private int REQUEST_PERMISSION =1;
    private int REQUEST_PERMISSION2 =2;
    private Timer multifuncion = new Timer();
    private int contadorPregunta;
    private ConnexionTCP sendData;
    private TextView txt_pregunta;
    private Button btn_si;
    private Button btn_no;
    private Typeface script;
    private boolean bloqueo = false;




    private final BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cmd = intent.getStringExtra("CMD");
            String datos = intent.getStringExtra("DATOS");


            if(cmd.equalsIgnoreCase("0")){

                Gson gson=new Gson();
                DatosTransferDTO informacion = null;
                try {
                    informacion = gson.fromJson(datos, DatosTransferDTO.class);
                    txt_pregunta.setText(informacion.getPregunta());

                    if(informacion.getAccion().equalsIgnoreCase("0")){
                        bloqueo = true;
                    }else if(informacion.getAccion().equalsIgnoreCase("1")){
                        bloqueo = false;
                    }else if(informacion.getAccion().equalsIgnoreCase("2")){
                        bloqueo = false;
                        btn_si.setBackgroundResource(R.drawable.boton_si);
                        btn_no.setBackgroundResource(R.drawable.boton_no);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(cmd.equalsIgnoreCase("1")){

            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (activityReceiver != null) {
            try {
                registerReceiver(activityReceiver, new IntentFilter(ACTION_STRING_ACTIVITY));
            } catch (Exception e) {
            }
        }

        txt_pregunta = (TextView) findViewById(R.id.txt_pregunta);
        btn_si = (Button) findViewById(R.id.btn_si);
        btn_no = (Button) findViewById(R.id.btn_no);

        /*******************************Para que La pantalla no se apague*********************/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Permisos();


        String fuente = "fuentes/condensed_bold.ttf";
        this.script = Typeface.createFromAsset(getAssets(),fuente);
        txt_pregunta.setTypeface(script);

        LeerIp();
        LeerId();
        //startTimer();


    }

    /**********************************************************************************************/

    public void BotonSi(View v){

        if(bloqueo){

        }else{
            bloqueo = true;
            btn_si.setBackgroundResource(R.drawable.boton_si_over);
            btn_no.setBackgroundResource(R.drawable.boton_no_2);


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            DatosTransferDTO datosTransferDTO = new DatosTransferDTO();
            datosTransferDTO.setFuncion("1");
            datosTransferDTO.setIdTableta(sharedPreferences.getString(constantes.IdConcursante, ""));
            datosTransferDTO.setSiNo("SI");

            Gson gson = new Gson();
            String json = gson.toJson(datosTransferDTO);
            Log.i(TAG, "=====Debe enviar:  "  +  json);

            sendData = new ConnexionTCP(getApplicationContext());
            sendData.sendData(json);

        }

    }

    public void BotonNo(View v){

        if(bloqueo){

        }else{
            bloqueo = true;
            btn_no.setBackgroundResource(R.drawable.boton_no_over);
            btn_si.setBackgroundResource(R.drawable.boton_si_2);


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            DatosTransferDTO datosTransferDTO = new DatosTransferDTO();
            datosTransferDTO.setFuncion("1");
            datosTransferDTO.setIdTableta(sharedPreferences.getString(constantes.IdConcursante, ""));
            datosTransferDTO.setSiNo("NO");

            Gson gson = new Gson();
            String json = gson.toJson(datosTransferDTO);
            Log.i(TAG, "=====Debe enviar:  "  +  json);

            sendData = new ConnexionTCP(getApplicationContext());
            sendData.sendData(json);
        }
    }

    private void startTimer(){
        try {
            multifuncion.scheduleAtFixedRate(new SendMultifuncion(), 0, 1000);
        }catch (Exception e){
            stopTimer();
            ReStartTimer();
            e.printStackTrace();
        }

    }
    public void ReStartTimer(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startTimer();


            }
        }, 2000);
    }
    private void stopTimer(){
        multifuncion.cancel();
    }

    private class SendMultifuncion extends TimerTask {
        public void run() {
            contadorPregunta++;
            if(contadorPregunta > 10){
                contadorPregunta = 0;


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                DatosTransferDTO datosTransferDTO = new DatosTransferDTO();
                datosTransferDTO.setFuncion("0");
                datosTransferDTO.setIdTableta(sharedPreferences.getString(constantes.IdConcursante, ""));

                Gson gson = new Gson();
                String json = gson.toJson(datosTransferDTO);
                Log.i(TAG, "=====DEbe enviar:  "  +  json);

                sendData = new ConnexionTCP(getApplicationContext());
                sendData.sendData(json);

            }
        }

    }

    /**********************************************************************************************/
    public void LeerIp(){
        String line = null;

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(Environment.getExternalStorageDirectory() + "/Download/" + file));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            line = line.replace(" ", "");
            line = line.replace("\r\n", "");
            line = line.replace("\r", "");
            line = line.replace("\n", "");


            Log.i(TAG, "-----Lo q se lee es"  +  line + "-----");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(line.equalsIgnoreCase("1.1.1.1")){
                editor.putString(constantes.IPSocket, "192.168.122.100");
            }else{
                editor.putString(constantes.IPSocket, line);
            }
            editor.commit();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
    }
    public void LeerId(){
        String line = null;

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(Environment.getExternalStorageDirectory() + "/Download/" + file2));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            line = line.replace(" ", "");
            line = line.replace("\r\n", "");
            line = line.replace("\r", "");
            line = line.replace("\n", "");


            Log.i(TAG, "-----Lo q se lee es"  +  line + "-----");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(constantes.IdConcursante, line);

            editor.commit();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                MuestraAlertMensaje("Cerrar Aplicación.", "Desea cerrar la aplicación?");
                return true;
            case KeyEvent.KEYCODE_HOME:
                Log.i(TAG, "Se Oprimio el Boton de Back");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "resumeeeen");

        if (activityReceiver != null) {
            registerReceiver(activityReceiver, new IntentFilter(ACTION_STRING_ACTIVITY));
        }
    }

    @Override
    public void finish() {
        super.finish();
        unregisterReceiver(activityReceiver);
        Intent data = new Intent();
        setResult(Activity.RESULT_CANCELED, data);
        multifuncion.cancel();
    }

    /**********************************************************************************************/
    public void Permisos(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);

            //return;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION2);

            return;
        }
    }

    /**********************************************************************************************/
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // User refused to grant permission.
            }
        }else  if (requestCode == REQUEST_PERMISSION2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // User refused to grant permission.
            }
        }
    }
    /*============================================================================================*/
    /*============================================================================================*/
    public void MuestraAlertMensaje(String titulo, String mensaje) {
        Log.i(TAG, " ================ SE MUESTRA MENSAJE EN ALERT : " + mensaje);
        ArrayList<Integer> idRoom = new ArrayList<>();
        try {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(titulo);
            if(mensaje==null) alertBuilder.setMessage("timeout intente de nuevo.");
            else alertBuilder.setMessage(mensaje);
            alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            alertBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    multifuncion.cancel();
                    finish();

                }
            });

            alertBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
