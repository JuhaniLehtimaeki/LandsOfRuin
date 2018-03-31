package com.landsofruin.companion.eventbus;

public class ReplaceTutorialEvent {
    private int scenarioId;


    public ReplaceTutorialEvent(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    public int getTutorialPosition() {
        return scenarioId;
    }
}
