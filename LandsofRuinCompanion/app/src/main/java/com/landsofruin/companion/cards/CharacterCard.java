package com.landsofruin.companion.cards;

import android.view.View;

import com.landsofruin.companion.state.CharacterState;

/**
 * Created by juhani on 25/04/16.
 */
public interface CharacterCard {

    CharacterState getCharacter();

    View getView();
}
