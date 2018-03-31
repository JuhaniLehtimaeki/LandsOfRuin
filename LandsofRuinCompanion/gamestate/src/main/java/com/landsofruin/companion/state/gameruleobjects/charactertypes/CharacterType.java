package com.landsofruin.companion.state.gameruleobjects.charactertypes;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("CharacterType")
public class CharacterType {


    public static final int TYPE_HERO = 0;
    public static final int TYPE_SQUAD_INDEPENDENT = 1;
    public static final int TYPE_SQUAD_CLOSE_SUPPORT = 2;
    public static final int TYPE_SQUAD_SLAVE = 3;

    private int rootSkill;
    private String id;
    private String name;
    private int baseAPs;
    private float movementPerAP;
    private int baseTargetOffensive;
    private int baseTargetOffensiveCloseCombat;
    private int baseTargetDefensive;
    private int baseLeadership;
    private int baseMaxLoad;
    private int baseThrowRange;
    private int detection;
    private int baseCamo;
    private int baseSuppressionDefence;
    private int baseZombieAgroRange;
    private int gearValue = 0;
    private int type = 0;


    public CharacterType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRootSkill() {
        return rootSkill;
    }

    public void setRootSkill(int rootSkill) {
        this.rootSkill = rootSkill;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBaseTargetOffensive() {
        return this.baseTargetOffensive;
    }

    public int getBaseTargetOffensiveCloseCombat() {
        return this.baseTargetOffensiveCloseCombat;
    }

    public int getBaseTargetDefensive() {
        return this.baseTargetDefensive;
    }

    public int getBaseAPs() {
        return baseAPs;
    }

    public void setBaseAPs(int baseAPs) {
        this.baseAPs = baseAPs;
    }

    public float getMovementPerAP() {
        return movementPerAP;
    }

    public void setMovementPerAP(float movementPerAP) {
        this.movementPerAP = movementPerAP;
    }

    public int getBaseLeadership() {
        return baseLeadership;
    }

    public void setBaseLeadership(int baseLeadership) {
        this.baseLeadership = baseLeadership;
    }

    public void setBaseTargetOffensive(int baseTargetOffensive) {
        this.baseTargetOffensive = baseTargetOffensive;
    }

    public void setBaseTargetOffensiveCloseCombat(int baseOffensiveCloseCombat) {
        this.baseTargetOffensiveCloseCombat = baseOffensiveCloseCombat;
    }

    public void setBaseTargetDefensive(int baseTargetDefensive) {
        this.baseTargetDefensive = baseTargetDefensive;
    }

    public int getBaseMaxLoad() {
        return baseMaxLoad;
    }

    public void setBaseMaxLoad(int baseMaxLoad) {
        this.baseMaxLoad = baseMaxLoad;
    }

    public int getBaseThrowRange() {
        return baseThrowRange;
    }

    public void setBaseThrowRange(int baseThrowRange) {
        this.baseThrowRange = baseThrowRange;
    }

    public int getDetection() {
        return detection;
    }

    public void setDetection(int detection) {
        this.detection = detection;
    }

    public int getBaseCamo() {
        return baseCamo;
    }

    public void setBaseCamo(int baseCamo) {
        this.baseCamo = baseCamo;
    }

    public int getBaseSuppressionDefence() {
        return baseSuppressionDefence;
    }

    public void setBaseSuppressionDefence(int baseSuppressionDefence) {
        this.baseSuppressionDefence = baseSuppressionDefence;
    }

    public int getBaseZombieAgroRange() {
        return baseZombieAgroRange;
    }

    public void setBaseZombieAgroRange(int baseZombieAgroRange) {
        this.baseZombieAgroRange = baseZombieAgroRange;
    }

    public void setGearValue(int gearValue) {
        this.gearValue = gearValue;
    }

    public int getGearValue() {
        return gearValue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
