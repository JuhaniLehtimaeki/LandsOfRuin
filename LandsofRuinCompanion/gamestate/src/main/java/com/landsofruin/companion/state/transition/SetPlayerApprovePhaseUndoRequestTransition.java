package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * {@link com.landsofruin.companion.state.transition.Transition} to end the game
 */
@ObjectiveCName("SetPlayerApprovePhaseUndoRequestTransition")
public class SetPlayerApprovePhaseUndoRequestTransition implements Transition {

    private String playerId;


    public SetPlayerApprovePhaseUndoRequestTransition(String playerId) {
        this.playerId = playerId;
    }


    @Override
    public boolean isRelevantForServerSync() {
        return true;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState, false);
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState, true);
    }

    private void trigger(GameState gameState, boolean isServer) {
        gameState.setPlayerApprovedPhaseChangeUndo(this.playerId, true);


        // check if all players approved
        for (PlayerState player : gameState.getPlayers()) {
            if (!gameState.getPlayerApprovedPhaseChangeUndo().contains(player.getIdentifier()) && !player.getIdentifier().equals(gameState.getPlayerRequestingPhaseChangeUndo())) {
                return;
            }
        }

        // if we're here everyone is ready

        gameState.setPhaseChangeUndoInEffect(true);
        gameState.setPhaseChangeUndoRequested(false);
        gameState.setPlayerRequestingPhaseChangeUndo(null);
        gameState.resetPlayerApprovedPhaseChangeUndo();

        gameState.getPhase().setPrimaryPhase(PrimaryPhase.getPreviousPhase(gameState.getPhase().getPrimaryPhase()));


        if (!isServer ) {
            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Game phase moved back", "The previous game phase is now active again." , IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON));
        }

    }

}
