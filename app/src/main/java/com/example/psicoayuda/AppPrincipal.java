package com.example.psicoayuda;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
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
import android.app.AlarmManager;


import androidx.appcompat.app.AppCompatActivity;

import com.example.psicoayuda.ui.login.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;



public class AppPrincipal extends AppCompatActivity implements SensorEventListener  {

    private String token;
    private Sensor acelerometro;
    private SensorManager sensorManager;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    boolean sacudido = false;
    //private db = FirebaseFirestore.getinstance();
    public String email;
    public String tokenRefresh;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    String valorX;
    String valorY;
    String valorZ;

    private static final String URL_REFRESH_TOKEN = "http://so-unlam.net.ar/api/api/refresh";


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
        final TextView batteryLevel = findViewById(R.id.batteryLevel);
        final TextView lecturaX = findViewById(R.id.x);
        final TextView lecturaY= findViewById(R.id.y);
        final TextView lecturaZ = findViewById(R.id.z);

        iniciarServicioRecepctorAlarma();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //Se muestran los valores del último cambio del sensor
        valorX = sharedpreferences.getString("X","No hay valor");
        lecturaX.setText(valorX);
        valorY = sharedpreferences.getString("Y","No hay valor");
        lecturaY.setText(valorY);
        valorZ = sharedpreferences.getString("Z","No hay valor");
        lecturaZ.setText(valorZ);

        BroadcastReceiver bateriaReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
                int level = -1;
                if (currentLevel >=0 && scale > 0){
                    level= (currentLevel * 100)/scale;
                }
                batteryLevel.setText("Batería restante : "+level+"%");

            }
        };
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(bateriaReceiver,batteryFilter);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acelerometro , SensorManager.SENSOR_DELAY_NORMAL);

        Intent receiveLoginIntent = getIntent();
        token = receiveLoginIntent.getExtras().getString("token1");
        email = receiveLoginIntent.getExtras().getString("email");
        tokenRefresh = receiveLoginIntent.getExtras().getString("Token_rfrs");

        final TextView resText = findViewById(R.id.textView2);
        resText.setText(email);

        final String asunto= asuntoText.getText().toString();
        String descripcion= descripcionText.getText().toString();

        Button btnSetup = (Button)findViewById(R.id.enviar);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppPrincipal.this, "Su consulta se ha publicado", Toast.LENGTH_SHORT).show();
                //Insertar en Firebase la consulta publicada.
          /*      db.collection ("users").document(asunto)(
                        hashMapOf(
                        "asunto" to asuntoText.text.toString(),
                        "decripcion" to descripcionText.text.toString())


            )*/
            }
        });

       /* if(sacudido){
            Toast.makeText(AppPrincipal.this, "LLEGUEEEEE", Toast.LENGTH_SHORT).show();
            //Toast.makeText(AppPrincipal.this, "Su consulta se ha publicado", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(AppPrincipal.this,SegundoActivity.class);
            //startActivity(intent);
            publicarConsulta(asunto, descripcion);
        }*/


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
                    valorX = Float.toString(x);
                    valorY = Float.toString(y);
                    valorZ = Float.toString(z);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("X", valorX);
                    editor.putString("Y", valorY);
                    editor.putString("Z", valorZ);
                    editor.commit();
                    Toast.makeText(AppPrincipal.this, "Su consulta se ha publicado", Toast.LENGTH_SHORT).show();
                    Intent intentPreguntas = new Intent(AppPrincipal.this,PreguntasActivity.class);
                    intentPreguntas.putExtra("tokenPreg",token);
                    intentPreguntas.putExtra("token_rfrsPreg",tokenRefresh);
                    intentPreguntas.putExtra("mail",email);
                    startActivity(intentPreguntas);
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
    public void   iniciarServicioRecepctorAlarma()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.MyAlarmReceiver.class);
        intent.putExtra("uri",URL_REFRESH_TOKEN);
        intent.putExtra("refresh",tokenRefresh);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,  firstMillis, AlarmManager.INTERVAL_HALF_HOUR, pIntent);
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
}
