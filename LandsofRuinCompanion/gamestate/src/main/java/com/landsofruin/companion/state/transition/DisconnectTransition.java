package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.Iterator;

@ObjectiveCName("DisconnectTransition")
public class DisconnectTransition implements Transition {
    private String playerIdentifier;

    public DisconnectTransition(String playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    private void trigger(GameState gameState) {
        Iterator<PlayerState> iterator = gameState.getPlayers().iterator();

        while (iterator.hasNext()) {
            PlayerState player = iterator.next();

            if (player.getIdentifier().equals(playerIdentifier)) {
                player.setConnected(false);
            }
        }
    }
}
