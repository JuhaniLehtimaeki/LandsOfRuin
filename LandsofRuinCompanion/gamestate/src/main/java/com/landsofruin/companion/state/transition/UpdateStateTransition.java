package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * Transition send from the server to a connected client to get the current
 * game state.
 */
@ObjectiveCName("UpdateStateTransition")
public class UpdateStateTransition implements Transition {
    public GameState currentGameState;

    public UpdateStateTransition(GameState gameState) {
        currentGameState = gameState;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        gameState.updateFrom(currentGameState);

        EventsHelper.getInstance().clientConnected();
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        throw new AssertionError("This should never be triggered on a server");
    }
}
