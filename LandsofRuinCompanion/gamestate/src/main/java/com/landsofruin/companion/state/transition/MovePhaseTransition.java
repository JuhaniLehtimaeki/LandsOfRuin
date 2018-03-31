package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;
import com.landsofruin.companion.state.tutorial.TutorialConstants;

/**
 * Advance to {@link PrimaryPhase#ACTION}.
 */
@ObjectiveCName("MovePhaseTransition")
public class MovePhaseTransition implements Transition {

    public MovePhaseTransition() {

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

        BattleReportEvent event = new BattleReportEvent();
        event.setEventTitle("Sync Phase");
        event.setEventType(BattleReportEvent.TYPE_PHASE_CHANGE);
        BattleReportLogger.getInstance().logEvent(event, gameState);
    }


    private void trigger(GameState gameState) {
        gameState.setPhaseChangeUndoEnabled(true);
        advancePhase(gameState);

    }


    private void advancePhase(GameState gameState) {
        PhaseState phase = gameState.getPhase();
        phase.setPrimaryPhase(PrimaryPhase.MOVE);
        phase.setSecondaryPhase(SecondaryPhase.NONE);
    }

}
