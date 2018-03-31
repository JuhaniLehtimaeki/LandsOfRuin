package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.NextAttackState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("StartAttackTransition")
public class StartAttackTransition implements Transition {

    private int actionId;
    private int wargearId;
    private String shooterCharacterId;

    public StartAttackTransition(String shooterCharacterId, int actionId, int wargearId) {
        this.shooterCharacterId = shooterCharacterId;
        this.actionId = actionId;
        this.wargearId = wargearId;

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
        gameState.setPhaseChangeUndoEnabled(false);
        gameState.getPhase().setNextAttackState(new NextAttackState(shooterCharacterId, actionId, wargearId));

        gameState.getPhase().setSecondaryPhase(SecondaryPhase.WAITING_FOR_ATTACK);
    }
}
