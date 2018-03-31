package com.landsofruin.companion.state.transition;

import android.util.Log;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.List;

/**
 * A player connects to the game (Game Setup Phase).
 */
@ObjectiveCName("ConnectTransition")
public class ConnectTransition implements Transition {
    private static final String TAG = "LoR/ConnectTransition";

    private PlayerState playerState;

    public ConnectTransition(PlayerState playerState) {
        this.playerState = playerState;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        gameState.setPhaseChangeUndoEnabled(false);
        origin.setPlayerIdentifier(playerState.getIdentifier());

        trigger(gameState);

        UpdateStateTransition updateTransition = new UpdateStateTransition(gameState);
        origin.write(updateTransition);


    }

    public void trigger(GameState gameState) {
        List<PlayerState> players = gameState.getPlayers();

        if (players.contains(playerState)) {
            // Player is already part of the game. This is a reconnect.
            PlayerState existingState = players.get(players.indexOf(playerState));
            existingState.setConnected(true);

            Log.d(TAG, "Player reconnected: " + existingState.getIdentifier());

        } else {
            Log.d(TAG, "Player connected: " + playerState.getIdentifier());

            playerState.setConnected(true);
            players.add(playerState);
        }
    }
}
