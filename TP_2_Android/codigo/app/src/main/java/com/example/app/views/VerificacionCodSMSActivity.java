package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.presenters.VerificacionCodSMS;

public class VerificacionCodSMSActivity extends AppCompatActivity {

    private VerificacionCodSMS presenter;
    private Button buttonValidar;
    private EditText codigoIngresado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion_codigo);

        buttonValidar = findViewById(R.id.buttonValidar);
        codigoIngresado = findViewById(R.id.editTextCodigoIngresado);

        String codEnviado = getIntent().getStringExtra("codEnviado");

        presenter = new VerificacionCodSMS(this);

        presenter.setCodEnviado(codEnviado);

        buttonValidar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.setCodIngresado(codigoIngresado.getText().toString());
                // Verifico el codigo que ingreso el usuario
                if(!presenter.verificarCodIngresado()){
                    Toast.makeText(getApplicationContext(), "Codigo incorrecto, vuelva a generar generar el codigo", Toast.LENGTH_LONG).show();
                    //Cierro la actividad actual
                    finish();
                }
                else{
                    // Ejecuto la actividad del login
                    lanzarVerificarUserLogin();
                }
            }
        });
    }

    public void lanzarVerificarUserLogin(){
        Intent intent = new Intent(this, VerificacionUserLoginActivity.class);
        this.startActivity(intent);
        finish();
    }


}