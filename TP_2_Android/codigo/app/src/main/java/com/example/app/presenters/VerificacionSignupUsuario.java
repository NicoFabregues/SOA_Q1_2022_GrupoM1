package com.example.app.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.app.models.HTTPServiceSignup;
import com.example.app.models.User;
import com.example.app.views.VerificacionUserSignupActivity;

import org.json.JSONObject;

public class VerificacionSignupUsuario extends BroadcastReceiver {

    private User user;
    private VerificacionUserSignupActivity view;
    private Intent intentServiceSignup;

    public VerificacionSignupUsuario(VerificacionUserSignupActivity view) {
        this.user = new User();
        this.view = view;
        this.intentServiceSignup = new Intent(view, HTTPServiceSignup.class);
    }

    public void setEmail(String email) {
        this.user.setEmail(email);
    }

    public void setPass(String pass) {
        this.user.setPass(pass);
    }

    public void setNombre(String nombre) {
        this.user.setNombre(nombre);
    }

    public void setApellido(String apellido) {
        this.user.setApellido(apellido);
    }

    public void setDni(String dni) {
        this.user.setDni(dni);
    }

    public void setComision(String comision) {
        this.user.setComision(comision);
    }

    public void setGrupo(String grupo) {
        this.user.setGrupo(grupo);
    }

    public void signUp(){
        // Inicio el servicio de signup por http
        JSONObject req = this.user.getJSONForSignup();
        this.intentServiceSignup.putExtra("jsonObject", req.toString());
        this.view.startService(this.intentServiceSignup);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Método para recibir el resultado del servicio de signup
        boolean success = intent.getBooleanExtra("success", false);
        String mensaje = intent.getStringExtra("mensaje");
        this.view.mostrarToastMake(mensaje);
        // En caso de poder registrarme correctamente vuelvo a la actividad del login
        if (success) {
            this.view.finish();
        }
    }

    public void stopAll(){
        this.view.stopService(intentServiceSignup);
    }
}
