package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.app.R;
import com.example.app.models.DatabaseHandler;
import com.example.app.models.Metrica;
import com.example.app.presenters.Metricas;
import com.example.app.presenters.Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricasActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ListView lista;
    private ArrayList<String> listaMetricas;
    private ArrayAdapter<String> arrayAdapter;
    private Metricas presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metricas);
        listaMetricas = new ArrayList<>();
        lista = findViewById(R.id.listaMetricas);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMetricas);
        lista.setAdapter(arrayAdapter);
        presenter = new Metricas(this);
    }

    public void agregarMetrica(String entry){
            listaMetricas.add(entry);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}