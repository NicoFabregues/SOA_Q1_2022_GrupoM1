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
    private static final String DATABASE_NAME = "TennisTracker";
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
        // Actualizo la BD.
        // Si existe la tabla, la elimino.
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_METRICAS);

        // Creo la tabla
        onCreate(db);
    }


    public void agregarMetrica(Metrica metrica) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TABLA_METRICAS_TIPO, metrica.getTipo());
        values.put(KEY_TABLA_METRICAS_FECHA, metrica.getFecha());
        values.put(KEY_TABLA_METRICAS_VALOR, metrica.getValor());

        db.insert(TABLA_METRICAS, null, values);

        // Cierro la conexión a la BD.
        db.close();
    }


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


    public List<Metrica> getAllMetricas() {
        List<Metrica> contactList = new ArrayList<Metrica>();
        String selectQuery = "SELECT * FROM " + TABLA_METRICAS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Chequeo que haya respuesta
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Metrica metrica = new Metrica(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(3)),
                        cursor.getString(2));
                contactList.add(metrica);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return contactList;
    }

    public int updateMetricaValor(Metrica metrica) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TABLA_METRICAS_VALOR, metrica.getValor());

        return db.update(TABLA_METRICAS, values, KEY_TABLA_METRICAS_ID + " = ?",
                new String[] { String.valueOf(metrica.getId()) });
    }


} 
