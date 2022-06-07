package com.example.app.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Metrica {

    private int id;
    private String tipo;
    private int valor;
    private Date fecha;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public Metrica() {
    }

    public Metrica(String tipo, int valor, String fecha) {
        this.tipo = tipo;
        this.valor = valor;
        this.setFecha(fecha);
    }

    public Metrica(int id, String tipo, int valor, String fecha) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.setFecha(fecha);
    }

    public Metrica(int id, String tipo, int valor, Date fecha) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = fecha;
    }

    public Metrica(String tipo, int valor, Date fecha) {
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getFecha() {
        return formatter.format(fecha);
    }

    public void setFecha(String fecha) {
        try {
            this.fecha = formatter.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return tipo +  " el dia " + this.getFecha() + ": " + valor;
    }
}
