package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * {@link com.landsofruin.companion.state.transition.Transition} to end the game
 */
@ObjectiveCName("StartPhaseUndoRequestTransition")
public class StartPhaseUndoRequestTransition implements Transition {

    private String playerId;

    public StartPhaseUndoRequestTransition(String playerId) {
        this.playerId = playerId;
    }


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
        gameState.setPhaseChangeUndoRequested(true);
        gameState.setPlayerRequestingPhaseChangeUndo(this.playerId);
        gameState.resetPlayerApprovedPhaseChangeUndo();
    }

}
