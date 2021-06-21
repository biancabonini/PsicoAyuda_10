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

    String preguntas[] = {"pregunta1","pregunta2","pregunta3"};
    private TextView leyenda;
    private TextView pregunta;
    private TextView resultado;
    float lastZ=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preguntas);
        leyenda = findViewById(R.id.leyenda);
        pregunta = findViewById(R.id.pregunta);
        resultado = findViewById(R.id.txtResultado);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, giroscopio , 3000000);

        contadorPreguntas = 0;

        pregunta.setText("pregunta");

        configurarBroadcastReceiver();




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
                resultEditText = (TextView) findViewById(R.id.txtResultado);
                String datosJSON = intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJSON);

                resultEditText.setText(datosJSON);
                Toast.makeText(getApplicationContext(),"Se recibió la respuesta del server",Toast.LENGTH_LONG).show();
                String token = datosJson.getString("token");
                String resultado = datosJson.getString("success");
                String token_refresh = datosJson.getString("token_refresh");
                //Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),resultado,Toast.LENGTH_LONG).show();

                if (resultado == "true")
                {
                    Toast.makeText(getApplicationContext(),"Se registró la lectura correctamente",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No se registró la lectura correctamente",Toast.LENGTH_LONG).show();
                }

            }
            catch (JSONException js){
                js.printStackTrace();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            if(contadorPreguntas>2){
                Toast.makeText(PreguntasActivity.this, "Respondiste todas las preguntas", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(PreguntasActivity.this,AppPrincipal.class);
                //startActivity(intent);
            }else{
                if((event.values[2]) > 0.5f) {
                    respuesta="Si";
                    pregunta.setText("Tu respuesta fue" + preguntas[contadorPreguntas]);
                    contadorPreguntas++;
                    String descrip=Float.toString (event.values[2]);
                    JSONObject RegistarEvtJson = new JSONObject();
                    try {
                        RegistarEvtJson.put("env", environment);
                        RegistarEvtJson.put("type_events",typeEvent);
                        RegistarEvtJson.put("description", descrip);
                        Intent RegistrarEvento = new Intent(PreguntasActivity.this, ServicesHttp_POST.class);
                        RegistrarEvento.putExtra("url", URL_REGISTRAR_EVENTO);
                        RegistrarEvento.putExtra("datosJson", RegistarEvtJson.toString());
                        RegistrarEvento.putExtra("action", action);

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
                } else if((event.values[0]) < -0.5f) {
                    respuesta="No";
                    pregunta.setText("Tu respuesta fue" + preguntas[contadorPreguntas]);
                    contadorPreguntas++;
                }
                resultado.setText(respuesta);
            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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


