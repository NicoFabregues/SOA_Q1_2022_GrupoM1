package com.example.app.presenters;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.app.models.ServiceCheckTokenExpiration;
import com.example.app.views.ActivityPrincipal;
import com.example.app.views.MetricasActivity;
import com.example.app.views.TenisActivity;

public class Principal extends Sensores {

    private ActivityPrincipal view;
    private Intent intentServiceCheckTokenExpiration;

    public Principal(ActivityPrincipal view, String refreshToken) {
        super(view, TenisActivity.class);
        this.view = view;
        this.intentServiceCheckTokenExpiration = new Intent(view, ServiceCheckTokenExpiration.class);
        this.startCheckTokenService(refreshToken);
    }

    private void startCheckTokenService(String refreshToken) {
        if (!this.isMyServiceRunning(ServiceCheckTokenExpiration.class))
            this.intentServiceCheckTokenExpiration.putExtra("refresh_token", refreshToken);
            this.view.startService(this.intentServiceCheckTokenExpiration);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.view.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Método para recibir el logout de la aplicación
        boolean stop_service = intent.getBooleanExtra("stop_service", false);
        if (stop_service) {
            this.stopAll();
        }
    }

    public void stopAll(){
        this.view.stopService(this.intentServiceCheckTokenExpiration);
    }
}
