package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("PlayerReadySetupMapTransition")
public class PlayerReadySetupMapTransition implements Transition {
    private String playerIdentifier;
    private boolean isReady;

    public PlayerReadySetupMapTransition(String playerIdentifier, boolean isReady) {
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
        for (PlayerState player : gameState.getPlayers()) {
            if (player.getIdentifier().equals(playerIdentifier)) {
                player.setPreGameReady(isReady);
                break;
            }
        }

        boolean everyoneReady = true;

        for (PlayerState player : gameState.getPlayers()) {
            if (!player.isPreGameReady()) {
                everyoneReady = false;
                break;
            }
        }

        if (everyoneReady) {
            PhaseState phase = gameState.getPhase();
            phase.setSecondaryPhase(SecondaryPhase.SETUP_TEAM);
            for (PlayerState player : gameState.getPlayers()) {
                player.setPreGameReady(false);
            }
        }


    }
}
