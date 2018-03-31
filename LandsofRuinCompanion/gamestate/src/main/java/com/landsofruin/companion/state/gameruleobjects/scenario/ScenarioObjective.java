package com.landsofruin.companion.state.gameruleobjects.scenario;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 8/15/13.
 */
@ObjectiveCName("ScenarioObjective")
public class ScenarioObjective {

    private int id;
    private String name;
    private String description;
    private int points;

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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
