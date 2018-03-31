package com.landsofruin.companion.cards;

import android.content.Context;
import android.util.AttributeSet;

import com.landsofruin.companion.eventbus.ShowHideEnemyCardsEvent;
import com.squareup.otto.Subscribe;

public class EnemyCardOverlayLayout extends CardOverlayFragmentsLayout {


    public EnemyCardOverlayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setIsEnemyLayout();
    }


    @Subscribe
    public void onShowEnemyCardsEvent(ShowHideEnemyCardsEvent event) {

        super.showHideOverScreenAndSpreadCards(event.getX(), event.getY());

    }


}
