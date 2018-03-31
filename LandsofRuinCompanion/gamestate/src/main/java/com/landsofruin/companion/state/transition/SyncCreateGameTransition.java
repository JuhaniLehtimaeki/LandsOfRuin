package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("SyncCreateGameTransition")
public class SyncCreateGameTransition implements Transition {
    private String serverIdentifier;
    private int nextPhase;

    public SyncCreateGameTransition(String serverIdentifier) {
        this.serverIdentifier = serverIdentifier;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        if (gameState.needsSyncTokens()) {
            nextPhase = SecondaryPhase.SYNC_REQUEST_TOKENS;
        } else {
            nextPhase = SecondaryPhase.SYNC_AUTHORIZE_GAME;
        }

        trigger(gameState);
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    private void trigger(GameState gameState) {
        gameState.setServerIdentifier(serverIdentifier);
        gameState.getPhase().setSecondaryPhase(nextPhase);
    }
}
