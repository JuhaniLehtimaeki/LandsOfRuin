package com.landsofruin.companion.eventbus;

import com.landsofruin.companion.state.CharacterState;

public class CharacterSelectedEvent {
    private CharacterState character;

    public CharacterSelectedEvent(CharacterState character) {
        this.character = character;
    }

    public CharacterSelectedEvent() {

    }

    public CharacterState getCharacter() {
        return character;
    }
}
