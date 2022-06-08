package com.example.app.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.presenters.EnvioSMS;

public class EnvioSMSActivity extends AppCompatActivity {

    private static final int REQUEST_SEND_SMS = 1;

    private EnvioSMS presenter;
    private Button buttonEnviarSMS;
    private EditText numCelular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envio_sms);

        int brightnessMode = 0;
        try {
            brightnessMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        buttonEnviarSMS = findViewById(R.id.buttonEnviarSMS);
        numCelular = findViewById(R.id.editTextNumCelular);

        presenter = new EnvioSMS(this);

        boolean permisoConcedido = presenter.solicitarPermisoSMS();

        buttonEnviarSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(numCelular.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "¡Ingrese un número de celular!", Toast.LENGTH_LONG).show();
                }
                else{
                    presenter.enviarSMS(numCelular.getText().toString(), permisoConcedido);
                    lanzarVerificarCodigo();
                }
            }
        });
    }
    public void lanzarVerificarCodigo(){
        Intent intent = new Intent(this, VerificacionCodSMSActivity.class);
        intent.putExtra("codEnviado", presenter.getCodEnviado());
        this.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permiso a enviar SMS concedido", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText((getApplicationContext()), "Permiso a enviar SMS denegado", Toast.LENGTH_LONG).show();
            }
        }
    }
}