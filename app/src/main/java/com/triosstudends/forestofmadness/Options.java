package com.triosstudends.forestofmadness;

import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

public class Options extends AppCompatActivity  implements View.OnClickListener{
    // Declaring Seekbars and AudioManager
    Button mainMenu;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private SeekBar musicBar;
    private AudioManager audioManager;
    public static boolean musicMuted = false;
    public static boolean SeMuted = false;
    String Mmuted;
    String Smuted;
    MediaPlayer player;

    /* testing git branch */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        //Setting up pref save for options menu
        prefs = getSharedPreferences(Mmuted,MODE_PRIVATE);
        editor = prefs.edit();
        //sets music muted to what is saved in preff
        musicMuted =prefs.getBoolean(Mmuted,musicMuted);

        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("titleTheme.mp3");

        }catch (IOException e){
            //catches an exception.
        }

        //Sets Onclick listner for Button To return to menu
        mainMenu = findViewById(R.id.mainMenu);
        mainMenu.setOnClickListener(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();

    }
    private void initControls(){
        //Sets SeekBar max to system max volume as well as sets teh currnet bar level to the current system audio level.
        Switch musicMute;
        Switch SeMute;
        try{
            musicMute = findViewById(R.id.switchMusic);
             SeMute = findViewById(R.id.switchSe);
            musicBar = findViewById(R.id.SbMusic);
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            musicBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            musicBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            //sets musicMute toggle postion based on musicMuted bool
            if(musicMuted){
                musicMute.setChecked(true);
            }

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
                        editor.putBoolean(Mmuted,musicMuted);
                        player.pause();
                        Toast.makeText(getApplicationContext(),"musicMuted",Toast.LENGTH_LONG).show();
                    }
                    else {
                        musicMuted = false;
                        editor.putBoolean(Mmuted,musicMuted);
                        playMusic();
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
        if(player != null){
            player.release();
            player = null;
        }
    }
    
    @Override
    public void onResume(){
        super.onResume();
       playMusic();
    }

    @Override
    public void onClick(View v) {
        //load back to the main menu once the back button is clicked.
        Intent i = new Intent(this,MainMenu.class);
        startActivity(i);
    }
    //funtion to rerturn bool for muting music
    public  static boolean returnBool(){
        return musicMuted;

    }
    //function created to handloe the soundpool.play call based on a bool
    public void playMusic(){

        if(!musicMuted) {
            if(player == null){
                player = MediaPlayer.create(this,R.raw.levelonebgm);
            }
            player.start();
        }
    }

    //function to return bool for muting Sound Effects
    public static boolean returnBool2(){
        return SeMuted;

    }
}
