package com.triosstudends.forestofmadness;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.HashMap;

public class Sprite {

    int x = 0;
    int y = 0;
    int width;

    public int getWidth() {
        return (int)(width * scale);
    }

    public int getHeight() {
        return (int)(height * scale);
    }

    int height;

    // Sprite Sheet
    int sheet_rows = 6;
    int sheet_cols = 5;

    private final Bitmap bitmap;
    private int srcX = 0;
    private int srcY = 0;
    Rect hitBox = null;
    float hbScaleX;
    float hbScaleY;

    // Animations
    private HashMap<String, Animation> animations;
    Animation currentAnimation = null;
    private int currentFrame = 0;
    private long currentFrameTime = 0;
    private long frameTime = 0;
    private boolean playing = false;

    float scale = 1.0f;

    Sprite (Bitmap bitmap){
        this.bitmap = bitmap;

        width = bitmap.getWidth() / sheet_cols;
        height = bitmap.getHeight() / sheet_rows;

        updateChar();

        animations = new HashMap<>();

    }

    public void updateDimens(int sheet_rows, int sheet_cols) {
        this.sheet_cols = sheet_cols;
        this.sheet_rows = sheet_rows;

        width = bitmap.getWidth() / this.sheet_cols;
        height = bitmap.getHeight() / this.sheet_rows;
    }

    public void update(long deltaTime){
        if (playing){
            currentFrameTime += deltaTime;

            if (currentFrameTime > frameTime){
                currentFrameTime = 0;
                currentFrame++;

                if(currentFrame > currentAnimation.startFrame + currentAnimation.frameCount - 1){
                    currentFrame = currentAnimation.startFrame;

                    if(!currentAnimation.looping){
                        playing = false;
                    }
                }

                updateChar();
            }
        }
        updateHitBox();
    }

    public void draw(Canvas canvas){
        Rect srcRect = new Rect(srcX, srcY, srcX + width, srcY + height );
        Rect dstRect = new Rect(x, y, (int)(x + width * scale), (int)(y + height * scale));

        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
    }

    public void updateHitBox(){
        int left = x - (int)(width * hbScaleX / 2 - width / 2);
        int top = y - (int)(height * hbScaleY / 2 - height / 2);
        int right = left + ((int)(width * hbScaleX));
        int bottom = top + ((int)(height * hbScaleY));

        if(hitBox == null) {
            hitBox = new Rect(left, top, right, bottom);
        }
        else{
            hitBox.left = left;
            hitBox.right = right;
            hitBox.top = top;
            hitBox.bottom = bottom;
        }
    }

    public void setHitBox(float scaleX, float scaleY){
       hbScaleX = scaleX;
       hbScaleY = scaleY;
       updateHitBox();
    }

    public void addAnimation(String name, int startFrame, int frameCount, int fps, boolean looping){
        animations.put(name, new Animation(name, startFrame, frameCount, fps, looping));
    }

    public boolean setAnimation(String name){
        if (currentAnimation != null){
            if (currentAnimation.animationName == name) {
                return true;
            }
        }

        currentAnimation = animations.get(name);
        if(currentAnimation == null){
            currentFrame = 0;
            playing = false;
            updateChar();
            return false;
        }
        currentFrame = currentAnimation.startFrame;
        currentFrameTime = 0;
        frameTime = 1000/ currentAnimation.fps;

        updateChar();

        playing = true;
        return true;
    }

    private void updateChar(){
        srcX = currentFrame % sheet_cols * width;
        srcY = currentFrame / sheet_cols * height;
    }
}
