package com.example.psicoayuda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.psicoayuda.R;
import com.example.psicoayuda.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.objectweb.asm.Label;

public class PreguntasActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor giroscopio;
    private boolean rotacion=false;
    private String respuesta="";
    int contadorPreguntas;
    private static final String URL_REGISTRAR_EVENTO = "http://so-unlam.net.ar/api/api/event";
    private static final String environment = "PROD";
    private static final String typeEvent = "Lectura del sensor giroscopio";
    private String description;
    public IntentFilter filter;
    private Receptor receiver = new Receptor();
    private TextView resultEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preguntas);
        final TextView leyenda = findViewById(R.id.leyenda);
        final TextView pregunta = findViewById(R.id.pregunta);

        String preguntas[] = {"pregunta1","pregunta2","pregunta3"};

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        contadorPreguntas = 0;

        pregunta.setText("pregunta");

        configurarBroadcastReceiver();

        SensorEventListener sensorGiroscopioListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[2] > 0.5f) { // anticlockwise
                    rotacion=true;
                    respuesta="Si";
                    String descrip=Float.toString (sensorEvent.values[2]);
                    JSONObject RegistarEvtJson = new JSONObject();
                    try {
                        RegistarEvtJson.put("env", environment);
                        RegistarEvtJson.put("type_events",typeEvent);
                        RegistarEvtJson.put("description", descrip);
                        Intent RegistrarEvento = new Intent(PreguntasActivity.this, ServicesHttp_POST.class);
                        RegistrarEvento.putExtra("url", URL_REGISTRAR_EVENTO);
                        RegistrarEvento.putExtra("datosJson", RegistarEvtJson.toString());
                        startService(RegistrarEvento);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(sensorEvent.values[2] < -0.5f) { // clockwise
                    rotacion=true;
                    respuesta="No";
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };

        while(rotacion &&  (contadorPreguntas < preguntas.length) ){
                rotacion=false;
                pregunta.setText(preguntas[contadorPreguntas]);
                contadorPreguntas++;
        }



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
                    Toast.makeText(getApplicationContext(),"Se ha logueado correctamente",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Ha ocurrido un error, espere y reintente",Toast.LENGTH_LONG).show();
                }

            }
            catch (JSONException js){
                js.printStackTrace();
            }
        }
    }


    };


