package com.example.app.views;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.presenters.ConsultasTenis;

public class RankingsActivity extends AppCompatActivity {

    private TextView mTenisRankingsView;
    private ConsultasTenis presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        mTenisRankingsView = findViewById(R.id.tenisRankingsText);

        presenter = new ConsultasTenis(this, null);

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
