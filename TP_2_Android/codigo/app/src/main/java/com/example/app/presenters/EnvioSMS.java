package com.example.app.presenters;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.app.models.SMSManager;
import com.example.app.views.EnvioSMSActivity;

public class EnvioSMS {

    private static final int REQUEST_SEND_SMS = 1;

    private SMSManager model;
    private EnvioSMSActivity view;

    public EnvioSMS(EnvioSMSActivity view) {
        this.model = new SMSManager();
        this.view = view;
    }

    public boolean solicitarPermisoSMS() {
        if (ContextCompat.checkSelfPermission(this.view, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.view, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
        }
        return ContextCompat.checkSelfPermission(this.view, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public void enviarSMS(String numCelular, boolean permisoConcedido) {
        this.model.enviarSMS(numCelular, permisoConcedido);
    }

    public String getCodEnviado() {
        return this.model.getCodSMS();
    }

}
