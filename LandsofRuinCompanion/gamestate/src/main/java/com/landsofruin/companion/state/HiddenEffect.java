package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.LinkedList;

@ObjectiveCName("HiddenEffect")
public class HiddenEffect extends CharacterEffect {
    private int currentTurn;

    public HiddenEffect(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Override
    public boolean isActive(int turn) {
        return true;
    }

    @Override
    public int getOffensiveModifier() {
        return 0;
    }

    @Override
    public int getDefensiveModifierZombie() {
        return 3;
    }

    @Override
    public int getDefensiveModifierCC() {
        return 3;
    }

    @Override
    public int getDefensiveModifierShooting() {
        return 3;
    }

    @Override
    public String getName() {
        return "hidden";
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
        return CharacterEffect.ID_HIDDEN;
    }

    @Override
    public String getDescription() {
        return "hidden from enemy";
    }


    @Override
    public String advanceOneTurn(GameState gameState, CharacterState character, int dieRoll) {
        return null;
    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        return null;
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("Hidden");
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
        return true;
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
