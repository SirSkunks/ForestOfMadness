package com.triosstudends.forestofmadness;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameView extends AppCompatActivity implements View.OnClickListener{

    TextView score;

    Button moveLeft;
    Button moveRight;

    private SoundPool soundPool;
    int levelTheme = -1;

    int playerScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);


    }
}
