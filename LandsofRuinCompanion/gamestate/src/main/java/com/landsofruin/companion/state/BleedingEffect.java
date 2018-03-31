package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.gameruleobjects.characterdata.CharacterStatModifier;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.ArrayList;
import java.util.LinkedList;

@ObjectiveCName("BleedingEffect")
public class BleedingEffect extends CharacterEffect {
    private boolean stillBleeding = true;
    private int currentTurn;

    public BleedingEffect(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Override
    public boolean isActive(int turn) {
        return stillBleeding;
    }

    @Override
    public int getOffensiveModifier() {
        return 0;
    }

    @Override
    public int getDefensiveModifierZombie() {
        return 0;
    }

    @Override
    public int getDefensiveModifierCC() {
        return 0;
    }

    @Override
    public int getDefensiveModifierShooting() {
        return 0;
    }

    @Override
    public String getName() {
        return "bleeding";
    }

    @Override
    public float getMovementModifierPercentage() {
        return 0;
    }

    @Override
    public int getMovementModifier() {
        return 0;
    }

    @Override
    public int getId() {
        return CharacterEffect.ID_BLEEDING;
    }

    @Override
    public String getDescription() {
        return "bleeding";
    }

    @Override
    public String advanceOneTurn(GameState gameState, CharacterState character, int dieRoll) {
        if (dieRoll == 1) {
            CharacterStatModifier modifier = new CharacterStatModifier(
                    "bleeding", -1, -1, Integer.MAX_VALUE, new ArrayList<Integer>());
            character.addModifier(modifier);

            return "Bleeding continues, -1/-1";
        } else {
            stillBleeding = false;
            return "Bleeding stops";
        }
    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        return null;
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("Bleeding");
        return ret;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public float getAPModifierPercentage() {
        return 0;
    }

    @Override
    public int getAPModifier() {
        return 0;
    }

    @Override
    public boolean canStack() {
        return false;
    }

    @Override
    public int getSetTurn() {
        return currentTurn;
    }

    @Override
    public float getSuppressionResistance() {
        return 0;
    }

    @Override
    public float getOffensiveModifierResistance() {
        return 0;
    }
}
