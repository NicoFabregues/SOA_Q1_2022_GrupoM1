package com.example.app.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.app.models.HTTPServiceLogin;
import com.example.app.models.User;
import com.example.app.views.VerificacionUserLoginActivity;

import org.json.JSONObject;

public class VerificacionLoginUsuario extends BroadcastReceiver {

    private User user;
    private VerificacionUserLoginActivity view;
    private Intent intentServiceLogin;

    public VerificacionLoginUsuario(VerificacionUserLoginActivity view) {
        this.user = new User();
        this.view = view;
        this.intentServiceLogin = new Intent(view, HTTPServiceLogin.class);
    }

    public void setEmail(String email) {
        this.user.setEmail(email);
    }

    public void setPass(String pass) {
        this.user.setPass(pass);
    }

    public void logIn(){
        JSONObject req = this.user.getJSONForLogIn();
        this.intentServiceLogin.putExtra("jsonObject", req.toString());
        this.intentServiceLogin.putExtra("user", user.getUser());
        this.view.startService(this.intentServiceLogin);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Método para recibir el resultado del servicio de login
        boolean success = intent.getBooleanExtra("success", false);
        String mensaje = intent.getStringExtra("mensaje");
        this.view.mostrarToastMake(mensaje);
        if (success) {
            //Ejecuto método de llamado de siguiente activity
            this.view.lanzarActivityPrincipal(intent.getStringExtra("token"));
        }
    }

    public void stopAll(){
        this.view.stopService(intentServiceLogin);
    }
}
