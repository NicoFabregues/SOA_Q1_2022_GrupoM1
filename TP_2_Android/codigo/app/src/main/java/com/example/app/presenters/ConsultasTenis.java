package com.example.app.presenters;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.app.models.ConnectionManager;
import com.example.app.models.POJO.Jugador;
import com.example.app.models.POJO.RankingResponse;
import com.example.app.models.POJO.TorneosResponse;
import com.example.app.models.POJO.TorneosResult;
import com.example.app.models.TenisApi;
import com.example.app.views.MetricasActivity;
import com.example.app.views.TorneosActivity;
import com.example.app.views.RankingsActivity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultasTenis extends Sensores {

    public static final String BASE_URL = "https://tennis-live-data.p.rapidapi.com/";

    private RankingsActivity rankingsView;
    private TorneosActivity torneosView;
    protected ConnectionManager connectionManager;

    public ConsultasTenis(RankingsActivity rView, TorneosActivity pView) {
        super(rView == null ? pView : rView, rView == null ? MetricasActivity.class : TorneosActivity.class);
        this.connectionManager = new ConnectionManager(rView == null ? pView : rView);
        this.rankingsView = rView;
        this.torneosView = pView;
    }

    public void getRankings() {

        if(connectionManager.hayConexion()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TenisApi tenisApi = retrofit.create(TenisApi.class);

            Call<RankingResponse> call = tenisApi.getRankings();

            call.enqueue(new Callback<RankingResponse>() {
                @Override
                public void onResponse(Call<RankingResponse> call, Response<RankingResponse> response) {

                    if (!response.isSuccessful()) {
                        rankingsView.setText("Codigo: " + response.code());
                        return;
                    }

                    List<Jugador> jugadoresList = Arrays.asList(response.body().getResults().getRankings());

                    for (Jugador jugador : jugadoresList) {
                        String content = "";
                        content += "id:\t\t" + jugador.getId() + "\n";
                        content += "RANKING:\t\t" + jugador.getRanking() + "\n";
                        content += "PUNTOS:\t\t" + jugador.getRanking_points() + "\n";
                        content += "PAIS:\t\t" + jugador.getCountry() + "\n";
                        content += "NOMBRE COMPLETO:\t\t" + jugador.getFull_name() + "\n\n";
                        rankingsView.append(content);
                    }
                }

                @Override
                public void onFailure(Call<RankingResponse> call, Throwable t) {
                    rankingsView.setText(t.getMessage());
                }
            });
        } else {
            Toast.makeText(rankingsView.getApplicationContext(), "No hay conexión a Internet", Toast.LENGTH_LONG).show();
        }
    }


    public void getTorneos() {
        if(connectionManager.hayConexion()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TenisApi tenisApi = retrofit.create(TenisApi.class);

            String year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
            Log.e("AÑO: ", year);
            Call<TorneosResponse> call = tenisApi.getTorneos(year);

            call.enqueue(new Callback<TorneosResponse>() {
                @Override
                public void onResponse(Call<TorneosResponse> call, Response<TorneosResponse> response) {

                    if(!response.isSuccessful()) {
                        torneosView.setText("Codigo: " + response.code());
                        return;
                    }
                    if(response.body() == null){
                        Log.e("RESPONSE", "NULL");
                        return;
                    }
                    Log.e("RESPONSE", response.toString());

                    TorneosResponse myResponse = response.body();
                    Log.e("BODY", myResponse.toString());

                    for (TorneosResult torneo : myResponse.getResults()) {
                        String content = "";
                        content += "id:\t\t" + torneo.getId() + "\n";
                        content += "Nombre:\t\t" + torneo.getName() + "\n";
                        content += "Ciudad:\t\t" + torneo.getCity() + " - " + torneo.getCountry() + "\n";
                        content += "Superficie:\t\t" + torneo.getSurface() + "\n";
                        content += "Temporada:\t\t" + torneo.getSeason() + "\n\n";
                        torneosView.append(content);
                    }
                }

                @Override
                public void onFailure(Call<TorneosResponse> call, Throwable t) {
                    torneosView.setText(t.getMessage());
                }
            });
        } else {
            Toast.makeText(torneosView.getApplicationContext(), "No hay conexión a Internet", Toast.LENGTH_LONG).show();
        }

    }


}
