package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.LinkedList;

@ObjectiveCName("BerserkerChargeEffect")
public class BerserkerChargeEffect extends CharacterEffect {


    private int currentTurn;
    private boolean isActive = true;


    public BerserkerChargeEffect(int currentTurn) {
        this.currentTurn = currentTurn;
    }


    @Override
    public int getOffensiveModifier() {
        return 0;
    }

    @Override
    public int getDefensiveModifierZombie() {
        return -1;
    }

    @Override
    public int getDefensiveModifierCC() {
        return 0;
    }

    @Override
    public int getDefensiveModifierShooting() {
        return -3;
    }

    @Override
    public String getName() {
        return "Berserker Charge";
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
        return CharacterEffect.ID_BERSERKER_CHARGE;
    }

    @Override
    public String getDescription() {
        return "Berserker Charge";
    }


    @Override
    public boolean isActive(int turn) {
        return isActive;
    }


    @Override
    public String advanceOneTurn(GameState gameState, CharacterState character, int dieRoll) {
        isActive = false;
        return "Berserker charge over";
    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        return null;
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<>();
        ret.add("Berserker Charge");
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
