package com.example.app.views;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.app.R;

public class MainActivity extends AppCompatActivity {

    /*private BroadcastReceiver BateriaReceiver;

    private BroadcastReceiver getBateriaReceiver() {
        return new BroadcastReceiver(){

            private static final int ESCALA = 100;

            @Override
            public void onReceive(Context context, Intent intent) {
                int nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int escala = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float porcentaje = nivel * ESCALA / (float)escala;
                Toast.makeText(getApplicationContext(), "Porcentaje batería: " + round(porcentaje) + "%", Toast.LENGTH_LONG).show();
            }
        };
    }*/

    protected void iniciarVerificacionSMSActivity() {
        Intent intent = new Intent(this, EnvioSMSActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*this.BateriaReceiver = this.getBateriaReceiver();*/
        setContentView(R.layout.activity_main);
        // Registro un escuchador con el Context de la MainActivity para calcular el porcentaje
        // de batería
        // Utilizo un IntentFilter para filtrar aquellos Intent de las difusiones que sean de tipo
        // ACTION_BATTERY_CHANGED
        /*
        this.registerReceiver(this.BateriaReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        */
        // Inicio la siguiente actividad
        this.iniciarVerificacionSMSActivity();
    }
}