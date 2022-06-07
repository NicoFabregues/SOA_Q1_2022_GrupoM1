package com.example.app.presenters;

import com.example.app.models.Jugador;
import com.example.app.models.MyResponse;
import com.example.app.models.TenisApi;
import com.example.app.views.ActivityPrincipal;
import com.example.app.views.MetricasActivity;
import com.example.app.views.TenisActivity;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultasTenis extends Sensores{

    public static final String BASE_URL = "https://tennis-live-data.p.rapidapi.com/";

    private TenisActivity view;

    public ConsultasTenis(TenisActivity view) {
        super(view, MetricasActivity.class);
        this.view = view;

    }

    public void getRankings() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TenisApi tenisApi = retrofit.create(TenisApi.class);

        Call<MyResponse> call = tenisApi.getRankings();

        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                if (!response.isSuccessful()) {
                    view.setText("Codigo: " + response.code());
                    return;
                }

                List<Jugador> jugadoresList = Arrays.asList(response.body().getResults().getRankings());

                for (Jugador jugador : jugadoresList) {
                    String content = "";
                    content += "id:\t\t" + jugador.getId() + "\n";
                    content += "RANKING:\t\t" + jugador.getRanking() + "\n";
                    content += "PUNTOS:\t\t" + jugador.getRanking_points() + "\n";
                    content += "PAIS:\t\t" + jugador.getCountry() + "\n";
                    content += "NOMBRE COMPLETO:\t\t" + jugador.getFull_name() + "\n";
                    content += "Movimiento:\t\t" + jugador.getMovement() + "\n\n";
                    view.append(content);
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                view.setText(t.getMessage());

            }
        });
    }


}
