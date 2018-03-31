package com.landsofruin.companion.state.transition;

import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

public class ReadySetupScenarioSelectTransition implements Transition {


    public ReadySetupScenarioSelectTransition() {
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


        PhaseState phase = gameState.getPhase();
        phase.setSecondaryPhase(SecondaryPhase.SETUP_SCENARIO_ROLES);
        for (PlayerState player : gameState.getPlayers()) {
            player.setPreGameReady(false);
        }

    }
}
