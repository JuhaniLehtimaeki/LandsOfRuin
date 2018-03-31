package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("ChangePlayerRoleTransition")
public class ChangePlayerRoleTransition implements Transition {
    private String playerIdentifier;
    private int playerRole;

    public ChangePlayerRoleTransition(String playerIdentifier, int playerRole) {
        this.playerIdentifier = playerIdentifier;
        this.playerRole = playerRole;
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
        gameState.findPlayerByIdentifier(playerIdentifier).setScenarioPlayerRole(playerRole);
    }
}
