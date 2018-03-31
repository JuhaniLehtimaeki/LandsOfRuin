package com.landsofruin.companion.state.gameruleobjects.wargear;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("WargearDefensive")
public class WargearDefensive extends Wargear {

    private int defensiveModifierShooting;
    private int defensiveModifierClosecombat;
    private int defensiveModifierZombies;
    private int defensiveModifierExplosives;

    private int movementModifier;
    private float movementModifierPercentage;

    public WargearDefensive() {
        super();
    }


    public WargearDefensive(int wargearType) {
        super(wargearType);
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

    public void setDefensiveModifierExplosives(int defensiveModifierExplosives) {
        this.defensiveModifierExplosives = defensiveModifierExplosives;
    }

    public int getDefensiveModifierExplosives() {
        return defensiveModifierExplosives;
    }

}
