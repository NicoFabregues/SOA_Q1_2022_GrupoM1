package com.example.app.presenters;


import com.example.app.models.DatabaseHandler;
import com.example.app.models.Metrica;
import com.example.app.views.ActivityPrincipal;
import com.example.app.views.MetricasActivity;

import java.util.List;

public class Metricas extends Sensores{

    private DatabaseHandler db;

    public Metricas(MetricasActivity view) {
        super(view, ActivityPrincipal.class);
        db = new DatabaseHandler(view);
        llenarLista(view);
    }

    private synchronized void llenarLista(MetricasActivity view) {
        List<Metrica> keys = db.getAllMetricas();
        for (Metrica entry : keys) {
            view.agregarMetrica(entry.toString());
        }
    }
}
