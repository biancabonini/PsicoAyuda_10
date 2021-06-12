package com.example.psicoayuda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button button_send;
    private Button button_verify;
    private EditText phone;
    private EditText code_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //Metodo que actua como Listener de los eventos que ocurren en los componentes graficos de la activty
    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            Intent verif;

            //Se determina que componente genero un evento
            switch (v.getId())
            {
                //Si se ocurrio un evento en el boton OK
                case R.id.enviar:


                   //verif=new Intent(MainActivity.this,ActivityLogin.class);

                    //Se le agrega al intent los parametros que se le quieren pasar a la activyt principal
                    //cuando se lanzado
                    //intent.putExtra("textoOrigen",activity_login.getText().toString());

                    //se inicia la activity principal
                    //startActivity(verif);
                    break;

                case R.id.verificar:

                    verif=new Intent(MainActivity.this,ActivityLogin.class);
                    startActivity(verif);
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();
            }


        }
    };

    public void verificar(View view)
    {
        Intent verif = new Intent(this,SegundoActivity.class);
        startActivity(verif);
    }

}
