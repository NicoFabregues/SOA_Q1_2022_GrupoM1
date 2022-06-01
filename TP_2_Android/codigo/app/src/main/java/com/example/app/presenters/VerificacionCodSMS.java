package com.example.app.presenters;

import com.example.app.models.SMSManager;
import com.example.app.views.VerificacionCodSMSActivity;

public class VerificacionCodSMS {

    private SMSManager model;
    private VerificacionCodSMSActivity view;

    public VerificacionCodSMS(VerificacionCodSMSActivity view) {
        this.model = new SMSManager();
        this.view = view;
    }

    public void setCodIngresado(String codigoIngresado) {
        this.model.setCodIngresado(codigoIngresado);
    }

    public boolean verificarCodIngresado() {
        return this.model.verificarCodIngresado();
    }

    public void setCodEnviado(String codEnviado) {
        this.model.setCodSMS(codEnviado);
    }

}
