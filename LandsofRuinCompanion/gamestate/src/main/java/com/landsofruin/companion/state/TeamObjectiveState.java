package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("TeamObjectiveState")
public class TeamObjectiveState {
    private String title;
    private String description;

    /**
     * required empty constructor for firebase. don't use in code!
     */
    public TeamObjectiveState() {

    }

    public TeamObjectiveState(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
