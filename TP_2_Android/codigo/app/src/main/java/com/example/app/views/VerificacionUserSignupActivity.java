package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.presenters.VerificacionSignupUsuario;

public class VerificacionUserSignupActivity extends AppCompatActivity {

    private VerificacionSignupUsuario presenter;
    private Button buttonSignup;
    private EditText editTextNombre;
    private EditText editTextApellido;
    private EditText editTextDNI;
    private EditText editTextMail;
    private EditText editTextPass;
    private EditText editTextComision;
    private EditText editTextGrupo;
    public IntentFilter filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion_user_signup);

        buttonSignup = findViewById(R.id.buttonSignup);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextDNI = findViewById(R.id.editTextDNI);
        editTextMail = findViewById(R.id.editTextMail);
        editTextPass = findViewById(R.id.editTextPass);
        editTextComision = findViewById(R.id.editTextComision);
        editTextGrupo = findViewById(R.id.editTextGrupo);

        presenter = new VerificacionSignupUsuario(this);
        configureBroadcastReceiver();

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.setNombre(editTextNombre.getText().toString());
                presenter.setApellido(editTextApellido.getText().toString());
                presenter.setDni(editTextDNI.getText().toString());
                presenter.setEmail(editTextMail.getText().toString());
                presenter.setPass(editTextPass.getText().toString());
                presenter.setComision(editTextComision.getText().toString());
                presenter.setGrupo(editTextGrupo.getText().toString());
                // Intento registrarme, en caso de poder vuelvo a la actividad del login
                presenter.signUp();
            }
        });
    }

    private void configureBroadcastReceiver() {
        // Metodo que registra un broadcast receiver para comunicar el servicio que recibe los
        // mensajes del servidor con el presenter de esta activity
        // Se registra la  accion SIGNUP_RESPONSE, para que cuando el servicio de signup la ejecute
        // se invoque automaticamente el OnReceive del presenter
        filtro = new IntentFilter("com.example.intentservice.intent.action.SIGNUP_RESPONSE");
        filtro.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(presenter, filtro);
    }

    public void mostrarToastMake(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stopAll();
    }
}