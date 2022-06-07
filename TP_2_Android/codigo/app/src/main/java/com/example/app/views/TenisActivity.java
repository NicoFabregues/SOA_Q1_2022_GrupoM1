package com.example.app.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.presenters.ConsultasTenis;
import com.example.app.presenters.EnvioSMS;

import java.util.ArrayList;
import java.util.Map;

public class TenisActivity extends AppCompatActivity {

    private TextView mTenisRankingsView;
    private ConsultasTenis presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenis);

        mTenisRankingsView = findViewById(R.id.tenisRankingsText);

        presenter = new ConsultasTenis(this);

        getRankings();

    }

    private void getRankings() {
        this.presenter.getRankings();
    }

    public void setText(String s) {
        mTenisRankingsView.setText(s);
    }

    public void append(String content) {
        mTenisRankingsView.append(content);
    }
}
