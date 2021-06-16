package com.example.psicoayuda;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.psicoayuda.R;

import org.objectweb.asm.Label;

public class PreguntasActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor giroscopio;
    private boolean rotacion=false;
    private String respuesta="";
    int contadorPreguntas;

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

        SensorEventListener sensorGiroscopioListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[2] > 0.5f) { // anticlockwise
                    rotacion=true;
                    respuesta="Si";
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



    };


