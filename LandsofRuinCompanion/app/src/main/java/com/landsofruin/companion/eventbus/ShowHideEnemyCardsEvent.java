package com.landsofruin.companion.eventbus;

public class ShowHideEnemyCardsEvent {
    private int x;
    private int y;

    public ShowHideEnemyCardsEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
