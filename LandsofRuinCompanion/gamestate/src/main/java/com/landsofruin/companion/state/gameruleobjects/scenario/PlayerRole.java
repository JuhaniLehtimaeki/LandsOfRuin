package com.landsofruin.companion.state.gameruleobjects.scenario;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

/**
 * Created by juhani on 8/15/13.
 */
@ObjectiveCName("PlayerRole")
public class PlayerRole {

    public static final int MAX_COUNT_UNLIMITED = 0;
    private int id;
    private String name;
    private String description;
    private int maxCount;
    private int minCount;
    private int firstTurnPriority;
    private boolean deployEdge;
    private boolean deployTableSection;
    private boolean allowInfiltrators;
    private boolean deployPreGame;
    private List<Integer> scenarioObjectives;
    private int maxGearValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public int getFirstTurnPriority() {
        return firstTurnPriority;
    }

    public void setFirstTurnPriority(int firstTurnPriority) {
        this.firstTurnPriority = firstTurnPriority;
    }

    public boolean isDeployEdge() {
        return deployEdge;
    }

    public void setDeployEdge(boolean deployEdge) {
        this.deployEdge = deployEdge;
    }

    public boolean isDeployTableSection() {
        return deployTableSection;
    }

    public void setDeployTableSection(boolean deployTableSection) {
        this.deployTableSection = deployTableSection;
    }

    public boolean isAllowInfiltrators() {
        return allowInfiltrators;
    }

    public void setAllowInfiltrators(boolean allowInfiltrators) {
        this.allowInfiltrators = allowInfiltrators;
    }

    public boolean isDeployPreGame() {
        return deployPreGame;
    }

    public void setDeployPreGame(boolean deployPreGame) {
        this.deployPreGame = deployPreGame;
    }

    public List<Integer> getScenarioObjectives() {
        return scenarioObjectives;
    }

    public void setScenarioObjectives(List<Integer> scenarionObjectives) {
        this.scenarioObjectives = scenarionObjectives;
    }

    public int getMaxGearValue() {
        return maxGearValue;
    }

    public void setMaxGearValue(int maxGearValue) {
        this.maxGearValue = maxGearValue;
    }
}
