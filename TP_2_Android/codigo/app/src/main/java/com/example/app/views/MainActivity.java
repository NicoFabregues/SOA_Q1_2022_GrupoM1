package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.app.R;

public class MainActivity extends AppCompatActivity {

    protected void iniciarVerificacionSMSActivity() {
        Intent intent = new Intent(this, EnvioSMSActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.iniciarVerificacionSMSActivity();
    }
}