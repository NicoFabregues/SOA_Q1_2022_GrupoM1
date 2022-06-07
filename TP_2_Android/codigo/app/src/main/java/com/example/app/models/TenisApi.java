package com.example.app.models;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface TenisApi {

    @Headers({
            "X-RapidAPI-Host: tennis-live-data.p.rapidapi.com",
            "X-RapidAPI-Key: 7c97a7cafdmsh3f6dfbb475086b9p14cb1cjsn5b27e84e88d1"
    })
    @GET("rankings/ATP")
    Call<MyResponse> getRankings();
}
