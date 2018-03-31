package com.landsofruin.companion.eventbus;

import com.landsofruin.companion.state.CharacterState;

public class BlinkCharactersEvent {
    private CharacterState character;

    public BlinkCharactersEvent(CharacterState character) {
        this.character = character;
    }

    public CharacterState getCharacter() {
        return character;
    }
}
