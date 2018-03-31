package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.util.LinkedList;

@ObjectiveCName("PinnedEffect")
public class SurprisedEffect extends CharacterEffect {
    private boolean isActive = true;
    private int currentTurn;

    public SurprisedEffect(int currentTurn) {
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
        return -5;
    }

    @Override
    public int getDefensiveModifierShooting() {
        return -5;
    }

    @Override
    public String getName() {
        return "Surprised";
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
        return CharacterEffect.ID_SURPRICED;
    }

    @Override
    public String getDescription() {
        return "Surprised";
    }

    @Override
    public String advanceOneTurn(GameState gameState, CharacterState character, int dieRoll) {

        if (gameState.getPhase().getGameTurn() >= this.currentTurn + 2) {
            isActive = false;
            return "Surprised recovered";
        }
        return "Still recovering from being surprised";


    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        return null;
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<>();
        ret.add("Surprised");
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
