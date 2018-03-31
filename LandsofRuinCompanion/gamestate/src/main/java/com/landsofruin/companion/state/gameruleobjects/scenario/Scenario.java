package com.landsofruin.companion.state.gameruleobjects.scenario;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhani on 8/15/13.
 */
@ObjectiveCName("Scenario")
public class Scenario {

    private int id;
    private String name;
    private String description;
    private List<Integer> playerRoles = new ArrayList<>();
    private int initialThreatLevel;
    private int areaDangerLevel;
    private String scenarioImageUri = null;

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

    public void addPlayerRole(Integer role) {
        this.playerRoles.add(role);
    }

    public List<Integer> getPlayerRoles() {
        return playerRoles;
    }

    @Exclude
    public int getMinPlayerCount() {
        int ret = 0;
        for (int roleId : playerRoles) {
            PlayerRole role = LookupHelper.getInstance().getPlayerRoleFor(roleId);
            ret += role.getMinCount();
        }
        return ret;
    }

    @Exclude
    public int getMaxPlayerCount() {
        int ret = 0;
        for (int roleId : playerRoles) {
            PlayerRole role = LookupHelper.getInstance().getPlayerRoleFor(roleId);
            if (role.getMaxCount() == 0) {
                return -1;
            }

            ret += role.getMaxCount();
        }
        return ret;
    }

    public int getInitialThreatLevel() {
        return initialThreatLevel;
    }

    public void setInitialThreatLevel(int initialThreatLevel) {
        this.initialThreatLevel = initialThreatLevel;
    }

    public void setScenarioImageUri(String scenarioImageUri) {
        this.scenarioImageUri = scenarioImageUri;
    }

    public String getScenarioImageUri() {
        return scenarioImageUri;
    }

    public void setAreaDangerLevel(int areaDangerLevel) {
        this.areaDangerLevel = areaDangerLevel;
    }

    public int getAreaDangerLevel() {
        return areaDangerLevel;
    }
}
