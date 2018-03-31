package com.landsofruin.companion.state.gameruleobjects.wargear;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

@ObjectiveCName("WargearAccessory")
public class WargearAccessory extends Wargear {

    private int defensiveModifierShooting;
    private int defensiveModifierClosecombat;
    private int defensiveModifierZombies;

    private int movementModifier;
    private float movementModifierPercentage;

    private int offensiveModifier;

    private int requiredEffect;
    private List<Integer> attachableTo;

    private float noiseModification;

    public WargearAccessory() {
        super();
    }

    public WargearAccessory(int wargearType) {
        super(wargearType);
    }

    public float getNoiseModification() {
        return noiseModification;
    }

    public void setNoiseModification(float noiseModification) {
        this.noiseModification = noiseModification;
    }

    public void setRequiredEffect(int requiredEffect) {
        this.requiredEffect = requiredEffect;
    }

    public int getRequiredEffect() {
        return requiredEffect;
    }

    public void setAttachableTo(List<Integer> attachableTo) {
        this.attachableTo = attachableTo;
    }

    public List<Integer> getAttachableTo() {
        return attachableTo;
    }

    public int getOffensiveModifier() {
        return offensiveModifier;
    }

    public void setOffensiveModifier(int offensiveModifier) {
        this.offensiveModifier = offensiveModifier;
    }

    public void setDefensiveModifierClosecombat(int defensiveModifierClosecombat) {
        this.defensiveModifierClosecombat = defensiveModifierClosecombat;
    }

    public void setDefensiveModifierShooting(int defensiveModifierShooting) {
        this.defensiveModifierShooting = defensiveModifierShooting;
    }

    public void setDefensiveModifierZombies(int defensiveModifierZombies) {
        this.defensiveModifierZombies = defensiveModifierZombies;
    }

    public void setMovementModifier(int movementModifier) {
        this.movementModifier = movementModifier;
    }

    public void setMovementModifierPercentage(float movementModifierPercentage) {
        this.movementModifierPercentage = movementModifierPercentage;
    }

    public int getDefensiveModifierClosecombat() {
        return defensiveModifierClosecombat;
    }

    public int getDefensiveModifierShooting() {
        return defensiveModifierShooting;
    }

    public int getDefensiveModifierZombies() {
        return defensiveModifierZombies;
    }

    public int getMovementModifier() {
        return movementModifier;
    }

    public float getMovementModifierPercentage() {
        return movementModifierPercentage;
    }

}
