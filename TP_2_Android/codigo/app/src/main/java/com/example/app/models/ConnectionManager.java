package com.example.app.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.app.presenters.ConsultasTenis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionManager {

    private static final int NETWORK_OK = 0;

    private Context contexto;

    public ConnectionManager (Context contexto){
        this.contexto = contexto;
    }

    public HttpURLConnection abrirConexion(String url) throws IOException {
        URL obj = new URL(url);
        return (HttpURLConnection) obj.openConnection();
    }

    public boolean hayConexion() {
        return internetConectado() || redConectado();
    }

    private boolean redConectado() {
        ConnectivityManager connectivityManager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean internetConectado() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == NETWORK_OK);
        } catch (Exception e) {
            return false;
        }
    }
}
