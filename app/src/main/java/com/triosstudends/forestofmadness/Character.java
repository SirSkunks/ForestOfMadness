package com.triosstudends.forestofmadness;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.HashMap;

public class Character {

    int x = 0;
    int y = 0;
    int width;
    int height;

    // Sprite Sheet
    int sheet_rows = 6;
    int sheet_cols = 5;

    private final Bitmap bitmap;
    private int srcX = 0;
    private int srcY = 0;

    // Animations
    HashMap<String, Animation>animations;
    private Animation currentAnimation = null;
    private int currentFrame = 0;
    private long currentFrameTime = 0;
    private long frameTime = 0;
    private boolean playing = false;

    public Character (Bitmap bitmap){
        this.bitmap = bitmap;

        width = bitmap.getWidth() / sheet_cols;
        height = bitmap.getHeight() / sheet_rows;

        updateChar();

        animations = new HashMap<String, Animation>();
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
    }

    public void draw(Canvas canvas){
        Rect srcRect = new Rect(srcX, srcY, srcX + width, srcY + height );
        Rect dstRect = new Rect(x, y, x + width, y + height );

        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
    }

    public void addAnimation(String name, int startFrame, int frameCount, int fps, int cellWidth, int cellHeight, boolean looping){
        animations.put(name, new Animation(name, startFrame, frameCount, fps, cellWidth, cellHeight, looping));
    }

    public boolean setAnimation(String name){
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
