package com.triosstudends.forestofmadness;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

public class GameView extends AppCompatActivity implements View.OnClickListener {

    TextView playerScore;

    CharacterView characterView;
    Background background;
    ButtonLeft buttonLeft;
    ButtonRight buttonRight;
    Platforms platform;
    Canvas canvas;
    Paint paint;

    private SoundPool soundPool;
    int levelTheme = -1;

    int pScore;
    boolean playing = true;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String dataName = "Data";
    String intName = "Int";
    int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = preferences.edit();

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try{
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("levelOneBgm.ogg");
            levelTheme = soundPool.load(descriptor, 0);
        }catch (IOException e){
            e.printStackTrace();
        }
        if(Options.returnBool() == true) {
            soundPool.play(levelTheme,1, 1,0,-1,1);

        }


        playerScore = findViewById(R.id.playerScore);

        characterView = new CharacterView(this);
        setContentView(characterView);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        soundPool.autoPause();
    }

    @Override
    protected void onPause(){
        super.onPause();
        characterView.pause();
        soundPool.autoPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        characterView.resume();
        soundPool.play(levelTheme,1, 1,0,-1,1);
    }

    @Override
    public void onClick(View v) {
        //onClick(characterView);
    }

    class CharacterView extends SurfaceView implements Runnable {

         // System Elements
         boolean running = true;
         Thread thread = null;
         Display display;
         int screenWidth;
         int screenHeight;

         //Canvas Elements
         final SurfaceHolder holder;

         //Image Elements
         Bitmap bitmap;
         Bitmap bgBmp;
         Bitmap world;
         Bitmap btnLeft;
         Bitmap btnRight;

         Character character;

         int vx = 0;
         int vy = 0;

         //Timer Elements
         long lastFrameTime;
         long deltaTime;
         int fps;

         public CharacterView(Context context){
             super(context);

             holder = getHolder();

             display = getWindowManager().getDefaultDisplay();
             Point size = new Point();
             display.getSize(size);
             screenHeight = size.y;
             screenWidth = size.x;

             BitmapFactory.Options options = new BitmapFactory.Options();
             options.inScaled = false;
             bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.jade);
             bgBmp = BitmapFactory.decodeResource(getResources(),R.drawable.background);
             world = BitmapFactory.decodeResource(getResources(), R.drawable.worldsprites);
             btnLeft = BitmapFactory.decodeResource(getResources(),R.drawable.temp1);
             btnRight = BitmapFactory.decodeResource(getResources(),R.drawable.temp2);

             // Background placement
             background = new Background(bgBmp);

             // Platform placement
             platform = new Platforms(world);
             platform.addAnimation("platform1", 0, 0, 0,64, 64, true);
             platform.addAnimation("platform2", 1, 0, 0,64, 64, true);
             platform.addAnimation("platform3", 2, 0, 0,64, 64, true);

             // Pick-ups
             platform = new Platforms(world);
             //platform.addAnimation("pills", );

             // Left button Creation
             buttonLeft = new ButtonLeft(btnLeft);
             buttonLeft.x =  screenWidth - buttonLeft.width * 6 + 3;
             buttonLeft.y = screenHeight - buttonLeft.height;

             // Right button Creation
             buttonRight = new ButtonRight(btnRight);
             buttonRight.x = screenWidth - buttonRight.width;
             buttonRight.y = screenHeight - buttonRight.height;

             // Character Creation and animations.
             character = new Character(bitmap);
             character.addAnimation("runRight", 0, 8, 7, 64,64, true);
             character.addAnimation("runLeft", 15, 8, 7, 64, 64, true);
             character.addAnimation("idle", 8, 1, 7, 64,64, true);
             character.addAnimation("jumpRight", 10, 5, 4, 64, 64,true);
             character.addAnimation("jumpLeft", 25, 5, 4, 64, 64, true);
             character.setAnimation("idle");

             character.x = screenWidth / 2 - character.width / 2;
             character.y = screenHeight / 2 - character.height / 2;
         }

         public void platformGeneration(){
             Random random = new Random();
             int generate = random.nextInt(100) + 1;
             if (generate <= 10){

             }
         }
         @Override
         public boolean onTouchEvent(MotionEvent event){
             float x = event.getX();
             float y = event.getY();

             switch (event.getAction() & MotionEvent.ACTION_MASK){
             case MotionEvent.ACTION_DOWN:
                 // Check to see if the user touches within the left button
                 if (x >= buttonLeft.x && x < (buttonLeft.x + buttonLeft.width)
                         && y >= buttonLeft.y && y < (buttonLeft.y + buttonLeft.height)) {
                     vx = -5;
                     character.setAnimation("runLeft");
                 }
                 // Check to see if the user touches within the right button
                 else if(x >= buttonRight.x && x < (buttonRight.x + buttonRight.width)
                         && y >= buttonRight.y && y < (buttonRight.y + buttonRight.height)){
                     vx = 5;
                     character.setAnimation("runRight");
                 }
                 // Check to see if the user touches anywhere but the buttons
                 else if(x != buttonLeft.x && x != (buttonLeft.x + buttonLeft.width)
                         && y != buttonLeft.y && y != (buttonLeft.y + buttonLeft.height) ||
                         x != buttonRight.x && x != (buttonRight.x + buttonRight.width)
                                 && y != buttonRight.y && y != (buttonRight.y + buttonRight.height)) {
                     vy = 0;// Temporarily set to 0 until more is added to the game.
                     character.setAnimation("jumpRight");
                 }
                 break;
                 case MotionEvent.ACTION_UP:
                     // Stop character movement when
                     if(x != buttonLeft.x && x != (buttonLeft.x + buttonLeft.width)
                             && y != buttonLeft.y && y != (buttonLeft.y + buttonLeft.height) ||
                             x != buttonRight.x && x != (buttonRight.x + buttonRight.width)
                                     && y != buttonRight.y && y != (buttonRight.y + buttonRight.height)) {
                         vx = 0;
                         vy = 0; // Temporarily set to 0 until more is added to the game.
                         character.setAnimation("idle");
                     }
             }
             return true;
         }

         public void updateLogic(){
             character.x += vx;
             character.y += vy;
             character.update(deltaTime);

             // Temporary level boundaries.
             //if the character touches the bottom of the screen stop the movement
             if(character.y + character.height > screenHeight){
                 vy = 0;
                 character.setAnimation("idle");
             }
             // if the character touches the top they go back down
             else if(character.y <= 0){
                 vy = 5;
             }
         }

         private void drawCanvas(){
             if (holder.getSurface().isValid()){
                 canvas = holder.lockCanvas();
                 canvas.drawColor(Color.argb(0,0,0,0));

                 background.draw(canvas);
                 buttonLeft.draw(canvas);
                 buttonRight.draw(canvas);
                 character.draw(canvas);
                 holder.unlockCanvasAndPost(canvas);
             }
         }

         public void controlFPS(){
             long timeThisFrame = (System.nanoTime() / 1000000 - lastFrameTime);
             long timeToSleep = 15 - timeThisFrame;
             deltaTime = 0;

             if(timeThisFrame > 0){
                 fps = (int) (1000/ timeThisFrame);
                 deltaTime = timeThisFrame;
             }
             if(timeToSleep > 0){
                 try{
                     thread.sleep(timeToSleep);
                 }catch (InterruptedException e){
                    e.printStackTrace();
                 }
             }

             lastFrameTime = System.nanoTime() / 1000000;
         }

         @Override
         public void run() {
             while(running){
                 updateLogic();
                 drawCanvas();
                 controlFPS();
             }
         }

         public void resume(){
             thread = new Thread(this);
             thread.start();
             running = true;

         }
         public void pause(){
             running = false;
             try{
                 thread.join();
             }catch (InterruptedException e){
                e.printStackTrace();
             }
         }
     }
}
