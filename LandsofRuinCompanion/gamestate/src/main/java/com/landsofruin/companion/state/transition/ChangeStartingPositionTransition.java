package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("ChangeStartingPositionTransition")
public class ChangeStartingPositionTransition implements Transition {
    private String playerIdentifier;
    private PlayerState.StartingPosition startingPosition;

    public ChangeStartingPositionTransition(String playerIdentifier, PlayerState.StartingPosition startingPosition) {
        this.playerIdentifier = playerIdentifier;
        this.startingPosition = startingPosition;
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
        gameState.findPlayerByIdentifier(playerIdentifier).setStartingPosition(startingPosition);
    }
}
