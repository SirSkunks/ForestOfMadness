package com.triosstudends.forestofmadness;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{

    Button play;
    Button options;

    SoundPool soundPool;
    int menuTheme = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("titleTheme.mp3");
            menuTheme = soundPool.load(descriptor, 0);
        } catch (IOException e) {
            //catches an exception.
        }
        soundPool.play(menuTheme, 1, 1, 0, -1, 1);

        Button startGame = findViewById(R.id.play);

        startGame.setOnClickListener(this);


    }

    @Override
    public void onClick (View v){
        startActivity(new Intent(MainMenu.this, GameView.class));
    }

    @Override
    public void onResume(){
        super.onResume();
        //soundPool.play(menuTheme,1,1,0,-1,1);
    }
}
