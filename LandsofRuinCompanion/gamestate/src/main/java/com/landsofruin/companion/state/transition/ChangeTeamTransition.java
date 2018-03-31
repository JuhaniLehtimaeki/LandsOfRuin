package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("ChangeTeamTransition")
public class ChangeTeamTransition implements Transition {
    private String playerIdentifier;
    private TeamState teamState;

    public ChangeTeamTransition(String playerIdentifier, TeamState teamState) {
        this.playerIdentifier = playerIdentifier;
        this.teamState = teamState;
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
        trigger(gameState);
    }

    private void trigger(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {
            if (player.getIdentifier().equals(playerIdentifier)) {
                player.setTeam(teamState);
                break;
            }
        }
    }
}
