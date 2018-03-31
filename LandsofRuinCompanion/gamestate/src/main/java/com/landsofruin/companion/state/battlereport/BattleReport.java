package com.landsofruin.companion.state.battlereport;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AttackLogItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by juhani on 19/01/16.
 */
@ObjectiveCName("BattleReport")
public class BattleReport {

    private String gameName;
    private String id;
    private int scenarioId;
    private boolean battleCompleted = false;

    private List<BattleReportPlayer> players = new ArrayList<>();
    private HashMap<String, BattleReportEvent> events = new HashMap<>();

    @Exclude
    private HashMap<String, AttackLogItem> characterActions = new HashMap<>();


    public BattleReport() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    public HashMap<String, BattleReportEvent> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String, BattleReportEvent> events) {
        this.events = events;
    }

    public boolean isBattleCompleted() {
        return battleCompleted;
    }

    public void setBattleCompleted(boolean battleCompleted) {
        this.battleCompleted = battleCompleted;
    }

    public List<BattleReportPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<BattleReportPlayer> players) {
        this.players = players;
    }

    public HashMap<String, AttackLogItem> getCharacterActions() {
        return characterActions;
    }

    public void setCharacterActions(HashMap<String, AttackLogItem> characterActions) {
        this.characterActions = characterActions;
    }
}
