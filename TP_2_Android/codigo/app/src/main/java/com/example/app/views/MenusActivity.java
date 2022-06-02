package com.example.app.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;

import java.util.ArrayList;
import java.util.Map;

public class MenusActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    ListView lista;
    ArrayList<String> listaMediciones;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);

        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.archivo_menus), Context.MODE_PRIVATE);
        listaMediciones = new ArrayList<>();
        llenarLista();

        lista = findViewById(R.id.listaPartidos);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMediciones);

        lista.setAdapter(arrayAdapter);
    }

    private synchronized void llenarLista(){
        Map<String,?> keys = sharedPref.getAll();

        for (Map.Entry<String,?> entry : keys.entrySet()){
            listaMediciones.add(entry.getValue().toString());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
