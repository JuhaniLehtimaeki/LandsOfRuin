package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.HashMap;

/**
 * Created by juhani on 1/25/14.
 */
@ObjectiveCName("PrepareEndGameState")
public class PrepareEndGameState {

    private boolean playerReady = false;

    private HashMap<Integer, Boolean> scenarioGoalsSet_Integer_Boolean = new HashMap<>();

    public boolean isPlayerReady() {
        return playerReady;
    }

    public void setPlayerReady(boolean playerReady) {
        this.playerReady = playerReady;
    }


    public void setScenarioGoalsSet(int scenarioGoalsId, boolean ready) {
        this.scenarioGoalsSet_Integer_Boolean.put(scenarioGoalsId, ready);
    }

    public boolean isScenarioGoalSet(int scenarioGoalsId) {
        Boolean ret = this.scenarioGoalsSet_Integer_Boolean.get(scenarioGoalsId);
        if (ret == null) {
            return false;
        }

        return ret;

    }
}
