package com.landsofruin.companion.state.proxyobjecthelper;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.MultipleAnimationsHolder;

/**
 * Created by juhani on 02/07/15.
 */
@ObjectiveCName("EventHelperInterface")
public interface EventHelperInterface {

    void myTurnStarted();
    void opponentIsWaitingForYou();
    void clientConnected();
    void addGameLogItemToLog(GameLogItem logItem);
    void queueAnimations(MultipleAnimationsHolder animations);

}
