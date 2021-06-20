package com.example.psicoayuda.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psicoayuda.R;
import com.example.psicoayuda.ServicesHttp_POST;
import com.example.psicoayuda.ServicesHttp_PUT;
import com.example.psicoayuda.SignUpActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String URL_LOGIN = "http://so-unlam.net.ar/api/api/login";

    public IntentFilter filter;

    private Receptor receiver = new Receptor();

    private LoginViewModel loginViewModel;
    public String email;
    private TextView resultEditText;
    private static final String action = "RESPUESTA_OPERACION";
    private String token_refresh;
    private  String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button signUpButton = findViewById(R.id.sign_up);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        configurarBroadcastReceiver();
        configurarBroadcastReceiverAlarm();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());*/
                JSONObject JSONsignIn = new JSONObject();
                try{
                    JSONsignIn.put("email", emailEditText.getText().toString());
                    JSONsignIn.put("password", passwordEditText.getText().toString());

                    email = emailEditText.getText().toString();
                    Intent signInIntent = new Intent(LoginActivity.this, ServicesHttp_POST.class);

                    signInIntent.putExtra("url", URL_LOGIN);
                    signInIntent.putExtra("datosJson", JSONsignIn.toString());
                    signInIntent.putExtra("action", action);

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo.isConnected()) {
                        //resultEditText.setText("Está conectado a internet");
                        Toast.makeText(getApplicationContext(),"Está conectado a internet",Toast.LENGTH_LONG).show();
                        startService(signInIntent);
                    } else {
                        //resultEditText.setText("No hay conexión a internet");
                        Toast.makeText(getApplicationContext(),"No está conectado a internet",Toast.LENGTH_LONG).show();
                    }


                }
                catch (JSONException js){
                    js.printStackTrace();
                }

            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void configurarBroadcastReceiver(){
        filter = new IntentFilter("com.example.psicoayuda.intent.action.RESPUESTA_OPERACION");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver,filter);
    }

    private void configurarBroadcastReceiverAlarm(){
        filter = new IntentFilter("com.example.psicoayuda.intent.action.TOKEN_REFRESH");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver,filter);
    }

    private class Receptor extends BroadcastReceiver
    {

        public void onReceive(Context context, Intent intent){
            try
            {
                String tipo = intent.getStringExtra("tipo");
                resultEditText = (TextView) findViewById(R.id.txtResultado2);
                String datosJSON = intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJSON);
                String resultado = datosJson.getString("success");
                resultEditText.setText(datosJSON);
                Toast.makeText(getApplicationContext(),"Se recibió la respuesta del server",Toast.LENGTH_LONG).show();
                if(tipo.equals("POST")) {
                    token = datosJson.getString("token");
                    token_refresh = datosJson.getString("token_refresh");

                    if (resultado == "true") {
                        Toast.makeText(getApplicationContext(), "Se ha logueado correctamente", Toast.LENGTH_LONG).show();
                        Intent goToAppPrincipal = new Intent(LoginActivity.this, com.example.psicoayuda.AppPrincipal.class);
                        goToAppPrincipal.putExtra("token1", token);
                        goToAppPrincipal.putExtra("email", email);
                        goToAppPrincipal.putExtra("Token_rfrs", token_refresh);
                        startActivity(goToAppPrincipal);
                    } else {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error, espere y reintente", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    if (resultado == "true")
                    {
                        Toast.makeText(getApplicationContext(),"Se ha refrescado el token correctamente",Toast.LENGTH_LONG).show();
                        token = datosJson.getString("token");
                        token_refresh = datosJson.getString("token");
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

}
