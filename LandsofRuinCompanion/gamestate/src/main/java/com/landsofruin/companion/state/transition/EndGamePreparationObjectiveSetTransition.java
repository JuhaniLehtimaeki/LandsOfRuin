package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * {@link com.landsofruin.companion.state.transition.Transition} to end the game
 */
@ObjectiveCName("EndGamePreparationObjectiveSetTransition")
public class EndGamePreparationObjectiveSetTransition implements Transition {

    private String playerId;
    private boolean set;
    private int objectiveId;

    public EndGamePreparationObjectiveSetTransition(String playerId, int objectiveId, boolean set) {
        this.playerId = playerId;
        this.set = set;
        this.objectiveId = objectiveId;
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
        for (PlayerState player : gameState.getPlayers()) {

            if (player.getIdentifier().equals(this.playerId)) {
                continue;
            }

            player.getPrepareEndGameState().setPlayerReady(false);
        }


        gameState.findPlayerByIdentifier(this.playerId).getPrepareEndGameState().setScenarioGoalsSet(this.objectiveId, this.set);
    }

}
