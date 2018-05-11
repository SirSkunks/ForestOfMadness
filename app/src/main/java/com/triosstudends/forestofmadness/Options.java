package com.triosstudends.forestofmadness;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

public class Options extends AppCompatActivity  implements View.OnClickListener{

    Button mainMenu;
    private SeekBar musicBar;
    private SeekBar effectBar;
    private AudioManager audioManager;



    SoundPool soundPool;
    int menuTheme = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("titleTheme.mp3");
            menuTheme = soundPool.load(descriptor, 0);
        }catch (IOException e){
            //catches an exception.
        }
        soundPool.play(menuTheme,1,1,0,-1,1);



        mainMenu = findViewById(R.id.mainMenu);
        mainMenu.setOnClickListener(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();


    }
    private void initControls(){
      try{
            musicBar = findViewById(R.id.SbMusic);
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            musicBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            musicBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progress;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    this.progress = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);

                }
            });

      }catch(Exception e){

      }
    }

    @Override
    public void onPause(){
        super.onPause();
        soundPool.autoPause();
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this,MainMenu.class);
        startActivity(i);



    }
}
