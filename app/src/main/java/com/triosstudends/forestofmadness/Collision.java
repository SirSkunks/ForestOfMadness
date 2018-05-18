package com.triosstudends.forestofmadness;

import android.graphics.Rect;

public class Collision {

    public static CollisionData blockTestRectangle(Sprite s1, Sprite s2){
        String collisionSide = "";

        Rect r1 = new Rect(s1.x, s1.y, s1.x + s1.width, s1.y + s1.height);
        Rect r2 = new Rect(s2.x, s2.y, s2.x + s2.width, s2.y + s2.height);
        int dx = r1.centerX() - r2.centerX();
        int dy = r1.centerY() - r2.centerY();

        int sumHalfWidth = r1.width() / 2 + r2.width() / 2;
        int sumHalfHeight = r1.height() / 2 + r2.height() / 2;

        if(sumHalfWidth > Math.abs(dx)){
            if(sumHalfHeight > Math.abs(dy)){
                // Determine overlap amount
                int ox = sumHalfWidth - Math.abs(dx);
                int oy = sumHalfHeight - Math.abs(dy);

                // Vertical Motion Overlap
                //Sprite1 is below Sprite2
                if(ox > oy){
                    ox = 0;
                    if (dy > 0){
                        collisionSide = "top";
                    }
                    // Sprite1 is above Sprite2
                    else{
                        collisionSide = "bottom";
                        oy = -oy;
                    }
                }
                // Horizontal overlap
                //Sprite1 is right of Sprite2
                else{
                    oy = 0;
                    if(dx > 0){
                        collisionSide = "left";
                    }
                    //Sprite1 is left of Sprite2
                    else{
                       collisionSide = "right";
                       ox = -ox;
                    }
                }
                return new CollisionData(ox, oy, collisionSide);
            }
        }
        return null;
    }

    static class   CollisionData{
        int offsetX;
        int offsetY;
        String collisionSide;

        CollisionData(int offsetX, int offsetY, String collisionSide){
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.collisionSide = collisionSide;
        }
    }
}
