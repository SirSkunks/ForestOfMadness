package com.triosstudends.forestofmadness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    //Starting the Beta Branch
    Button play;
    Button options;


    MediaPlayer player;
    boolean musicMuted = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

      musicMuted = Options.returnBool();


        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("titleTheme.mp3");


        } catch (IOException e) {
            //catches an exception.
        }


        Button startGame = findViewById(R.id.play);
        startGame.setOnClickListener(this);

        Button loadOptions = findViewById(R.id.options);
        loadOptions.setOnClickListener(this);

    }

    @Override
    public void onClick (View v){
        switch (v.getId()){
            // Goes to the Game screen
            case R.id.play:
                startActivity(new Intent(MainMenu.this, GameView.class));
                break;
            // Goes to the Options screen
            case R.id.options:
                Intent o = new Intent(this,Options.class);
                startActivity(o);
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        playMusic();
    }

    public void playMusic() {
//
        if(!musicMuted) {
            if(player == null){
                player = MediaPlayer.create(this,R.raw.levelonebgm);

                }
            player.start();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        musicMuted = Options.returnBool();
       if(player != null){
           player.release();
           player = null;
       }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
       player.release();
       player = null;
    }





}
