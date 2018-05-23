package com.triosstudends.forestofmadness;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameView extends AppCompatActivity implements View.OnClickListener {

    TextView playerScore;

    CharacterView characterView;
    Background background;
    ButtonLeft buttonLeft;
    ButtonRight buttonRight;

    Canvas canvas;
    Paint paint;



    private SoundPool soundPool;
    int levelTheme = -1;
    boolean musicMuted = false;
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
        musicMuted = Options.returnBool();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playMusic();
            }
        });
        try{
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("levelOneBgm.ogg");
            levelTheme = soundPool.load(descriptor, 0);
        }catch (IOException e){
            e.printStackTrace();
        }



        characterView = new CharacterView(this);
        setContentView(characterView);


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //soundPool.autoPause();
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
        playMusic();
        //soundPool.play(levelTheme,1, 1,0,-1,1);
    }

    @Override
    public void onClick(View v) {
        //onClick(characterView);
    }
    public void playMusic(){
        if(!musicMuted){
            soundPool.play(levelTheme,1, 1,0,-1,1);
        }
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
         Bitmap pickUps;
         Bitmap btnLeft;
         Bitmap btnRight;

         Sprite character;
         Sprite items;

         int vx = 0;
         int vy = 0;

         //Timer Elements
         long lastFrameTime;
         long deltaTime;
         int fps;

        ArrayList<Sprite> plats;

         public CharacterView(Context context){
             super(context);

             plats = new ArrayList<>();
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
             pickUps = BitmapFactory.decodeResource(getResources(),R.drawable.items);
             btnLeft = BitmapFactory.decodeResource(getResources(),R.drawable.leftbutton);
             btnRight = BitmapFactory.decodeResource(getResources(),R.drawable.rightbutton);

             // Background placement
             background = new Background(bgBmp);
             background.width = screenWidth;
             background.height = screenHeight;

             // Pick-ups
             items = new Sprite(pickUps);
             items.addAnimation("coffee", 0, 1, 1, 34, 34, false);
             items.addAnimation("pills", 1, 1, 1, 34, 34, false);

             // Left button Creation
             buttonLeft = new ButtonLeft(btnLeft);
             buttonLeft.x = 0;
             buttonLeft.y = screenHeight - buttonLeft.height;

             // Right button Creation
             buttonRight = new ButtonRight(btnRight);
             buttonRight.x = screenWidth - buttonRight.width;
             buttonRight.y = screenHeight - buttonRight.height;

             // Character Creation and animations.
             character = new Sprite(bitmap);
             character.addAnimation("runRight", 0, 8, 7, 64,64, true);
             character.addAnimation("runLeft", 15, 8, 7, 64, 64, true);
             character.addAnimation("idle", 8, 1, 7, 64,64, true);
             character.addAnimation("jumpRight", 10, 5, 4, 64, 64,false);
             character.addAnimation("jumpLeft", 25, 5, 4, 64, 64, false);
             character.setAnimation("idle");

             character.x = screenWidth / 2 - character.width / 2;
             character.y = screenHeight / 2 - character.height / 2;


         }

         public void platformGeneration(){
             Random random = new Random();
             int generate = random.nextInt(100) + 1;

             // Bottom Row of platforms
             if (generate <= 50) {

                 Sprite lead = new Sprite(world);
                 lead.sheet_rows = 4;
                 lead.sheet_cols = 5;
                 lead.addAnimation("platform1", 0, 1, 1, 64, 64, false);
                 lead.setAnimation("platform1");
                 lead.x = screenWidth;
                 lead.y = screenHeight - lead.height;

                 Sprite middle = new Sprite(world);
                 middle.sheet_rows = 4;
                 middle.sheet_cols = 5;
                 middle.addAnimation("platform2", 1, 1, 1, 64, 64, false);
                 middle.setAnimation("platform2");
                 middle.x = lead.x + lead.width;
                 middle.y = lead.y;

                 Sprite end = new Sprite(world);
                 end.sheet_rows = 4;
                 end.sheet_cols = 5;
                 end.addAnimation("platform3", 2, 1, 1, 64, 64, false);
                 end.setAnimation("platform3");
                 end.x = middle.x + middle.width;
                 end.y = lead.y;
                 plats.add(lead);
                 plats.add(middle);
                 plats.add(end);
             }

             // Middle row of platforms
             else {

                 Sprite lead = new Sprite(world);
                 lead.sheet_rows = 4;
                 lead.sheet_cols = 5;
                 lead.addAnimation("platform1", 0, 1, 1, 64, 64, false);
                 lead.setAnimation("platform1");
                 lead.x = screenWidth;
                 lead.y = screenHeight / 2 - 3;

                 Sprite middle = new Sprite(world);
                 middle.sheet_rows = 4;
                 middle.sheet_cols = 5;
                 middle.addAnimation("platform2", 1, 1, 1, 64, 64, false);
                 middle.setAnimation("platform2");
                 middle.x = lead.x + lead.width;
                 middle.y = lead.y;

                 Sprite end = new Sprite(world);
                 end.sheet_rows = 4;
                 end.sheet_cols = 5;
                 end.addAnimation("platform3", 2, 1, 1, 64, 64, false);
                 end.setAnimation("platform3");
                 end.x = middle.x + middle.width;
                 end.y = lead.y;
                 plats.add(lead);
                 plats.add(middle);
                 plats.add(end);
             }
         }

         public void updatePlatforms(){
             Iterator<Sprite> i = plats.iterator();
             while (i.hasNext()){
                 Sprite p = i.next();
                 p.x -= 10;

                 if(p.x + p.width < 0){
                     i.remove();
                 }
             }
         }

         public void genItems(){
             Random random = new Random();
             int generate = random.nextInt(100) + 1;


             if(generate >= 50){
                Sprite item = new Sprite(pickUps);
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
                     vy = -5;
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
                         vy = 5;
                         character.setAnimation("runRight");
                     }
             }
             return true;
         }

         public void updateLogic(){

             character.x += vx;
             character.y += vy;
             character.update(deltaTime);
             updatePlatforms();
             updateCollision();

             if(plats.size() % 3 == 0 && plats.size() < 9){
                 platformGeneration();
             }
         }

         public void updateCollision(){

            //if the character touches the bottom of the screen stop the movement
            if(character.y + character.height > screenHeight){
                vy = 0;
                character.y = screenHeight - character.height;
            }
            // if the character touches the top they go back down
            else if(character.y <= 0){
                vy = 0;
                character.y = 0;
            }
            if (character.x +character.width > screenWidth){
                character.x = screenWidth - character.width;
                vx = 0;
            }
            else if(character.x <= 0){
                character.x = 0;
                vx = 0;
            }

            //Check to see if the character is Colliding with a platform

             for (Sprite p: plats){
                Collision.CollisionData collisionData = Collision.blockTestRectangle(character, p);

                if(collisionData != null){
                    character.x += collisionData.offsetX;
                    character.y += collisionData.offsetY;
                    break;
                }
             }

         }

         private void drawCanvas(){
             if (holder.getSurface().isValid()) {
                 synchronized (holder) {
                     try {
                         canvas = holder.lockCanvas();
                         canvas.drawColor(Color.argb(255, 255, 0, 0));

                         background.draw(canvas);
                         for (Sprite p : plats) {
                             p.draw(canvas);
                         }

                         // items.draw(canvas);
                         buttonLeft.draw(canvas);
                         buttonRight.draw(canvas);
                         character.draw(canvas);
                     }
                     finally {
                         if (canvas != null) {
                             holder.unlockCanvasAndPost(canvas);
                         }
                     }
                 }
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
