package com.example.app.models;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class RequestTask extends AsyncTask<Void, Void, Void> {

    private static final String URI = "http://so-unlam.net.ar";
    private static final String ENDPOINT = "/api/api/refresh";

    protected String url, token;
    protected ConnectionManager connectionManager;
    protected JSONObject response;
    protected Exception exception;


    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    protected void setConnectionHeadersPUT(HttpURLConnection connection) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Authorization",  "Bearer ");
    }

    protected void PUT() {
        try {
            HttpURLConnection connection = connectionManager.abrirConexion(this.url);
            this.setConnectionHeadersPUT(connection);

            // Se envia el request
            connection.connect();

            // Se parsea el response
            parseResponse(connection);

            connection.disconnect();

        } catch (Exception e) {
            exception = e;
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
        }
    }

    private String getUrl() {
        return URI + ENDPOINT;
    }
}
