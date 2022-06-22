package com.example.app.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Metrica {

    private int id;
    private String user;
    private Date fecha;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public Metrica() {
    }

    public Metrica(String user, String fecha) {
        this.user = user;
        this.setFecha(fecha);
    }

    public Metrica(int id, String user, String fecha) {
        this.id = id;
        this.user = user;
        this.setFecha(fecha);
    }

    public Metrica(int id, String user, Date fecha) {
        this.id = id;
        this.user = user;
        this.fecha = fecha;
    }

    public Metrica(String user, Date fecha) {
        this.user = user;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }


    public void setUser(String user) {
        this.user = user;
    }
    public String getUser() {
        return user;
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
        return user +  " " + this.getFecha();
    }
}
