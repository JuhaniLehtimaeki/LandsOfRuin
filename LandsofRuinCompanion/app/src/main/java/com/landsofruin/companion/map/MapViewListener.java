package com.landsofruin.companion.map;

import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.PointState;

public interface MapViewListener {
    void onCharacterMoved(CharacterState character);
    void onCharacterMovedOffTable(CharacterState character);
    void onThrowableCreated(int wargearId, PointState point, String characterId, int actionId);
}
