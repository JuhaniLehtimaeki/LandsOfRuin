package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * {@link com.landsofruin.companion.state.transition.Transition} to end the game
 */
@ObjectiveCName("DenyPhaseUndoConfirmationTransition")
public class DenyPhaseUndoConfirmationTransition implements Transition {
    @Override
    public boolean isRelevantForServerSync() {
        return true;
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
        gameState.setPhaseChangeUndoInEffect(false);
        gameState.setPhaseChangeUndoRequested(false);
        gameState.setPlayerRequestingPhaseChangeUndo(null);
        gameState.resetPlayerApprovedPhaseChangeUndo();
    }

}
