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

public class RefrescarToken implements SensorEventListener {

    private static final int UMBRAL_AGITACION = 1600, INTERVALO = 100, ESCALA = 10000;
    private final Sensor acelerometro;
    private final SensorManager accelerometerManager;

    private RefrescarTokenActivity view;
    private RequestTask model;
    private float ultX, ultY, ultZ;
    private long ultActualizacion;
    private final Semaphore semaforo = new Semaphore(1);

    public RefrescarToken(RefrescarTokenActivity view, String refreshToken) {
        this.view = view;
        this.model = new RequestTask(refreshToken, this);
        this.accelerometerManager = (SensorManager) view.getSystemService(SENSOR_SERVICE);
        this.acelerometro = accelerometerManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.accelerometerManager.registerListener(this, this.acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public ConnectionManager getConnectionManager() {
        return new ConnectionManager(this.view);
    }

    public void unregisterListener() {
        this.accelerometerManager.unregisterListener(this, acelerometro);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x, y, z;
        long tiempoActual = System.currentTimeMillis();
        if ((tiempoActual - ultActualizacion) > INTERVALO) {

            long diferenciaTiempo = (tiempoActual - ultActualizacion);
            ultActualizacion = tiempoActual;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            float velocidad = Math.abs(x + y + z - ultX - ultY - ultZ) / diferenciaTiempo * ESCALA;

            if (velocidad > UMBRAL_AGITACION) {
                this.view.salirRefresh();
            }
            ultX = x;
            ultY = y;
            ultZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
