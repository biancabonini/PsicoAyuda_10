package com.example.psicoayuda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.security.Principal;

import io.paperdb.Paper;

public class AppPrincipal extends AppCompatActivity implements SensorEventListener  {

    private String token;
    private Sensor acelerometro;
    private SensorManager sensorManager;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    boolean sacudido = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_principal);

        final EditText asuntoText = findViewById(R.id.EditTextAsunto);
        final EditText descripcionText = findViewById(R.id.EditTextDescripcion);
        final Button enviarButton = findViewById(R.id.enviar);
        final TextView asunto_txtv = findViewById(R.id.asunto_txt);
        final TextView desc_txtv = findViewById(R.id.descripcion_txt);
        final TextView token_refresh = findViewById(R.id.textView2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acelerometro , SensorManager.SENSOR_DELAY_NORMAL);

        token = getIntent().getExtras().getString("token");

        final TextView resText = findViewById(R.id.textView2);
        resText.setText(token);

        String asunto= asuntoText.getText().toString();
        String descripcion= descripcionText.getText().toString();

        Button btnSetup = (Button)findViewById(R.id.enviar);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppPrincipal.this, "Su consulta se ha publicado", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(AppPrincipal.this,SegundoActivity.class);
                //startActivity(intent);
            }
        });

        if(sacudido){
            Toast.makeText(AppPrincipal.this, "Su consulta se ha publicado", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(AppPrincipal.this,SegundoActivity.class);
            //startActivity(intent);
            publicarConsulta(asunto, descripcion);
        }


    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    sacudido = true;
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void publicarConsulta(String asunto, String descripcion){
        Intent intent = new Intent(AppPrincipal.this,SegundoActivity.class);
        startActivity(intent);
    }
}
