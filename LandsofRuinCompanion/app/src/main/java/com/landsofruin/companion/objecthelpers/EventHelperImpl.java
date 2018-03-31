package com.landsofruin.companion.objecthelpers;

import com.landsofruin.companion.CompanionApplication;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.NewGameLogItemEvent;
import com.landsofruin.companion.eventbus.OpponentIsWaitingForYouEvent;
import com.landsofruin.companion.eventbus.QueueAnimationsEvent;
import com.landsofruin.companion.net.event.ClientConnectedEvent;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.proxyobjecthelper.EventHelperInterface;

/**
 * Created by juhani on 02/07/15.
 */
public class EventHelperImpl implements EventHelperInterface {
    @Override
    public void myTurnStarted() {
        CompanionApplication.getInstance().getSoundEffects()
                .playYourTurnSound();
    }

    @Override
    public void opponentIsWaitingForYou() {
        CompanionApplication.getInstance().getSoundEffects()
                .playOpponentIsWaitingForYou();
        BusProvider.postOnMainThread(new OpponentIsWaitingForYouEvent());
    }

    @Override
    public void clientConnected() {
        BusProvider.postOnMainThread(new ClientConnectedEvent());
    }


    @Override
    public void addGameLogItemToLog(GameLogItem logItem) {
        BusProvider.postOnMainThread(new NewGameLogItemEvent(logItem));

    }

    @Override
    public void queueAnimations(MultipleAnimationsHolder animations) {
        BusProvider.postOnMainThread(new QueueAnimationsEvent(animations));
    }
}
