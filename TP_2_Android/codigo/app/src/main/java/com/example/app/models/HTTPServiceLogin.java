package com.example.app.models;

import android.content.Intent;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTPServiceLogin extends HTTPService{

    private static final String class_name = HTTPServiceLogin.class.getSimpleName();
    private static final String ENDPOINT = "/api/api/login";
    private static final String TYPE_EVENTS = "Login usuario";
    private static final String EVENT_DESCRIPTION = "Registro login de usuario en Servidor";
    private Intent intentServiceRegistrarEvento;

    public HTTPServiceLogin() {
        super(class_name);
    }

    private void startHTTPServiceRegistrarEvento() {
        Evento evento = new Evento(TYPE_EVENTS, EVENT_DESCRIPTION);
        JSONObject req = evento.getJSONForRegistrarEvento();
        this.intentServiceRegistrarEvento = new Intent(this, HTTPServiceRegistrarEvento.class);
        this.intentServiceRegistrarEvento.putExtra("token", token);
        this.intentServiceRegistrarEvento.putExtra("jsonObject", req.toString());
        startService(this.intentServiceRegistrarEvento);
    }

    protected void updateOrCreateMetrica(String user) {
        super.updateOrCreateMetrica(user);
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
                    Intent i = new Intent("com.example.intentservice.intent.action.LOGIN_RESPONSE");
                    i.putExtra("success", success);
                    i.putExtra("mensaje", "Error al enviar Request");
                    // Envío de valores al broadcast receiver del presenter de login
                    sendBroadcast(i);
                }
                else if (!success) {
                    Intent i = new Intent("com.example.intentservice.intent.action.LOGIN_RESPONSE");
                    i.putExtra("success", success);
                    i.putExtra("mensaje", "Usuario o contraseña incorrectos");
                    // Envío de valores al broadcast receiver del presenter de login
                    sendBroadcast(i);
                }
                else {
                    token = response.getString("token");
                    updateOrCreateMetrica(intent.getStringExtra("user"));
                    startHTTPServiceRegistrarEvento();
                    Intent i = new Intent("com.example.intentservice.intent.action.LOGIN_RESPONSE");
                    i.putExtra("success", success);
                    i.putExtra("mensaje", "Login exitoso");
                    i.putExtra("token", token);
                    // Envío de valores al broadcast receiver del presenter de login
                    sendBroadcast(i);
                }
                stopSelf();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent i = new Intent("com.example.intentservice.intent.action.LOGIN_RESPONSE");
            i.putExtra("success", success);
            i.putExtra("mensaje", "No se encontró conexión a Internet");
            // Envío de valores al broadcast receiver del presenter de login
            sendBroadcast(i);
        }
    }

    protected String getUrl() {
        return super.getUrl() + ENDPOINT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(intentServiceRegistrarEvento != null)
            stopService(intentServiceRegistrarEvento);
    }
}
