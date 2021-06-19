package com.example.psicoayuda;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServicesHttp_POST extends IntentService {

    private Integer HTTP_OK = 200;
    private Integer HTTP_CREATED = 201;
    private String NOT_OK = "ERROR";
    public String action = "";
    public String resGetAction;
    private String token;

    public ServicesHttp_POST() {
        super("ServicesHttp_POST");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
    }*/

    protected void onHandleIntent(Intent intent) {

        try {
            resGetAction = intent.getExtras().getString("action");
            if(resGetAction.equals("RESPUESTA_OPERACION")){
                action = "com.example.psicoayuda.intent.action.RESPUESTA_OPERACION";
            }
            else if(resGetAction.equals("REGISTRAR_EVENTO")){
                action = "com.example.psicoayuda.intent.action.REGISTRAR_EVENTO";
                token = intent.getExtras().getString("tokenAP");
            }
            String url = intent.getExtras().getString("url");
            JSONObject datosJson = new JSONObject(intent.getExtras().getString(("datosJson")));
            ejecutarPost(url, datosJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ejecutarPost(String url, JSONObject datosJson) {

        String result = POST(url, datosJson);
        if(result==null){
            return;
        }
        if(result.equals(NOT_OK)){
            return;
        }

        Intent i = new Intent(action);
        i.putExtra("datosJson",result);
        sendBroadcast(i);

    }

    private String POST(String url, JSONObject datosJson) {
        {
            HttpURLConnection urlConnection = null;
            String res = "";

            try {
                URL mUrl = new URL(url);
                urlConnection = (HttpURLConnection) mUrl.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                if(resGetAction.equals("REGISTRAR_EVENTO")){
                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                wr.write(datosJson.toString().getBytes("UTF-8"));

                wr.flush();

                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                urlConnection.disconnect();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                    res = convertInputStreamToString(inputStream).toString();

                } else {
                    return NOT_OK;
                }

                wr.close();
                urlConnection.disconnect();
                return res;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private StringBuilder convertInputStreamToString(InputStreamReader inputStream) throws IOException{

        BufferedReader br = new BufferedReader(inputStream);
        StringBuilder result = new StringBuilder();
        String line;
        while((line= br.readLine())!= null){
            result.append(line + "\n");
        }
        br.close();
        return result;
    }
}
