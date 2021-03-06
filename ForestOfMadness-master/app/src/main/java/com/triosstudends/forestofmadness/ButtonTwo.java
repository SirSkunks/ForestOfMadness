package com.triosstudends.forestofmadness;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

class ButtonTwo {
    int x;
    int y;
    int width;
    int height;

    private final Bitmap bmp;
    private int scrX = 0;
    private int scrY = 0;

    public ButtonTwo(Bitmap bmp){
        this.bmp = bmp;

        width = bmp.getWidth();
        height = bmp.getHeight();
    }
    public void draw(Canvas canvas){
        Rect srcRect = new Rect(scrX, scrY, scrX + width, scrY + height);
        Rect dstRect = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, srcRect, dstRect, null);
    }
}
