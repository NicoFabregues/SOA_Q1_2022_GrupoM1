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

    // URL para conexión con API de la catedra.
    private static final String URI = "http://so-unlam.net.ar";

    protected String url, token;
    protected ConnectionManager connectionManager;
    protected JSONObject request;
    protected JSONObject response;
    protected Exception exception;
    protected boolean success;
    private DatabaseHandler db;

    public HTTPService(String class_name) {

        super(class_name);
        this.url = this.getUrl();
        this.exception = null;
        this.token = "";
        this.success = false;
        this.connectionManager = new ConnectionManager(this);
        this.response = null;
        this.request = null;
        this.db = new DatabaseHandler(this);

    }

    /**
    Método que será heredado por clases hijas para crear o actualizar una metrica
    */
    protected void updateOrCreateMetrica(String user) {

        // Obtengo la fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar c = Calendar.getInstance();
        String fecha = sdf.format(c.getTime());

        // Obtengo la metrica
        Metrica metrica = db.getMetrica(user);

        // Si existe, la actualizo, sino, la creo.
        if (metrica != null) {
            metrica.setFecha(fecha);
            db.updateMetricaValor(metrica);
        } else {
            db.agregarMetrica(new Metrica(user, fecha));
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

            // Escribo el JSON en el body del POST
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            // Loggeo el request enviado
            Log.i("POST REQUEST", request.toString());
            wr.writeBytes(request.toString());

            wr.flush();
            wr.close();

            // Envío request al server
            connection.connect();

            // Obtengo el responder del server y leo
            parseResponse(connection);

            connection.disconnect();

        } catch (Exception e) {
            exception = e;
        }
    }

    /**
    * Método para leer la respuesta que devolvió el servidor.
     * */
    private void parseResponse(HttpURLConnection connection) throws IOException, JSONException {

        int code = connection.getResponseCode();

        if(code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
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
    * Método para extender y agregar el endpoint y devolver la url completa.
    */
    protected String getUrl() {
        return URI;
    }
}
