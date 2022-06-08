package com.example.app.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private static final String ENV = "PROD";

    private String email;
    private String pass;
    private String nombre;
    private String apellido;
    private String dni;
    private String comision;
    private String grupo;

    public User() {}

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setComision(String comision) {
        this.comision = comision;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public JSONObject getJSONForLogIn(){
        JSONObject req = new JSONObject();
        try {
            req.put("email", this.email);
            req.put("password", this.pass);
            return req;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUser(){
        return this.email;
    }

    public JSONObject getJSONForSignup(){
        JSONObject req = new JSONObject();
        try {
            req.put("env", ENV);
            req.put("name", this.nombre);
            req.put("lastname", this.apellido);
            req.put("dni", Integer.parseInt(this.dni));
            req.put("email", this.email);
            req.put("password", this.pass);
            req.put("commission", Integer.parseInt(this.comision));
            req.put("group", Integer.parseInt(this.grupo));
            return req;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
