package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * {@link com.landsofruin.companion.state.transition.Transition} to end the game
 */
@ObjectiveCName("EndGamePreparationSetPlayerReadyTransition")
public class EndGamePreparationSetPlayerReadyTransition implements Transition {

    private String playerId;
    private boolean ready;

    public EndGamePreparationSetPlayerReadyTransition(String playerId, boolean ready) {
        this.playerId = playerId;
        this.ready = ready;
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
        gameState.findPlayerByIdentifier(this.playerId).getPrepareEndGameState().setPlayerReady(this.ready);

        performEndGameIfAllPlayersReady(gameState);
    }

    private void performEndGameIfAllPlayersReady(GameState game) {


        for (PlayerState player : game.getPlayers()) {
            if (!player.getPrepareEndGameState().isPlayerReady()) {
                return;
            }
        }

        PhaseState phase = game.getPhase();
        phase.setPrimaryPhase(PrimaryPhase.GAME_END);
        phase.setSecondaryPhase(SecondaryPhase.NONE);
    }

}
