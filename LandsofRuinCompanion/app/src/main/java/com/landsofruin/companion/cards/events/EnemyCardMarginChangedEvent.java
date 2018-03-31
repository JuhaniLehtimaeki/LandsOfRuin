package com.landsofruin.companion.cards.events;

/**
 * Created by juhani on 10/12/13.
 */
public class EnemyCardMarginChangedEvent {


    private int speed;

    public EnemyCardMarginChangedEvent(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }
}
