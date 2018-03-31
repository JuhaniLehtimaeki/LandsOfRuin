package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.gameruleobjects.characterdata.CharacterStatModifier;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.ArrayList;
import java.util.LinkedList;

@ObjectiveCName("OnFireEffect")
public class OnFireEffect extends CharacterEffect {
    private boolean stillUnconscious = true;
    private int currentTurn;

    public OnFireEffect(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Override
    public boolean isActive(int turn) {
        return stillUnconscious;
    }

    @Override
    public int getOffensiveModifier() {
        return -5;
    }

    @Override
    public int getDefensiveModifierZombie() {
        return +5;
    }

    @Override
    public int getDefensiveModifierCC() {
        return -3;
    }

    @Override
    public int getDefensiveModifierShooting() {
        return -3;
    }

    @Override
    public String getName() {
        return "on fire";
    }

    @Override
    public float getMovementModifierPercentage() {
        return -0.5f;
    }

    @Override
    public int getMovementModifier() {
        return 0;
    }

    @Override
    public int getId() {
        return CharacterEffect.ID_ON_FIRE;
    }


    @Override
    public String getDescription() {
        return "on fire";
    }


    @Override
    public String advanceOneTurn(GameState gameState, CharacterState character, int dieRoll) {
        if (dieRoll <= 3) {
            CharacterStatModifier modifier = new CharacterStatModifier(
                    "burned", -1, -1, Integer.MAX_VALUE, new ArrayList<Integer>());
            character.addModifier(modifier);
            return "Still on fire: -1/-1";
        } else {
            stillUnconscious = false;
            return "Fire is out";
        }
    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        return null;
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("On fire");
        return ret;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public float getAPModifierPercentage() {
        return -0.5f;
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
