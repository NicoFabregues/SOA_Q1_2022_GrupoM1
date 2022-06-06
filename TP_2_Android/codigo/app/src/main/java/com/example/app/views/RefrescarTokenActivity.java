package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.presenters.RefrescarToken;


public class RefrescarTokenActivity extends AppCompatActivity {

    private Intent intentPrevio, intentSalir, intentActivityPrincipal;
    private Button botonRefrescar;
    private RefrescarToken presenter;
    private String refreshToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refrescar_token);

        botonRefrescar = findViewById(R.id.buttonRefrescar);
        intentSalir = new Intent(this, VerificacionUserLoginActivity.class);
        intentActivityPrincipal = new Intent(this, ActivityPrincipal.class);

        intentPrevio = getIntent();
        refreshToken = intentPrevio.getStringExtra("refresh_token");

        presenter = new RefrescarToken(this, refreshToken);

        botonRefrescar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread th = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        presenter.ejecutarTask();
                    }
                };
                th.start();
            }
        });
    }

    public void iniciarActivityPrincipal(Intent intent) {
        intentActivityPrincipal.putExtra("token", intent.getStringExtra("token"));
        intentActivityPrincipal.putExtra("refresh_token", intent.getStringExtra("refresh_token"));
        startActivity(intentActivityPrincipal);
    }

    private void sendBroadcastToStopCheckToken() {
        Intent iserv = new Intent("com.example.intentservice.intent.action.STOP_CHECK_TOKEN");
        iserv.putExtra("stop_service", true);
        //Se envian los valores al bradcast reciever del presenter de la activity principal
        sendBroadcast(iserv);
    }

    public void salirRefresh() {
        sendBroadcastToStopCheckToken();
        startActivity(intentSalir);
    }

    public void mostrarToastMake(String msg) {
        Handler mainHandler = new Handler(getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}