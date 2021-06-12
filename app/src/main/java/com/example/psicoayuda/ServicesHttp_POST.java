package com.example.psicoayuda;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONObject;

public class ServicesHttp_POST extends IntentService {

    public ServicesHttp_POST(){
        super ("ServicesHttp_POST");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.i("LOGUEO_SERVICE","Service onCreate()");
    }

    protected void onHandleIntent(Intent intent){
        try {
            String uri = intent.getExtras().getString("uri");
            JSONObject datosJson = new JSONObject(intent.getExtras().getString(("datosJson")));
           // ejecutarPost(uri,datosJson);
        }catch (Exception e){
            //log
        }
    }
}
