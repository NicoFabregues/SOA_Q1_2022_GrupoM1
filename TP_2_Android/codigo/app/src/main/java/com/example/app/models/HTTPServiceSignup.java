package com.example.app.models;

import android.content.Intent;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTPServiceSignup extends HTTPService{

    private static final String class_name = HTTPServiceSignup.class.getSimpleName();
    private static final String ENDPOINT = "/api/api/register";
    private static final String TIPO_METRICA = "Cant Registros";

    private DatabaseHandler db;

    public HTTPServiceSignup() {
        super(class_name);
    }

    protected void updateOrCreateMetrica() {
        super.updateOrCreateMetrica(TIPO_METRICA);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(connectionManager.hayConexion()) {
            try {
                if (intent.hasExtra("jsonObject")) {
                    request = new JSONObject(intent.getStringExtra("jsonObject"));
                }
                POST(request);
                if (exception != null) {
                    Intent i = new Intent("com.example.intentservice.intent.action.SIGNUP_RESPONSE");
                    i.putExtra("success", success);
                    i.putExtra("mensaje", "Error al enviar Request");
                    // Envío de valores al broadcast receiver del presenter de login
                    sendBroadcast(i);
                }
                else if (!success) {
                    Intent i = new Intent("com.example.intentservice.intent.action.SIGNUP_RESPONSE");
                    i.putExtra("success", success);
                    i.putExtra("mensaje", "Error de datos del Request");
                    // Envío de valores al broadcast receiver del presenter de login
                    sendBroadcast(i);
                }
                else {
                    token = response.getString("token");
                    updateOrCreateMetrica();
                    Intent i = new Intent("com.example.intentservice.intent.action.SIGNUP_RESPONSE");
                    i.putExtra("success", success);
                    i.putExtra("mensaje", "Usuario registrado con éxito");
                    i.putExtra("token", token);
                    // Envío de valores al broadcast receiver del presenter de login
                    sendBroadcast(i);
                }
                stopSelf();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent i = new Intent("com.example.intentservice.intent.action.SIGNUP_RESPONSE");
            i.putExtra("success", success);
            i.putExtra("mensaje", "No se encontró conexión a Internet");
            // Envío de valores al broadcast receiver del presenter de login
            sendBroadcast(i);
        }
    }

    protected String getUrl() {
        return super.getUrl() + ENDPOINT;
    }

}
