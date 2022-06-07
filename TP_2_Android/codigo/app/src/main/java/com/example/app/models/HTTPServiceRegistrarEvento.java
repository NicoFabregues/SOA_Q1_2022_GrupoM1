package com.example.app.models;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class HTTPServiceRegistrarEvento extends HTTPService {

    private static final String class_name = HTTPServiceRegistrarEvento.class.getSimpleName();
    private static final String ENDPOINT = "/api/api/event";
    private String token;

    public HTTPServiceRegistrarEvento() {
        super(class_name);
    }

    protected void setConnectionHeadersPOST(HttpURLConnection connection) throws ProtocolException {
        super.setConnectionHeadersPOST(connection);
        connection.setRequestProperty("Authorization", token);
        connection.setConnectTimeout(5000);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        token = "Bearer " + intent.getStringExtra("token");
        if(connectionManager.hayConexion()) {
            try {
                if (intent.hasExtra("jsonObject")) {
                    request = new JSONObject(intent.getStringExtra("jsonObject"));
                }
                POST(request);
                if (exception != null) {
                    Log.i("REGISTRO DE EVENTO", "Error en envio de request");
                }
                else if (success != true) {
                    Log.i("REGISTRO DE EVENTO", "Falló registro de evento");
                }
                else {
                    Log.i("REGISTRO DE EVENTO", "Evento registrado con éxito");
                }
                stopSelf();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("REGISTRO DE EVENTO", "No se encontró conexión a Internet");
        }
    }

    protected String getUrl() {
        return super.getUrl() + ENDPOINT;
    }
}