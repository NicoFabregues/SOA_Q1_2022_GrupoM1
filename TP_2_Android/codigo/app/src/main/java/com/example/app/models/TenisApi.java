package com.example.app.models;

import com.example.app.models.POJO.RankingResponse;
import com.example.app.models.POJO.TorneosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface TenisApi {

    @Headers({
            "X-RapidAPI-Host: tennis-live-data.p.rapidapi.com",
            "X-RapidAPI-Key: a5bd8d8087msha03d00166705a18p1a19d3jsn2576f2cd95d1"
    })
    @GET("rankings/ATP")
    Call<RankingResponse> getRankings();

    @Headers({
            "X-RapidAPI-Host: tennis-live-data.p.rapidapi.com",
            "X-RapidAPI-Key: a5bd8d8087msha03d00166705a18p1a19d3jsn2576f2cd95d1"
    })
    @GET("/tournaments/ATP/{year}")
    Call<TorneosResponse> getTorneos(@Path("year") String year);
}
