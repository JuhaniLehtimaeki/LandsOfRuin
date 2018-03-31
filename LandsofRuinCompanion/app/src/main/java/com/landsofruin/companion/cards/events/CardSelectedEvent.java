package com.landsofruin.companion.cards.events;

import com.landsofruin.companion.cards.CharacterCardFragment;

public class CardSelectedEvent {

    private CharacterCardFragment selected;

    public CardSelectedEvent() {
    }

    public CardSelectedEvent(CharacterCardFragment selected) {
        this.selected = selected;
    }

    public CharacterCardFragment getSelectedCharacterCardFragment() {
        return selected;
    }
}
