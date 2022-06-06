package com.example.app.presenters;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.app.models.ConnectionManager;
import com.example.app.models.RequestTask;
import com.example.app.views.RefrescarTokenActivity;

import java.util.concurrent.Semaphore;

public class RefrescarToken {



    private RefrescarTokenActivity view;
    private RequestTask model;
    private final Semaphore semaforo = new Semaphore(1);

    public RefrescarToken(RefrescarTokenActivity view, String refreshToken) {
        this.view = view;
        this.model = new RequestTask(refreshToken, this);
    }

    public ConnectionManager getConnectionManager() {
        return new ConnectionManager(this.view);
    }


    public void ejecutarTask() {
        try {
            if (semaforo.availablePermits() > 0) {
                this.semaforo.acquire();
                this.model.execute();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void actualizarActivity(Intent intent) {
        boolean success = intent.getBooleanExtra("success", false);
        String mensaje = intent.getStringExtra("mensaje");
        this.view.mostrarToastMake(mensaje);
        this.semaforo.release();
        if (success) {
            //Ejecuto método de llamado de siguiente activity
            this.view.iniciarActivityPrincipal(intent);
        } else {
            this.view.salirRefresh();
        }
    }
}
