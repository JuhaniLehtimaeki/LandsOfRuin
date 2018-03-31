package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("ChangeScenarioTransition")
public class ChangeScenarioTransition implements Transition {
    private int scenarioId;

    public ChangeScenarioTransition(int scenarioId) {
        this.scenarioId = scenarioId;
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

        Scenario scenario = LookupHelper.getInstance().getScenarioFor(this.scenarioId);
        if (scenario == null) {
            return;
        }

        gameState.setScenario(this.scenarioId);
        gameState.setTitle(scenario.getName());

        gameState.getWorld().setBaseThreatLevel(scenario.getInitialThreatLevel());
        gameState.getWorld().setAreaDangerLevel(scenario.getAreaDangerLevel());

        for (PlayerState playerState : gameState.getPlayers()) {
            playerState.setScenarioPlayerRole(-1);
        }
    }
}
