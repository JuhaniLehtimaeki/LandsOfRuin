package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.LinkedList;

@ObjectiveCName("PinnedEffect")
public class PinnedEffect extends CharacterEffect {
    private boolean isActive = true;
    private int currentTurn;

    public PinnedEffect(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Override
    public boolean isActive(int turn) {
        return isActive;
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
        return "pinned";
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
        return CharacterEffect.ID_PINNED;
    }

    @Override
    public String getDescription() {
        return "pinned";
    }

    @Override
    public String advanceOneTurn(GameState gameState, CharacterState character, int dieRoll) {
        isActive = false;
        return "Pinned recovered";
    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        return null;
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("Pinned");
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
