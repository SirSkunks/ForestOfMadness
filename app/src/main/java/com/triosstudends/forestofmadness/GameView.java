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
import android.media.MediaPlayer;
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

    MediaPlayer player;
    private SoundPool soundPool;
    private int jump;
    private int getHit;
    private int pickUp;
    boolean musicMuted = false;
    boolean effectsMuted = false;
    int pScore = 0;
    int pHealth = 2000;
    int currentHP = pHealth;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String dataName = "Data";
    int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = preferences.edit();
        musicMuted = Options.returnBool();
        effectsMuted = Options.returnBool2();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);





            jump = soundPool.load(this, R.raw.jump,1);
            getHit = soundPool.load(this, R.raw.gethit,1);
            pickUp = soundPool.load(this, R.raw.powerup,1);


        //soundPool.play(jump,1,1,1,0,1);
        characterView = new CharacterView(this);
        setContentView(characterView);
        paint = new Paint();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        soundPool.release();
        player.release();
        player = null;
    }

    @Override
    protected void onPause(){
        super.onPause();
        characterView.pause();
        soundPool.autoPause();
        if(player != null){
            player.release();
            player = null;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        characterView.resume();
        playMusic();
    }

    @Override
    public void onClick(View v) {
        //onClick(characterView);
    }
    public void playMusic(){
        if(!musicMuted){
            if(player == null){
                player = MediaPlayer.create(this,R.raw.levelonebgm);
            }
            player.start();
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
        boolean isJumping = false;
        boolean movingLeft = false;
        boolean movingRight = false;

        int vx = 0;
        int vy = 0;
        int gravity = 1;

        //Timer Elements
        long lastFrameTime;
        long deltaTime;
        int fps;

        ArrayList<Sprite> plats;
        ArrayList<Sprite> items;

        public CharacterView(Context context){
            super(context);

            plats = new ArrayList<>();
            holder = getHolder();
            items = new ArrayList<>();

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
            character.addAnimation("runRight", 0, 8, 7, true);
            character.addAnimation("runLeft", 15, 8, 7, true);
            character.addAnimation("idle", 8, 1, 1, true);
            character.addAnimation("jumpRight", 12, 1, 1, false);
            character.setAnimation("idle");

            character.x = 0 - character.width / 2;
            character.y = screenHeight / 2 - character.height / 2;

            Sprite start = new Sprite(world);
            start.updateDimens(4, 5);
            start.addAnimation("platform1", 0, 1, 1, false);
            start.setAnimation("platform1");
            start.x = 0;
            start.y = screenHeight /2 - 3;

            Sprite midsect = new Sprite(world);
            midsect.updateDimens(4, 5);
            midsect.addAnimation("platform2", 1, 1, 1, false);
            midsect.setAnimation("platform2");
            midsect.x = start.x + start.width;
            midsect.y = start.y;

            Sprite endsect = new Sprite(world);
            endsect.updateDimens(4, 5);
            endsect.addAnimation("platform3", 2, 1, 1, false);
            endsect.setAnimation("platform3");
            endsect.x = midsect.x + midsect.width;
            endsect.y = start.y;
            plats.add(start);
            plats.add(midsect);
            plats.add(endsect);

        }

        public void platformGeneration(){
            Random random = new Random();
            int generate = random.nextInt(100) + 1;
            int spawn = random.nextInt(100) + 1;

            // Bottom Row of platforms
            if (generate <= 50) {

                Sprite lead = new Sprite(world);
                lead.updateDimens(4, 5);
                lead.addAnimation("platform1", 0, 1, 1, false);
                lead.setAnimation("platform1");
                lead.x = screenWidth;
                lead.y = screenHeight - lead.height;

                Sprite middle = new Sprite(world);
                middle.updateDimens(4, 5);
                middle.addAnimation("platform2", 1, 1, 1, false);
                middle.setAnimation("platform2");
                middle.x = lead.x + lead.width;
                middle.y = lead.y;

                Sprite end = new Sprite(world);
                end.updateDimens(4, 5);
                end.addAnimation("platform3", 2, 1, 1, false);
                end.setAnimation("platform3");
                end.x = middle.x + middle.width;
                end.y = lead.y;
                plats.add(lead);
                plats.add(middle);
                plats.add(end);

                if(items.size() <= 1) {

                    if (spawn <= 15) {
                        Log.d("*****", "coffee spawn");
                        Sprite coffee = new Sprite(pickUps);
                        coffee.updateDimens(1, 3);
                        coffee.addAnimation("coffee", 0, 1, 1, false);
                        coffee.setAnimation("coffee");
                        coffee.x = middle.x;
                        coffee.y = middle.y - coffee.getHeight();

                        items.add(coffee);

                    } else if (spawn <= 45) {
                        Log.d("*****", "pill spawn");
                        Sprite pills = new Sprite(pickUps);
                        pills.updateDimens(1, 3);
                        pills.addAnimation("pills", 1, 1, 1, false);
                        pills.setAnimation("pills");
                        pills.x = middle.x;
                        pills.y = middle.y - pills.height / 2;
                        items.add(pills);
                    }
                }
            }

            // Middle row of platforms
            else {

                Sprite lead = new Sprite(world);
                lead.updateDimens(4, 5);
                lead.addAnimation("platform1", 0, 1, 1, false);
                lead.setAnimation("platform1");
                lead.x = screenWidth;
                lead.y = screenHeight / 2 - 3;

                Sprite middle = new Sprite(world);
                middle.updateDimens(4, 5);
                middle.addAnimation("platform2", 1, 1, 1, false);
                middle.setAnimation("platform2");
                middle.x = lead.x + lead.width;
                middle.y = lead.y;

                Sprite end = new Sprite(world);
                end.updateDimens(4, 5);
                end.addAnimation("platform3", 2, 1, 1, false);
                end.setAnimation("platform3");
                end.x = middle.x + middle.width;
                end.y = lead.y;

                plats.add(lead);
                plats.add(middle);
                plats.add(end);

                if (spawn <= 25) {
                    Log.d("*****", "coffee spawn");
                    Sprite coffee = new Sprite(pickUps);
                    coffee.updateDimens(1, 3);
                    coffee.addAnimation("coffee", 0, 1, 1, false);
                    coffee.setAnimation("coffee");
                    coffee.x = middle.x;
                    coffee.y = middle.y - coffee.height;
                    items.add(coffee);
                }
                else if (spawn <= 50){
                    Log.d("*****", "pill spawn");
                    Sprite pills = new Sprite(pickUps);
                    pills.updateDimens(1, 3);
                    pills.addAnimation("pills", 1, 1, 1, false);
                    pills.setAnimation("pills");
                    pills.x = middle.x;
                    pills.y = middle.y - pills.height;
                    items.add(pills);
                }
                else{
                    Log.d("*****", "nothing");

                }
            }
        }

        public void updatePlatforms(){
            Iterator<Sprite> i = plats.iterator();
            while (i.hasNext()){
                Sprite p = i.next();

                p.x -= 7;

                if(p.x + p.width < 0){
                    i.remove();
                }
            }
        }
        public void updateItems(){
            Iterator<Sprite> i = items.iterator();
            while(i.hasNext()){
                Sprite t = i.next();
                t.x -= 7;

                if(t.x + t.width <0){
                    i.remove();
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    // Check to see if the user touches within the left button
                    if (x < (buttonLeft.x + buttonLeft.width) && y >= buttonLeft.y) {
                        /*vx = -5;
                        character.setAnimation("runLeft");*/
                        movingLeft = true;
                    }
                    // Check to see if the user touches within the right button
                    else if(x >= buttonRight.x && y >= buttonRight.y){
                       /* vx = 5;
                        character.setAnimation("runRight");*/
                       movingRight = true;
                    }
                    // Check to see if the user touches anywhere but the buttons
                    else {
                        if (!isJumping) {
                            isJumping = true;

                            character.setAnimation("jumpRight");
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Stop character movement when
                    if(x < (buttonLeft.x + buttonLeft.width) && y >= buttonLeft.y) {

                        movingLeft = false;
                    }
                    else if(x >= buttonRight.x && y >= buttonRight.y){
                        movingRight = false;
                    }
                    else{
                        isJumping = false;
                    }
            }
            return true;
        }
//
        public void updateLogic(){

            if(movingRight){
                vx = 7;
                character.setAnimation("runRight");
            }
            else if(movingLeft){
                vx = -7;
                character.setAnimation("runLeft");
            }
            else{
                vx = 0;
                character.setAnimation("runRight");
            }

            if (isJumping) {
                vy = -28;
                if(!effectsMuted) {
                    soundPool.play(jump, 1, 1, 1, 0, 1);
                }
                isJumping = false;
            }

            vy += gravity;

            character.x += vx;
            character.y += vy;
            character.update(deltaTime);
            updatePlatforms();
            updateItems();
            updateCollision();

            pScore ++;
            currentHP --;
            if(plats.size() % 3 == 0 && plats.size() < 6){
                platformGeneration();
            }
        }

        public void updateCollision(){

            //if the character touches the bottom of the screen stop the movement
            if(character.y + character.height > screenHeight){
                vy = 0;
                character.y = screenHeight - character.height;
            }
            // if the character touches the top
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

                if (collisionData != null) {
                    character.x += collisionData.offsetX;
                    character.y += collisionData.offsetY;
                    if(collisionData.collisionSide == "bottom"){
                        vy = 0;
                    }
                    break;
                }
            }
            // Check to see if the character is Colliding with an item
            Iterator<Sprite> i = items.iterator();
            while(i.hasNext()){
                Sprite s = i.next();
                Collision.CollisionData collisionData = Collision.blockTestRectangle(character, s);

                if(collisionData != null){
                    Log.d("*****", "Item removed " + i);
                    if(s.currentAnimation.animationName == "coffee"){
                        if(currentHP > 0 || currentHP < pHealth){
                            currentHP += 100;
                            if(!effectsMuted) {
                                soundPool.play(pickUp, 1, 1, 0, 0, 1);
                            }
                            if(currentHP >= pHealth){
                                currentHP = pHealth;
                            }
                        }
                    }
                    if(s.currentAnimation.animationName == "pills"){
                        if (currentHP > 0 || currentHP < pHealth){
                            currentHP -= 50;
                            if(!effectsMuted) {
                                soundPool.play(getHit, 1, 1, 0, 0, 1);
                            }
                        }
                    }
                    i.remove();
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
                        for (Sprite i : items){
                            i.draw(canvas);
                        }
                        paint.setColor(Color.argb(255, 249, 129, 0));
                        paint.setTextSize(40);
                        canvas.drawText("Score: " + pScore, 10, 50, paint);
                        canvas.drawText("Health: " + currentHP + " / " + pHealth, 300, 50, paint);
                        buttonLeft.draw(canvas);
                        buttonRight.draw(canvas);
                        character.draw(canvas);
                    } finally {
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
                if (currentHP == 0){
                    running = false;
                }
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
