package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("PlayerReadyTransition")
public class PlayerReadyTransition implements Transition {
    private String playerIdentifier;
    private boolean isReady;

    public PlayerReadyTransition(String playerIdentifier, boolean isReady) {
        this.playerIdentifier = playerIdentifier;
        this.isReady = isReady;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    private void trigger(GameState gameState) {
        gameState.findPlayerByIdentifier(playerIdentifier).setReady(isReady);




    }
}
