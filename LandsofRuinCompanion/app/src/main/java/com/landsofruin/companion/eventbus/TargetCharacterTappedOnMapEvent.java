package com.landsofruin.companion.eventbus;

import com.landsofruin.companion.state.CharacterState;

public class TargetCharacterTappedOnMapEvent {
    private CharacterState character;

    public TargetCharacterTappedOnMapEvent(CharacterState character) {
        this.character = character;
    }

    public CharacterState getCharacter() {
        return character;
    }
}
