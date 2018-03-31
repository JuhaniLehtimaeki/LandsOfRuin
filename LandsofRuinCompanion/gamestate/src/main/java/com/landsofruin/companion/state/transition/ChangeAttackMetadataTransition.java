package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.NextAttackState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.HashMap;

@ObjectiveCName("ChangeAttackMetadataTransition")
public class ChangeAttackMetadataTransition implements Transition {
    private HashMap<String, HashMap<String, Boolean>> characterMetadata_String_String_Boolean;
    private HashMap<String, Boolean> metadata;
    private String performingPlayer;

    public ChangeAttackMetadataTransition(HashMap<String, HashMap<String, Boolean>> characterMetadata, HashMap<String, Boolean> metadata, String performingPlayer) {
        this.characterMetadata_String_String_Boolean = characterMetadata;
        this.metadata = metadata;
        this.performingPlayer = performingPlayer;
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
        NextAttackState attackState = gameState.getPhase().getNextAttackState();
        if (attackState != null) {
            attackState.setCharacterMetadata(characterMetadata_String_String_Boolean);
            attackState.setMetadata(metadata);
            attackState.unreadyAllPlayers();
        }
    }
}
