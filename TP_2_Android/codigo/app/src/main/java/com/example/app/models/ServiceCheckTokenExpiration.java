package com.example.app.models;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.example.app.views.RefrescarTokenActivity;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServiceCheckTokenExpiration extends IntentService {

    private static final String class_name = ServiceCheckTokenExpiration.class.getSimpleName();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture task;
    private Intent intentRefresh;
    private String refreshToken;
    private static final int INITIAL_DELAY = 15, PERIOD = 15;

    public ServiceCheckTokenExpiration() {
        super(class_name);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        intentRefresh = new Intent(this, RefrescarTokenActivity.class);
        refreshToken = intent.getStringExtra("refresh_token");
        intentRefresh.putExtra("refresh_token", refreshToken);
        intentRefresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Cancelo la tarea cuando se ejecuta este método para que no se siga ejecutando
                // y por ejemplo se abra la activity de refresh token cuando se volvió al login
                task.cancel(true);
                startActivity(intentRefresh);
            }
        },INITIAL_DELAY, PERIOD, TimeUnit.MINUTES);
    }
}
