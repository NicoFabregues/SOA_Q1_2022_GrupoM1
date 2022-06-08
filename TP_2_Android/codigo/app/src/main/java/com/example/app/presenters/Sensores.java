package com.example.app.presenters;

import static android.content.Context.SENSOR_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public abstract class Sensores extends BroadcastReceiver {

    private static final int UMBRAL_AGITACION = 35000, ESCALA = 5000, INTERVALO = 1500;
    private final Sensor acelerometro;
    private final SensorManager accelerometerManager;
    private SensorEventListener listenerAcelerometro;
    private static double actiempo =0;


    private final Sensor luz;
    private final SensorManager luzManager;
    private SensorEventListener listenerLuz;
    private float luzActual;
    private int brillo;
    private static final int DIVLUZ = 400, INTERVALOLUZ = 60000;
    private static final double  MULTLUZ = 2.54;
    private static double luztiempo = 0;

    public Sensores(AppCompatActivity view, Class<?> siguiente) {

        listenerAcelerometro = new SensorEventListener() {
            @Override
            public void onSensorChanged (SensorEvent event){

                float x=event.values[0];
                float velocidad = x * x * ESCALA;

                if (velocidad > UMBRAL_AGITACION && x < 0) {
                    if(System.currentTimeMillis()- actiempo >INTERVALO)
                    {
                        unregisterListener();
                        actiempo =System.currentTimeMillis();
                        view.startActivity(new Intent(view, siguiente));
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(view.getApplicationContext())) {
                        if(System.currentTimeMillis()- luztiempo >INTERVALOLUZ) {
                            luztiempo = System.currentTimeMillis();
                            Toast.makeText(view, "Require Permission to Handle Screen Brightness", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + view.getPackageName()));
                            view.startActivityForResult(intent, 200);
                        }
                    }
                    else {
                        luztiempo = System.currentTimeMillis();
                        luzActual = event.values[0];
                        brillo = (int) ((luzActual * MULTLUZ) / DIVLUZ);//Obtengo un valor entre 0-254
                        brillo = 255-brillo;//Obtengo el inverso
                        Settings.System.putInt(view.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, brillo);//Seteo el brillo
                    }
                }
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