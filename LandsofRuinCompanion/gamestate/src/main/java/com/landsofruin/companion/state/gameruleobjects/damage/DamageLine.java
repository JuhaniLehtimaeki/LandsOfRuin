package com.landsofruin.companion.state.gameruleobjects.damage;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

@ObjectiveCName("DamageLine")
public class DamageLine {

    public static final int DAMAGE_TYPE_CC = 1;
    public static final int DAMAGE_TYPE_SHOOT = 2;


    private int damageType;

    private int diceRoll;
    private String effectText;
    private String privateEffectText;
    private String publicEffectText;

    private int offenciveModifier;
    private int defensiveModifier;
    private int effectiveTurnCount;

    private List<Integer> addsEffects = new ArrayList<>();

    private int psychologyEffect;
    private float renownGainOfTargetsGearValue;

    private List<Integer> locations = new ArrayList<>();

    private ArrayList<StatModifier> modifiers = new ArrayList<>();

    public DamageLine() {
    }

    public DamageLine(int diceRoll, String effectText,
                      String privateEffectText, String publicEffectText, int damageType) {
        super();
        this.diceRoll = diceRoll;
        this.effectText = effectText;
        this.privateEffectText = privateEffectText;
        this.publicEffectText = publicEffectText;
        this.damageType = damageType;
        this.addsEffects = new ArrayList<>();
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public String getEffectText() {
        return effectText;
    }

    public String getPrivateEffectText() {
        return privateEffectText;
    }

    public String getPublicEffectText() {
        return publicEffectText;
    }

    public int getOffenciveModifier() {
        return offenciveModifier;
    }

    public void setOffenciveModifier(int offenciveModifier) {
        this.offenciveModifier = offenciveModifier;
    }

    public int getDefensiveModifier() {
        return defensiveModifier;
    }

    public void setDefensiveModifier(int defensiveModifier) {
        this.defensiveModifier = defensiveModifier;
    }

    public int getEffectiveTurnCount() {
        return effectiveTurnCount;
    }

    public void setEffectiveTurnCount(int effectiveTurnCount) {
        this.effectiveTurnCount = effectiveTurnCount;
    }

    public ArrayList<StatModifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(ArrayList<StatModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public List<Integer> getAddsEffects() {
        return addsEffects;
    }

    public void setAddsEffects(List<Integer> addsEffects) {
        this.addsEffects = addsEffects;
    }

    public int getDamageType() {
        return damageType;
    }

    public int getPsychologyEffect() {
        return psychologyEffect;
    }

    public void setPsychologyEffect(int psychologyEffect) {
        this.psychologyEffect = psychologyEffect;
    }

    public float getRenownGainOfTargetsGearValue() {
        return renownGainOfTargetsGearValue;
    }

    public void setRenownGainOfTargetsGearValue(float renownGainOfTargetsGearValue) {
        this.renownGainOfTargetsGearValue = renownGainOfTargetsGearValue;
    }

    public void setLocations(List<Integer> locations) {
        this.locations = locations;
    }

    public List<Integer> getLocations() {
        return locations;
    }
}
