package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        intentPrevio = getIntent();
        String refreshToken = intentPrevio.getStringExtra("refresh_token");

        presenter = new Principal(this, refreshToken);
        configureBroadcastReceiver();

        buttonVerPartidos = findViewById(R.id.buttonVerPartidos);
        buttonVerRankings = findViewById(R.id.buttonVerRankings);
        buttonVerMetricas = findViewById(R.id.buttonVerMetricas);


        buttonVerPartidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPrincipal.this, TorneosActivity.class));
            }
        });
        buttonVerRankings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPrincipal.this, RankingsActivity.class));
            }
        });
        buttonVerMetricas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPrincipal.this, MetricasActivity.class));
            }
        });
    }

    /**
    * Metodo que registra un broadcast receiver para comunicar el servicio que recibe los
     mensajes del servidor con el presenter de esta activity
     Se registra la accion LOGOUT_APP, para que cuando la activity de refrescar token
     la ejecute se llame automaticamente el OnReceive del presentador
    * */
    private void configureBroadcastReceiver() {

        filtro = new IntentFilter("com.example.intentservice.intent.action.STOP_CHECK_TOKEN");
        filtro.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(presenter, filtro);
    }
}