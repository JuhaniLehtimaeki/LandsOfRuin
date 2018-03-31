package com.landsofruin.companion.cards.events;

import com.landsofruin.companion.cards.CharacterCardFragment;

/**
 * Created by juhani on 10/12/13.
 */
public class EnemyCardSelectedEvent extends CardSelectedEvent {

    public EnemyCardSelectedEvent(CharacterCardFragment selected) {
        super(selected);
    }
}
