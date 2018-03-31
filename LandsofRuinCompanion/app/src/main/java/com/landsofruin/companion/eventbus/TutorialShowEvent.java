package com.landsofruin.companion.eventbus;

public class TutorialShowEvent {


    private float x;
    private float y;
    private int width;
    private int height;

    public TutorialShowEvent(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
