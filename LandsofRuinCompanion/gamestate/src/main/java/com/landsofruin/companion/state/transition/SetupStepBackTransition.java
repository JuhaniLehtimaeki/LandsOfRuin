package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("SetupStepBackTransition")
public class SetupStepBackTransition implements Transition {

    public SetupStepBackTransition() {

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

        if (gameState.getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_SCENARIO_SELECT) {
            gameState.getPhase().setSecondaryPhase(SecondaryPhase.SETUP_PLAYERS);
        } else if (gameState.getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_SCENARIO_ROLES) {
            gameState.getPhase().setSecondaryPhase(SecondaryPhase.SETUP_SCENARIO_SELECT);
        } else if (gameState.getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_MAP_SELECT) {
            gameState.getPhase().setSecondaryPhase(SecondaryPhase.SETUP_SCENARIO_ROLES);
        } else if (gameState.getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_TABLE) {
            gameState.getPhase().setSecondaryPhase(SecondaryPhase.SETUP_MAP_SELECT);
        } else if (gameState.getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_TEAM) {
            gameState.getPhase().setSecondaryPhase(SecondaryPhase.SETUP_TABLE);
        } else if (gameState.getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_OVERVIEW) {
            gameState.getPhase().setSecondaryPhase(SecondaryPhase.SETUP_TEAM);
        }
        for (PlayerState player : gameState.getPlayers()) {
            player.setPreGameReady(false);
        }
    }
}
