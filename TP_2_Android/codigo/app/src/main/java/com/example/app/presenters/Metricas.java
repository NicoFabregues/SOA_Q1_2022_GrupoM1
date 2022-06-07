package com.example.app.presenters;

import android.content.Intent;

import com.example.app.models.ServiceCheckTokenExpiration;
import com.example.app.views.ActivityPrincipal;
import com.example.app.views.MetricasActivity;
import com.example.app.views.TenisActivity;

public class Metricas extends Sensores{
    public Metricas(MetricasActivity view) {
        super(view, ActivityPrincipal.class);
    }
}
