package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.LinkedList;

@ObjectiveCName("UnconsciousEffect")
public class UnconsciousEffect extends CharacterEffect {
    private boolean stillUnconscious = true;
    private int currentTurn;

    public UnconsciousEffect(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Override
    public boolean isActive(int turn) {
        return stillUnconscious;
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
        return "unconscious";
    }

    @Override
    public float getMovementModifierPercentage() {
        return -1;
    }

    @Override
    public int getMovementModifier() {
        return 0;
    }

    @Override
    public int getId() {
        return CharacterEffect.ID_UNCONSCIOUS;
    }

    @Override
    public String getDescription() {
        return "unconscious";
    }

    @Override
    public String advanceOneTurn(GameState gameState, CharacterState character, int dieRoll) {
        if (dieRoll == 1) {
            return "Still unconscious";
        } else {
            stillUnconscious = false;
            return "Wakes up";
        }
    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        return null;
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("Unconscious");
        return ret;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public float getAPModifierPercentage() {
        return -1;
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
