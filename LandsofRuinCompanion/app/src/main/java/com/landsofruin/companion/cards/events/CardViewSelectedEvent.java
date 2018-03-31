package com.landsofruin.companion.cards.events;

import com.landsofruin.companion.cards.CharacterCard;

public class CardViewSelectedEvent {

    private CharacterCard selected;

    public CardViewSelectedEvent() {
    }

    public CardViewSelectedEvent(CharacterCard selected) {
        this.selected = selected;
    }

    public CharacterCard getSelectedCharacterCardFragment() {
        return selected;
    }
}
