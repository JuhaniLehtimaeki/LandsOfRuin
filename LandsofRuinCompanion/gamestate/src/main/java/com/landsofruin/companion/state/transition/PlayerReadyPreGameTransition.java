package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;
import com.landsofruin.companion.state.tutorial.TutorialConstants;

@ObjectiveCName("PlayerReadyPreGameTransition")
public class PlayerReadyPreGameTransition implements Transition {
    private String playerIdentifier;
    private boolean isReady;

    private boolean phaseChanged = false;

    public PlayerReadyPreGameTransition(String playerIdentifier, boolean isReady) {
        this.playerIdentifier = playerIdentifier;
        this.isReady = isReady;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
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
                if (player.isMe() && !isServer) {
                    EventsHelper.getInstance().opponentIsWaitingForYou();
                }
                break;
            }
        }

        if (everyoneReady) {
            PhaseState phase = gameState.getPhase();
            phase.setPrimaryPhase(PrimaryPhase.ACTION);
            phase.setSecondaryPhase(SecondaryPhase.WAITING_FOR_ACTION);
            phaseChanged = true;

            if (isServer) {
                BattleReportEvent event = new BattleReportEvent();
                event.setEventTitle("Action Phase");
                event.setEventType(BattleReportEvent.TYPE_PHASE_CHANGE);
                BattleReportLogger.getInstance().logEvent(event, gameState);
            }
        }
    }
}
