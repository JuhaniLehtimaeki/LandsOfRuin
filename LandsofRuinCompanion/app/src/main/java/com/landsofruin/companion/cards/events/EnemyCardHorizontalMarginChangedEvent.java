package com.landsofruin.companion.cards.events;

/**
 * Created by juhani on 10/12/13.
 */
public class EnemyCardHorizontalMarginChangedEvent  {



    private int speed;

    public EnemyCardHorizontalMarginChangedEvent(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }
}
