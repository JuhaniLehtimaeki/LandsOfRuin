package com.landsofruin.companion.state.proxyobjecthelper;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.MultipleAnimationsHolder;

/**
 * Created by juhani on 30/06/15.
 */
@ObjectiveCName("EventsHelper")
public class EventsHelper {

    private static EventsHelper instance = new EventsHelper();

    private EventHelperInterface eventHelperInterface;

    private EventsHelper() {
    }

    public void setEventHelperInterface(EventHelperInterface eventHelperInterface) {
        this.eventHelperInterface = eventHelperInterface;
    }

    public static EventsHelper getInstance() {
        return instance;
    }


    public void myTurnStarted() {
        eventHelperInterface.myTurnStarted();

    }

    public void opponentIsWaitingForYou() {
        eventHelperInterface.opponentIsWaitingForYou();

    }


    public void clientConnected() {
        eventHelperInterface.clientConnected();

    }


    public void addGameLogItemToLog(GameLogItem logItem) {
        eventHelperInterface.addGameLogItemToLog(logItem);

    }

    public void queueAnimations(MultipleAnimationsHolder animations) {
        eventHelperInterface.queueAnimations(animations);

    }


}
