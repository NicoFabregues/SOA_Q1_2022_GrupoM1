package com.example.app.models;

import android.app.IntentService;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public abstract class HTTPService extends IntentService {

    private static final String URI = "http://so-unlam.net.ar";

    protected String url, token, refreshToken;
    protected ConnectionManager connectionManager;
    protected JSONObject request;
    protected JSONObject response;
    protected Exception exception;
    protected boolean success;
    private DatabaseHandler db;

    public HTTPService(String class_name) {
        // Nombre del thread usado para debugging
        super(class_name);
        this.url = this.getUrl();
        this.exception = null;
        this.token = "";
        this.refreshToken = "";
        this.success = false;
        this.connectionManager = new ConnectionManager(this);
        this.response = null;
        this.request = null;
        this.db = new DatabaseHandler(this);
    }

    protected void updateOrCreateMetrica(String tipoMetrica) {
        // Metodo a ser heredado por las clases hijas para crear o actualizar una metrica
        // Obtengo la fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String fecha = sdf.format(c.getTime());
        // Busco una metrica con el tipo dado por la clase hija y la fecha actual
        Metrica metrica = db.getMetrica(tipoMetrica, fecha);
        if (metrica != null) {
            // Si existe la actualizo
            metrica.setValor(metrica.getValor() + 1);
            db.updateMetricaValor(metrica);
        } else {
            // Si no existe la creo
            db.agregarMetrica(new Metrica(tipoMetrica, 1, fecha));
        }
    }

    protected void setConnectionHeadersPOST(HttpURLConnection connection) throws ProtocolException {
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
    }

    protected void POST(JSONObject request) {
        try {
            HttpURLConnection connection = connectionManager.abrirConexion(this.url);
            this.setConnectionHeadersPOST(connection);

            //Este paquete JSON se escribe en el campo body del mensaje POST
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            // Loggeo el request enviado en el POST
            Log.i("POST REQUEST", request.toString());
            wr.writeBytes(request.toString());

            wr.flush();
            wr.close();

            //Se envia el request al Servidor
            connection.connect();

            //Se obtiene la respuesta que envio el Servidor ante el request
            parseResponse(connection);

            connection.disconnect();

        } catch (Exception e) {
            exception = e;
        }
    }

    private void parseResponse(HttpURLConnection connection) throws IOException, JSONException {
        int code = connection.getResponseCode();
        if(code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED){
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer responseBuffer = new StringBuffer();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                responseBuffer.append(inputLine);
            }
            in.close();

            response = new JSONObject(responseBuffer.toString());
            success = response.getBoolean("success");
            Log.i("RESPONSE", response.toString());
        }
    }

    /**
    * Método a ser utilizado por las clases que lo extiendan para agregar
    * el endpoint y devolver la url a la que se van a conectar
    */
    protected String getUrl() {
        return URI;
    }
}
