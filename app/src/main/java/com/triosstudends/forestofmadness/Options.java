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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

public class Options extends AppCompatActivity  implements View.OnClickListener{
    // Declaring Seekbars and AudioManager
    Button mainMenu;
    private SeekBar musicBar;
    private SeekBar effectBar;
    private AudioManager audioManager;
    private Switch musicMute;
    private Switch SeMute;
    public static boolean musicMuted = false;
    public static boolean SeMuted = false;

    SoundPool soundPool;
    int menuTheme = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        //Load Sounds Pool
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("titleTheme.mp3");
            menuTheme = soundPool.load(descriptor, 0);
        }catch (IOException e){
            //catches an exception.
        }

        if(musicMuted) {
            soundPool.play(menuTheme, 1, 1, 0, -1, 1);
        }


        //Sets Onclick listner for Button To return to menu
        mainMenu = findViewById(R.id.mainMenu);
        mainMenu.setOnClickListener(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();
    }
    private void initControls(){
        //Sets SeekBar max to system max volume as well as sets teh currnet bar level to the current system audio level.
      try{
            musicMute = findViewById(R.id.switchMusic);
             SeMute = findViewById(R.id.switchSe);
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
                    //sets volume level to the progress of the seek bar
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
                }
            });
            musicMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if(isChecked){
                        musicMuted = true;
                        Toast.makeText(getApplicationContext(),"musicMuted",Toast.LENGTH_LONG).show();
                    }
                    if(!isChecked){
                        musicMuted = false;
                        Toast.makeText(getApplicationContext(),"musicOn",Toast.LENGTH_LONG).show();

                    }


                }


             });
          SeMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
          {
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
              {
                  if(isChecked){
                      SeMuted = true;
                      Toast.makeText(getApplicationContext(),"SoundEffectMuted",Toast.LENGTH_LONG).show();
                  }
                  if(!isChecked){
                      SeMuted = false;
                      Toast.makeText(getApplicationContext(),"SoundEffectOn",Toast.LENGTH_LONG).show();
                  }


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
    public void onResume(){
        super.onResume();
        soundPool.play(menuTheme, 1, 1, 0, -1, 1);
    }

    @Override
    public void onClick(View v) {
        //load back to the main menu once the back button is clicked.
        Intent i = new Intent(this,MainMenu.class);
        startActivity(i);
    }

    public  static boolean returnBool(){
        return musicMuted;

    }

    public static boolean returnBool2(){
        return SeMuted;

    }
}
