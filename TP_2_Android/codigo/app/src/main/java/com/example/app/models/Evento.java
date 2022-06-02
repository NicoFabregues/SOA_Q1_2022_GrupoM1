package com.example.app.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Evento {

    private static final String ENV = "PROD";

    private String typeEvents;
    private String description;

    public Evento(String typeEvents, String description) {
        this.typeEvents = typeEvents;
        this.description = description;
    }

    public JSONObject getJSONForRegistrarEvento(){
        JSONObject req = new JSONObject();
        try {
            req.put("env", ENV);
            req.put("type_events", this.typeEvents);
            req.put("description", this.description);
            return req;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
