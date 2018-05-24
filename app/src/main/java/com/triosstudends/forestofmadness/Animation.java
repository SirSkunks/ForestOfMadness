package com.triosstudends.forestofmadness;

public class Animation {
    String animationName;
    int startFrame;
    int frameCount;
    int fps;
    boolean looping;

    public Animation(String animationName, int startFrame, int frameCount, int fps, boolean looping) {
        this.animationName = animationName;
        this.startFrame = startFrame;
        this.frameCount = frameCount;
        this.fps = fps;
        this.looping = looping;
    }
}
