package com.example.app.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ScanYourMenu";
    private static final String TABLA_METRICAS = "metricas";
    private static final String KEY_TABLA_METRICAS_ID = "id";
    private static final String KEY_TABLA_METRICAS_FECHA = "fecha";
    private static final String KEY_TABLA_METRICAS_TIPO = "tipo";
    private static final String KEY_TABLA_METRICAS_VALOR = "valor";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creo las tablas
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLA_METRICAS + "("
                + KEY_TABLA_METRICAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_TABLA_METRICAS_TIPO + " TEXT NOT NULL,"
                + KEY_TABLA_METRICAS_FECHA + " TEXT NOT NULL,"
                + KEY_TABLA_METRICAS_VALOR + " INTEGER NOT NULL" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Se actualiza la base de datos
        // Elimino la tabla anterior si existiera
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_METRICAS);

        // Creo las tablas de nuevo
        onCreate(db);
    }

    // Metodo para agregar una nueva metrica
    public void agregarMetrica(Metrica metrica) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TABLA_METRICAS_TIPO, metrica.getTipo()); // Tipo de metrica
        values.put(KEY_TABLA_METRICAS_FECHA, metrica.getFecha()); // Fecha de metrica
        values.put(KEY_TABLA_METRICAS_VALOR, metrica.getValor()); // Valor de metrica

        // Agrego el registro
        db.insert(TABLA_METRICAS, null, values);
        db.close(); // Cierro la conexion a la base de datos
    }

    // Metodo para buscar una metrica por tipo y fecha
    public Metrica getMetrica(String tipo, String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLA_METRICAS,
                new String[] { KEY_TABLA_METRICAS_ID, KEY_TABLA_METRICAS_TIPO,
                        KEY_TABLA_METRICAS_FECHA, KEY_TABLA_METRICAS_VALOR},
                KEY_TABLA_METRICAS_TIPO + " LIKE ?" + " AND " + KEY_TABLA_METRICAS_FECHA + " LIKE ?",
                new String[] { tipo, fecha},
                null,
                null,
                null,
                null);
        // Chequeo que el cursor no sea nulo y haya al menos un elemento como resultado de la query
        if (cursor != null && cursor.moveToFirst()) {
            Metrica metrica = new Metrica(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    Integer.parseInt(cursor.getString(3)),
                    cursor.getString(2));
            cursor.close();
            return metrica;
        }
        else {
            return null;
        }
    }

    // Metodo para traer todas las metricas existentes
    public List<Metrica> getAllMetricas() {
        List<Metrica> contactList = new ArrayList<Metrica>();
        String selectQuery = "SELECT * FROM " + TABLA_METRICAS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Chequeo que el cursor no sea nulo y haya al menos un elemento como resultado de la query
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Metrica metrica = new Metrica(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(3)),
                        cursor.getString(2));
                contactList.add(metrica);
            } while (cursor.moveToNext());
            // Cierro el cursor una vez obtuve todas las metricas
            cursor.close();
        }
        return contactList;
    }

    // Metodo para actualizar el valor de una metrica
    public int updateMetricaValor(Metrica metrica) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TABLA_METRICAS_VALOR, metrica.getValor());

        return db.update(TABLA_METRICAS, values, KEY_TABLA_METRICAS_ID + " = ?",
                new String[] { String.valueOf(metrica.getId()) });
    }
} 
