package com.example.app.models;

import android.telephony.SmsManager;

public class SMSManager {

    private static final int RANDOM_FACTOR_COD_SMS = 999999;

    private String codIngresado;
    private String codSMS;

    public SMSManager() {}

    public String getCodSMS() {
        return codSMS;
    }

    public void setCodSMS(String codSMS){
        this.codSMS = codSMS;
    }

    public void setCodIngresado(String codIngresado) {
        this.codIngresado = codIngresado;
    }

    private void generarCodSMS(){
        int cod = (int) (Math.random() * RANDOM_FACTOR_COD_SMS);
        this.codSMS = Integer.toString(cod);
    }

    public void enviarSMS(String numCelular, boolean permisoConcedido){
        if (!permisoConcedido) return;
        SmsManager smsManager = SmsManager.getDefault();
        generarCodSMS();
        smsManager.sendTextMessage(numCelular, null, this.codSMS, null, null);
    }

    public boolean verificarCodIngresado() {
        return this.codIngresado.equals(this.codSMS);
    }
}
