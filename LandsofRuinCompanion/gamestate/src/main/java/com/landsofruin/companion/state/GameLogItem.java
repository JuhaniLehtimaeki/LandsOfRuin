package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;

/**
 * Created by juhani on 12/08/15.
 */
@ObjectiveCName("GameLogItem")
public class GameLogItem {

    private String shortText;
    private String longText;
    private int iconResourceId;
    private CharacterState characterState;
    private CharacterState targetCharacterState;


    public GameLogItem(String shortText, String longText, int iconResourceId, CharacterState characterState, CharacterState targetCharacterState) {
        this.shortText = shortText;
        this.longText = longText;
        this.iconResourceId = iconResourceId;
        this.characterState = characterState;
        this.targetCharacterState = targetCharacterState;
    }

    public GameLogItem(String shortText, String longText, int iconResourceId, CharacterState characterState) {
        this.shortText = shortText;
        this.longText = longText;
        this.iconResourceId = iconResourceId;
        this.characterState = characterState;
    }

    public GameLogItem(String shortText, String longText, int iconResourceId) {
        this.shortText = shortText;
        this.longText = longText;
        this.iconResourceId = iconResourceId;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getLongText() {
        return longText;
    }

    public String getShortText() {
        return shortText;
    }

    public CharacterState getCharacterState() {
        return characterState;
    }

    public CharacterState getTargetCharacterState() {
        return targetCharacterState;
    }
}
