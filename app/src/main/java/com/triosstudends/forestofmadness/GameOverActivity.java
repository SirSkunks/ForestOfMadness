package com.triosstudends.forestofmadness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {

    Button restart;
    Button main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Button replay = findViewById(R.id.restart);
        replay.setOnClickListener(this);

        Button menu = findViewById(R.id.main);
        menu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.restart:
                startActivity(new Intent(GameOverActivity.this, GameView.class));
                break;
            case R.id.main:
                Intent m = new Intent(this, MainMenu.class);
                startActivity(m);
                break;
        }

    }
}
