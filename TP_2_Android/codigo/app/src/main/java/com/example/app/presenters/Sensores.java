package com.example.app.presenters;

import static android.content.Context.SENSOR_SERVICE;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.views.ActivityPrincipal;


public abstract class Sensores  extends BroadcastReceiver {

    private static final int UMBRAL_AGITACION = 40000, ESCALA = 5000, INTERVALO = 800;
    private final Sensor acelerometro;
    private final SensorManager accelerometerManager;
    private SensorEventListener listenerAcelerometro;
    private static double tiempo=0;


    private final Sensor luz;
    private final SensorManager luzManager;
    private SensorEventListener listenerLuz;
    private float luzActual;


    public Sensores(AppCompatActivity view, Class<?> sigiente) {

        listenerAcelerometro = new SensorEventListener() {
            @Override
            public void onSensorChanged (SensorEvent event){

                float x=event.values[0];
                float velocidad = x*x*ESCALA;


                if (velocidad > UMBRAL_AGITACION) {
                    if(System.currentTimeMillis()-tiempo>INTERVALO)
                    {
                        unregisterListener();
                        tiempo=System.currentTimeMillis();
                        view.startActivity(new Intent(view, sigiente));
                        view.recreate();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };


        this.accelerometerManager = (SensorManager) view.getSystemService(SENSOR_SERVICE);
        this.acelerometro = accelerometerManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.accelerometerManager.registerListener(listenerAcelerometro, this.acelerometro, SensorManager.SENSOR_DELAY_NORMAL);

        listenerLuz = new SensorEventListener() {
            @Override
            public void onSensorChanged (SensorEvent event){
                luzActual=event.values[0];
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        this.luzManager = (SensorManager) view.getSystemService(SENSOR_SERVICE);
        this.luz = luzManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.luzManager.registerListener(listenerLuz, this.luz, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public void unregisterListener() {
        this.accelerometerManager.unregisterListener(listenerAcelerometro);
        this.luzManager.unregisterListener(listenerLuz);
    }

}