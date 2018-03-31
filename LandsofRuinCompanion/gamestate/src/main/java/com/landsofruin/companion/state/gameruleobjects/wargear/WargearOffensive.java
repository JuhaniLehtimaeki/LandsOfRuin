package com.landsofruin.companion.state.gameruleobjects.wargear;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;

@ObjectiveCName("WargearOffensive")
public class WargearOffensive extends Wargear {

    private int maxTargets;
    private int modifierShort;
    private int modifierMid;
    private int modifierLong;

    private int modifierDeployed;

    private int diceLightInfantry;
    private int diceHeavyInfantry;
    private int diceLightArmored;
    private int diceHeavyArmored;

    private int noiseLevel;
    private int bulletsPerAction;
    private int clipSize;
    private String clipName;

    private String modeName;
    private String imageUri;

    private float clipWeight;

    private int suppression;
    private int suppresionWithoutHititng;

    private int highlightPriority;

    private int clipGearValue = 0;

    private boolean isSquadMode = false;


    public WargearOffensive() {
        super();
    }

    public WargearOffensive(int wargearType) {
        super(wargearType);
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public int getModifierShort() {
        return modifierShort;
    }

    public void setModifierShort(int modifierShort) {
        this.modifierShort = modifierShort;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public int getModifierMid() {
        return modifierMid;
    }

    public void setModifierMid(int modifierMid) {
        this.modifierMid = modifierMid;
    }

    public int getModifierLong() {
        return modifierLong;
    }

    public void setModifierLong(int modifierLong) {
        this.modifierLong = modifierLong;
    }

    public int getDiceLightInfantry() {
        return diceLightInfantry;
    }

    public void setDiceLightInfantry(int diceLightInfantry) {
        this.diceLightInfantry = diceLightInfantry;
    }

    public int getDiceHeavyInfantry() {
        return diceHeavyInfantry;
    }

    public void setDiceHeavyInfantry(int diceHeavyInfantry) {
        this.diceHeavyInfantry = diceHeavyInfantry;
    }

    public int getDiceLightArmored() {
        return diceLightArmored;
    }

    public void setDiceLightArmored(int diceLightArmored) {
        this.diceLightArmored = diceLightArmored;
    }

    public int getDiceHeavyArmored() {
        return diceHeavyArmored;
    }

    public void setDiceHeavyArmored(int diceHeavyArmored) {
        this.diceHeavyArmored = diceHeavyArmored;
    }



    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public int getClipSize() {
        return clipSize;
    }

    public void setClipSize(int clipSize) {
        this.clipSize = clipSize;
    }

    public String getClipName() {
        return clipName;
    }

    public void setClipName(String clipName) {
        this.clipName = clipName;
    }

    public int getBulletsPerAction() {
        return bulletsPerAction;
    }

    public void setBulletsPerAction(int bulletsPerAction) {
        this.bulletsPerAction = bulletsPerAction;
    }

    public float getClipWeight() {
        return clipWeight;
    }

    public void setClipWeight(float clipWeight) {
        this.clipWeight = clipWeight;
    }

    public int getSuppression() {
        return suppression;

    }

    public int getNoiseLevel(CharacterState characterState) {
        //TODO: this needs to take into account silencers etc
        return noiseLevel;
    }

    public int getNoiseLevel() {
        return noiseLevel;
    }

    public void setSuppression(int suppresion) {
        this.suppression = suppresion;
    }

    public int getSuppresionWithoutHititng() {
        return suppresionWithoutHititng;
    }

    public void setSuppresionWithoutHititng(int suppresionWithoutHititng) {
        this.suppresionWithoutHititng = suppresionWithoutHititng;
    }

    public int getHighlightPriority() {
        return highlightPriority;
    }

    public void setHighlightPriority(int highlightPriority) {
        this.highlightPriority = highlightPriority;
    }

    public int getClipGearValue() {
        return clipGearValue;
    }

    public void setClipGearValue(int clipGearValue) {
        this.clipGearValue = clipGearValue;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public int getModifierDeployed() {
        return modifierDeployed;
    }

    public void setModifierDeployed(int modifierDeployed) {
        this.modifierDeployed = modifierDeployed;
    }

    public boolean isSquadMode() {
        return isSquadMode;
    }

    public void setSquadMode(boolean squadMode) {
        this.isSquadMode = squadMode;
    }
}
