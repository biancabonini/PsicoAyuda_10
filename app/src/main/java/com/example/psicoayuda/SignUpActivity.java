package com.example.psicoayuda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.psicoayuda.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    private static final String URL_SIGN_UP = "http://so-unlam.net.ar/api/api/register";
    private String txtEnvironment = "PROD";
    private Integer comision = 2900;
    private Integer grupo = 10;
    private TextView resultEditText;
    public IntentFilter filter;
    private Receptor receiver = new Receptor();
    public String resultado;
    public String token;
    private static final String action = "RESPUESTA_OPERACION";
    public String tokenRefresh;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText nameEditText = findViewById(R.id.user_name);
        final EditText lastnameEditText = findViewById(R.id.user_lastname);
        final EditText dniEditText = findViewById(R.id.user_dni);
        final Button signUpButton = findViewById(R.id.sign_up);

        configurarBroadcastReceiver();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pegarle a la API de registrar
                JSONObject JSONsignUp = new JSONObject();
                try {

                    JSONsignUp.put("env", txtEnvironment);
                    JSONsignUp.put("name", nameEditText.getText().toString());
                    JSONsignUp.put("lastname", lastnameEditText.getText().toString());
                    JSONsignUp.put("dni", Integer.parseInt(dniEditText.getText().toString()));
                    JSONsignUp.put("email", emailEditText.getText().toString());
                    JSONsignUp.put("password", passwordEditText.getText().toString());
                    JSONsignUp.put("commission", comision);
                    JSONsignUp.put("group", grupo);

                    Intent signUpIntent = new Intent(SignUpActivity.this, ServicesHttp_POST.class);

                    signUpIntent.putExtra("url", URL_SIGN_UP);
                    signUpIntent.putExtra("datosJson", JSONsignUp.toString());
                    signUpIntent.putExtra("action", action);

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo.isConnected()) {
                        Toast.makeText(getApplicationContext(),"Est?? conectado a internet",Toast.LENGTH_LONG).show();
                        startService(signUpIntent);
                    } else {
                        Toast.makeText(getApplicationContext(),"No hay conexi??n a internet",Toast.LENGTH_LONG).show();

                    }


                } catch (JSONException js) {
                    js.printStackTrace();
                }

            }
        });
    }


    private void configurarBroadcastReceiver(){
        filter = new IntentFilter("com.example.psicoayuda.intent.action.RESPUESTA_OPERACION");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver,filter);
    }

    private class Receptor extends BroadcastReceiver
    {

        public void onReceive(Context context, Intent intent){
            try
            {
                String tipo = intent.getStringExtra("tipo");
                resultEditText = (TextView) findViewById(R.id.txtResultado);
                String datosJSON = intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJSON);
                if(tipo.equals("POST")){
                    resultEditText.setText(datosJSON);
                    token = datosJson.getString("token");
                    resultado = datosJson.getString("success");
                    if (resultado == "true")
                    {
                        Toast.makeText(getApplicationContext(),"Se ha registrado correctamente",Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error, espere y reintente",Toast.LENGTH_LONG).show();
                    }

                }
                else{
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
}


