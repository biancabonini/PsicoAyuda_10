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

public class ServicesHttp_PUT extends IntentService {

    private Integer HTTP_OK = 200;
    private Integer HTTP_CREATED = 201;
    private String NOT_OK = "ERROR";
    public String action = "";
    public String resGetAction;
    private String token;

    public ServicesHttp_PUT() {
        super("ServicesHttp_PUT");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    protected void onHandleIntent(Intent intent) {

        try {

            String url = intent.getExtras().getString("url");
            String tokenRefresh = intent.getExtras().getString("tokenPut_rfrsh");
            ejecutarPut(url, tokenRefresh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ejecutarPut(String url,String tokenRefresh) {

        String result = PUT(url,tokenRefresh);
        if(result==null){
            return;
        }
        if(result.equals(NOT_OK)){
            return;
        }

        Intent i = new Intent("com.example.psicoayuda.intent.action.TOKEN_REFRESH");
        i.putExtra("respuesta",result);
        sendBroadcast(i);

    }

    private String PUT(String url,String tokenRefresh) {
        {
            HttpURLConnection urlConnection = null;
            String res = "";

            try {
                URL mUrl = new URL(url);
                urlConnection = (HttpURLConnection) mUrl.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Authorization", "Bearer " + tokenRefresh);

                /*DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                wr.write(datosJson.toString().getBytes("UTF-8"));

                wr.flush();*/

                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                urlConnection.disconnect();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                    res = convertInputStreamToString(inputStream).toString();

                } else {
                    return NOT_OK;
                }

                //wr.close();
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
