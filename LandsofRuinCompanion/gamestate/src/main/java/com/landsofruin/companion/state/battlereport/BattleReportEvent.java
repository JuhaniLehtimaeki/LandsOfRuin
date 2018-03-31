package com.landsofruin.companion.state.battlereport;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhani on 19/01/16.
 */
@ObjectiveCName("BattleReportEvent")
public class BattleReportEvent {

    public static final int TYPE_PHASE_CHANGE = 0;
    public static final int TYPE_ASSIGN_ACTION = 1;
    public static final int TYPE_PERFORM_ACTION = 2;

    private String id;
    private String eventTitle;
    private int eventType;

    private int actionId;
    private List<String> sourceCharacters = new ArrayList<>();
    private List<String> targetCharacters = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public List<String> getSourceCharacters() {
        return sourceCharacters;
    }

    public List<String> getTargetCharacters() {
        return targetCharacters;
    }

    public void setSourceCharacters(List<String> sourceCharacters) {
        this.sourceCharacters = sourceCharacters;
    }

    public void setTargetCharacters(List<String> targetCharacters) {
        this.targetCharacters = targetCharacters;
    }

    public void addSourceCharacter(String characterId) {
        sourceCharacters.add(characterId);
    }

    public void addTargetCharacter(String characterId) {
        targetCharacters.add(characterId);
    }
}
