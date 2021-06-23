package com.example.psicoayuda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.psicoayuda.R;
import com.example.psicoayuda.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.objectweb.asm.Label;

public class PreguntasActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor giroscopio;
    private SensorManager sensorManager;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    boolean sacudido = false;
    private  int contadorPreguntas;
    private String respuesta="";

    private static final String URL_REGISTRAR_EVENTO = "http://so-unlam.net.ar/api/api/event";
    private static final String environment = "PROD";
    private static final String typeEvent = "Lectura del sensor giroscopio";
    private String description;
    public IntentFilter filter;
    private Receptor receiver = new Receptor();
    private TextView resultEditText;
    private String action = "REGISTRAR_EVENTO";

    String preguntas[] = {"¿Recibiste ayuda psicológica alguna vez?","¿Estas siendo atentido por algún psicologo?","¿Te interesa ser contactado para resolver tu consulta?"};
    private TextView leyenda;
    private TextView pregunta;
    private TextView resultado;
    private TextView resultServer;
    float lastZ=0;
    String token;
    String email;
    String tokenRefresh;
    int flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preguntas);
        leyenda = findViewById(R.id.leyenda);
        pregunta = findViewById(R.id.pregunta);
        resultado = findViewById(R.id.txtResultado);
        resultServer = findViewById(R.id.txtRespuestaServer);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, giroscopio , 3000000);

        contadorPreguntas = 0;
        flag = 0;

        pregunta.setText(preguntas[contadorPreguntas]);

        configurarBroadcastReceiver();

        Intent intentAppPrincipal = getIntent();

        token = intentAppPrincipal.getExtras().getString("tokenPreg");
        tokenRefresh = intentAppPrincipal.getExtras().getString("token_rfrsPreg");
        email = intentAppPrincipal.getExtras().getString("mail");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String descrip;
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            if(contadorPreguntas>2){
                if(flag == 0) {
                    Toast.makeText(PreguntasActivity.this, "Respondiste todas las preguntas", Toast.LENGTH_SHORT).show();
                    pregunta.setText(" ");
                    resultado.setText("Respondiste todas las preguntas");
                    flag = 1;
                }
            }else{
                pregunta.setText(preguntas[contadorPreguntas]);
                if((event.values[2]) > 0.5f) {
                    respuesta="SI";
                    resultado.setText("Tu respuesta fue " + respuesta);
                    contadorPreguntas++;
                    descrip=Float.toString (event.values[2]);
                    registrarEvento(descrip);
                } else if((event.values[0]) < -0.5f) {
                    respuesta="NO";
                    resultado.setText("Tu respuesta fue " + respuesta);
                    contadorPreguntas++;
                    descrip=Float.toString (event.values[0]);
                    registrarEvento(descrip);
                }
            }

        }
    }

    void registrarEvento(String valor){
        JSONObject RegistarEvtJson = new JSONObject();
        try {
            RegistarEvtJson.put("env", environment);
            RegistarEvtJson.put("type_events",typeEvent);
            RegistarEvtJson.put("description", valor);
            Intent RegistrarEvento = new Intent(PreguntasActivity.this, ServicesHttp_POST.class);
            RegistrarEvento.putExtra("url", URL_REGISTRAR_EVENTO);
            RegistrarEvento.putExtra("datosJson", RegistarEvtJson.toString());
            RegistrarEvento.putExtra("action", action);
            RegistrarEvento.putExtra("tokenAP", token);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {

                Toast.makeText(PreguntasActivity.this, "Está conectado a internet", Toast.LENGTH_SHORT).show();
                startService(RegistrarEvento);
            }
            else{
                Toast.makeText(PreguntasActivity.this, "No está conectado a internet", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void configurarBroadcastReceiver(){
        filter = new IntentFilter("com.example.psicoayuda.intent.action.REGISTRAR_EVENTO");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver,filter);
    }

    private class Receptor extends BroadcastReceiver
    {

        public void onReceive(Context context, Intent intent){
            try
            {
                String tipo = intent.getStringExtra("tipo");
                resultServer = (TextView) findViewById(R.id.txtRespuestaServer);
                String datosJSON = intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJSON);
                resultServer.setText(datosJSON);
                Toast.makeText(getApplicationContext(),"Se recibió la respuesta del server",Toast.LENGTH_LONG).show();
                String resultado = datosJson.getString("success");
                if(tipo.equals("POST")){

                    if (resultado == "true")
                    {
                        Toast.makeText(getApplicationContext(),"Se registró la lectura correctamente",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No se registró la lectura correctamente",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    if (resultado == "true")
                    {
                        Toast.makeText(getApplicationContext(),"Se ha refrescado el token correctamente",Toast.LENGTH_LONG).show();
                        token = datosJson.getString("token");
                        tokenRefresh = datosJson.getString("token");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No se ha refrescado el token",Toast.LENGTH_LONG).show();
                    }

                }
            }
            catch (JSONException js){
                js.printStackTrace();
            }
        }
    }
    public static class MyAlarmReceiver extends BroadcastReceiver {

        public  MyAlarmReceiver()
        {
            super();
        }

        //Cuando reciba respuesta de la alarma, se ejecutará el PUT para refrescar el Token
        @Override
        public void onReceive(Context context, Intent intent) {

            String uri= intent.getStringExtra("uri");
            String token_refresh = intent.getStringExtra("refresh");
            Intent  putService = new Intent(context, ServicesHttp_PUT.class);
            putService.putExtra("tokenPut_rfrsh",token_refresh);
            putService.putExtra("urlPUT",uri);
            context.startService(putService);

        }
    }


};


