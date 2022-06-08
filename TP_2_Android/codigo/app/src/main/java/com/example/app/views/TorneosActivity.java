package com.example.app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.presenters.ConsultasTenis;

public class TorneosActivity extends AppCompatActivity {

    private TextView mTenisTorneosView;
    private ConsultasTenis presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_torneos);

        mTenisTorneosView = findViewById(R.id.tenisTorneosText);

        presenter = new ConsultasTenis(null, this);

        getTorneos();
    }

    private void getTorneos() {
        this.presenter.getTorneos();
    }

    public void setText(String s) {
        mTenisTorneosView.setText(s);
    }

    public void append(String content) {
        mTenisTorneosView.append(content);
    }
}