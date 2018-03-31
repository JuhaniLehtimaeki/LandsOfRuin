package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.LinkedList;

@ObjectiveCName("AttackStateSetTargetsTransition")
public class AttackStateSetTargetsTransition implements Transition {
    private LinkedList<String> characterIds_String = new LinkedList<>();

    public AttackStateSetTargetsTransition(LinkedList<String> characterIds) {

        this.characterIds_String = characterIds;
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
        gameState.getPhase().getNextAttackState().setTargets(this.characterIds_String);

    }
}
