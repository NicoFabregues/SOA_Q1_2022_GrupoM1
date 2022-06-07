package com.example.app.models.POJO;

import java.util.List;

public class TorneosResponse {

    public TorneosMeta meta;
    public List<TorneosResult> results = null;

    public TorneosMeta getMeta() {
        return meta;
    }

    public List<TorneosResult> getResults() {
        return results;
    }
}
