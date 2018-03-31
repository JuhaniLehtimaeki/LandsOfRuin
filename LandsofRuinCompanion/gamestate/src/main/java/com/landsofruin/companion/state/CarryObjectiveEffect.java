package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.LinkedList;

@ObjectiveCName("CarryObjectiveEffect")
public class CarryObjectiveEffect extends CharacterEffect {
    private int currentTurn;

    public CarryObjectiveEffect(int currentTurn) {
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
        return -4;
    }

    @Override
    public int getDefensiveModifierCC() {
        return -4;
    }

    @Override
    public int getDefensiveModifierShooting() {
        return -2;
    }

    @Override
    public String getName() {
        return "carry objective";
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
        return CharacterEffect.ID_CARRY_OBJECTIVE;
    }

    @Override
    public String getDescription() {
        return "carry objective";
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
        ret.add("Carry objective, -50% movement, -2 defensive rolls");
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
