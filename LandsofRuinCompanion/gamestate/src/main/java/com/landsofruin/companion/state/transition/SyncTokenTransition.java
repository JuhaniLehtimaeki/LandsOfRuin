package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("SyncTokenTransition")
public class SyncTokenTransition implements Transition {
    private String playerIdentifier;
    private String syncToken;

    public SyncTokenTransition(String playerIdentifier, String syncToken) {
        this.playerIdentifier = playerIdentifier;
        this.syncToken = syncToken;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    private void trigger(GameState gameState) {
        PlayerState player = gameState.findPlayerByIdentifier(playerIdentifier);
        player.setSyncToken(syncToken);

        if (!gameState.needsSyncTokens()) {
            gameState.getPhase().setSecondaryPhase(SecondaryPhase.SYNC_AUTHORIZE_GAME);
        }
    }
}
