package com.example.psicoayuda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class AppPrincipal extends AppCompatActivity {

    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_principal);

        final EditText asuntoText = findViewById(R.id.EditTextAsunto);
        final EditText descripcionText = findViewById(R.id.EditTextDescripcion);
        final Button enviarButton = findViewById(R.id.enviar);

        token = getIntent().getExtras().getString("token");

        final TextView resText = findViewById(R.id.textView2);
        resText.setText(token);

    }
}
