package com.landsofruin.gametracker.scenarios;

import com.landsofruin.companion.state.gameruleobjects.scenario.PlayerRole;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.gameruleobjects.scenario.ScenarioObjective;

import java.util.HashMap;
import java.util.LinkedList;


public class ScenariosManager {

    private static ScenariosManager instance = new ScenariosManager();
    private LinkedList<Scenario> scenarios = new LinkedList<>();
    private LinkedList<PlayerRole> playerRoles = new LinkedList<>();
    private LinkedList<ScenarioObjective> scenarioObjectives = new LinkedList<>();
    private HashMap<Integer, Scenario> scenarioLookupCache = new HashMap<>();
    private HashMap<Integer, PlayerRole> playerRoleLookupCache = new HashMap<>();


    private ScenariosManager() {
    }

    public static ScenariosManager getInstance() {
        return instance;
    }


    public LinkedList<Scenario> getScenarios() {
        return scenarios;
    }


    public Scenario getScenarioByID(int id) {
        Scenario ret = scenarioLookupCache.get(id);
        if (ret == null) {

            for (Scenario scenario : scenarios) {
                if (id == scenario.getId()) {
                    ret = scenario;
                    scenarioLookupCache.put(id, scenario);
                }
            }
        }
        return ret;
    }


    public ScenarioObjective getScenarioObjectiveByID(int id) {
        for (ScenarioObjective scenarioObjective : scenarioObjectives) {
            if (id == scenarioObjective.getId()) {
                return scenarioObjective;
            }
        }
        return null;
    }


    public LinkedList<PlayerRole> getPlayerRoles() {
        return playerRoles;
    }


    public PlayerRole getPlayerRoleByID(int id) {
        PlayerRole ret = playerRoleLookupCache.get(id);

        if (ret == null) {
            for (PlayerRole playerRole : playerRoles) {
                if (id == playerRole.getId()) {
                    ret = playerRole;
                    playerRoleLookupCache.put(id, ret);
                }
            }
        }
        return ret;
    }


    public void clearData() {
        this.scenarioLookupCache.clear();
        this.scenarioObjectives.clear();
        this.scenarios.clear();
        this.playerRoleLookupCache.clear();
        this.playerRoles.clear();
    }


    public void addScenarioData(Scenario scenario) {
        this.scenarios.add(scenario);
    }


    public void addPlayerRoleData(PlayerRole playerRole) {
        this.playerRoles.add(playerRole);
    }


    public void addObjectiveData(ScenarioObjective scenarioObjective) {
        this.scenarioObjectives.add(scenarioObjective);
    }


}
