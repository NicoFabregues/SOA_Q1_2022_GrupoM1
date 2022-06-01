package com.example.app.models;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.app.presenters.RefrescarToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.concurrent.Semaphore;

public class RequestTask extends AsyncTask<Void, Void, Void> {

    private static final String URI = "http://so-unlam.net.ar";
    private static final String ENDPOINT = "/api/api/refresh";

    protected String url, token, refreshToken;
    protected ConnectionManager connectionManager;
    protected JSONObject response;
    protected Exception exception;
    protected RefrescarToken caller;

    public RequestTask(String refreshToken, RefrescarToken refrescarToken) {
        // Nombre del thread usado para debugging
        super();
        this.url = this.getUrl();
        this.exception = null;
        this.token = "";
        this.refreshToken = refreshToken;
        this.connectionManager = refrescarToken.getConnectionManager();
        this.caller = refrescarToken;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        this.enviarPeticion();
        return null;
    }

    protected void setConnectionHeadersPUT(HttpURLConnection connection) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Authorization",  "Bearer " + refreshToken);
    }

    protected void PUT() {
        try {
            HttpURLConnection connection = connectionManager.abrirConexion(this.url);
            this.setConnectionHeadersPUT(connection);

            //Se envia el request al Servidor
            connection.connect();

            //Se obtiene la respuesta que envio el Servidor ante el request
            parseResponse(connection);

            connection.disconnect();

        } catch (Exception e) {
            exception = e;
        }
    }

    private void enviarPeticion() {
        if(connectionManager.hayConexion()){
            PUT();
            if (exception != null) {
                Intent i = new Intent();
                i.putExtra("success", false);
                i.putExtra("mensaje", "Error en envio de request");
                caller.actualizarActivity(i);
            }
            else if(!token.isEmpty()) {
                Intent i = new Intent();
                i.putExtra("success", true);
                i.putExtra("mensaje", "Token actualizado");
                i.putExtra("token", token);
                i.putExtra("refresh_token", refreshToken);
                caller.actualizarActivity(i);
            }
            else {
                Intent i = new Intent();
                i.putExtra("success", false);
                i.putExtra("mensaje", "Ocurrio un error con el refresh del token");
                caller.actualizarActivity(i);
            }
        } else {
            Intent i = new Intent();
            i.putExtra("success", false);
            i.putExtra("mensaje", "No hay  conexión a Internet");
            //Se envian los valores al bradcast reciever del presenter de login
            caller.actualizarActivity(i);
        }
    }

    private void parseResponse(HttpURLConnection connection) throws IOException, JSONException {
        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer responseBuffer = new StringBuffer();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                responseBuffer.append(inputLine);
            }
            in.close();

            response = new JSONObject(responseBuffer.toString());
            Log.i("RESPONSE", response.toString());
            token = response.getString("token");
            refreshToken = response.getString("token_refresh");
        }
    }

    private String getUrl() {
        return URI + ENDPOINT;
    }
}
