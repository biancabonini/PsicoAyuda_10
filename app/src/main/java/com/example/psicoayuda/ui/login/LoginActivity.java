package com.example.psicoayuda.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.example.psicoayuda.MainActivity;
import com.example.psicoayuda.R;
import com.example.psicoayuda.SegundoActivity;
import com.example.psicoayuda.ServicesHttp_POST;
import com.example.psicoayuda.SignUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String URL_LOGIN = "http://so-unlam.net.ar/api/api/login";

    public IntentFilter filter;

    private Receptor receiver = new Receptor();

    private LoginViewModel loginViewModel;


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

        /*loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        nameEditText.addTextChangedListener(afterTextChangedListener);
        lastnameEditText.addTextChangedListener(afterTextChangedListener);
        dniEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
        */

        configurarBroadcastReceiver();

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

                    Intent signInIntent = new Intent(LoginActivity.this, ServicesHttp_POST.class);

                    signInIntent.putExtra("url", URL_LOGIN);
                    signInIntent.putExtra("datosJson", JSONsignIn.toString());

                    startService(signInIntent);

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

    /*private void updateUiWithUser(LoggedInUserView model) {
        String welcome =  model.getDisplayName();
         TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

     */
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
                String datosJSON = intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJSON);

                //resultEditText.setText(datosJSON);
                Toast.makeText(getApplicationContext(),"Se recibió la respuesta del server",Toast.LENGTH_LONG).show();
                String token = datosJson.getString("token");
                String resultado = datosJson.getString("success");
                String token_refresh = datosJson.getString("token_refresh");
                //Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),resultado,Toast.LENGTH_LONG).show();

                /*if (resultado == "true")
                {
                    Toast.makeText(getApplicationContext(),"Se ha registrado correctamente",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Ha ocurrido un error, espere y reintente",Toast.LENGTH_LONG).show();
                }
                if (resultado == "true") {*/
                Intent AppPrincipal = new Intent(LoginActivity.this, LoginActivity.class);
                AppPrincipal.putExtra("token", token);
                startActivity(AppPrincipal);
                //}

            }
            catch (JSONException js){
                js.printStackTrace();
            }
        }
    }
}
