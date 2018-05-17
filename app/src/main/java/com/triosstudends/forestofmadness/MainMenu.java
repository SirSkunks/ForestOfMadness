package com.triosstudends.forestofmadness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
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


    SoundPool soundPool;
    boolean musicMuted = false;
    int menuTheme = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

      musicMuted = Options.returnBool();

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playMusic();
            }
        });

        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("titleTheme.mp3");
            menuTheme = soundPool.load(descriptor, 0);

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

        if(!musicMuted) {
            soundPool.play(menuTheme, 1, 1, 0, -1, 1);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        musicMuted = Options.returnBool();
        soundPool.autoPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        soundPool.autoPause();
    }





}
