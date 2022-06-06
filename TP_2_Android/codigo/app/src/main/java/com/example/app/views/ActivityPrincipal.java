package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.app.R;
import com.example.app.presenters.Principal;

public class ActivityPrincipal extends AppCompatActivity {

    private Intent intentPrevio;
    private Principal presenter;
    private IntentFilter filtro;
    private Button buttonVerRankings, buttonVerPartidos, buttonVerMetricas;
   // private SensorManager luzManager;
   // private Sensor sensorLuz;
   // private float medicionLuz;
   // private SensorEventListener listenerLuz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        intentPrevio = getIntent();
        String refreshToken = intentPrevio.getStringExtra("refresh_token");

        presenter = new Principal(this, refreshToken);
        configurarBroadcastReciever();

        buttonVerPartidos = findViewById(R.id.buttonVerPartidos);
        buttonVerRankings = findViewById(R.id.buttonVerRankings);
        buttonVerMetricas = findViewById(R.id.buttonVerMetricas);
/*
        luzManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorLuz = luzManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        listenerLuz = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                medicionLuz = event.values[0];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        luzManager.registerListener(listenerLuz, sensorLuz, SensorManager.SENSOR_DELAY_GAME);
*/
        buttonVerRankings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPrincipal.this, TenisActivity.class));
            }
        });
        buttonVerMetricas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPrincipal.this, MetricasActivity.class));
            }
        });
    }

    private void configurarBroadcastReciever() {
        //Metodo que registra un broadcast receiver para comunicar el servicio que recibe los
        //mensajes del servidor con el presenter de esta activity
        //Se registra la  accion LOGOUT_APP, para que cuando la activity de refrescar token
        //la ejecute se invoque automaticamente el OnRecive del presenter
        filtro = new IntentFilter("com.example.intentservice.intent.action.STOP_CHECK_TOKEN");
        filtro.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(presenter, filtro);
    }
}