package com.landsofruin.companion.eventbus;

public class ReplaceScenarioDetailsEvent {
    private int scenarioId;


    public ReplaceScenarioDetailsEvent(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    public int getScenarioId() {
        return scenarioId;
    }
}
