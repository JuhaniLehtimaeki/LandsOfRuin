package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("StartSyncSetupTransition")
public class StartSyncSetupTransition implements Transition {
    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    private void trigger(GameState gameState) {
        PhaseState phase = gameState.getPhase();

        phase.setPrimaryPhase(PrimaryPhase.SYNC_SETUP);
        phase.setSecondaryPhase(SecondaryPhase.SYNC_CREATE_GAME);
    }
}
