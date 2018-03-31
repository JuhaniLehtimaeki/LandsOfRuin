package com.landsofruin.companion.state.gameruleobjects.wargear;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

@ObjectiveCName("WargearConsumable")
public class WargearConsumable extends Wargear {

    public static final int TEMPLATE_SIZE_SECTION = -1;

    private float throwRangeModifier;
    private List<Integer> hitAddsEffects;
    private int hitPower;
    private List<Integer> templateSizeEvolution;
    private int hitNoise;
    public int suppression;
    private String requiresSquad = null;
    private String consumesSquadMember = null;

    public WargearConsumable() {
        super();
        hitAddsEffects = new ArrayList<>();
    }

    public WargearConsumable(int wargearType) {
        super(wargearType);
        hitAddsEffects = new ArrayList<>();
    }

    public float getThrowRangeModifier() {
        return throwRangeModifier;
    }

    public void setThrowRangeModifier(float throwRangeModifier) {
        this.throwRangeModifier = throwRangeModifier;
    }

    public int getHitPower() {
        return hitPower;
    }

    public void setHitPower(int hitPower) {
        this.hitPower = hitPower;
    }

    public int getHitNoise() {
        return hitNoise;
    }

    public void setHitNoise(int hitNoise) {
        this.hitNoise = hitNoise;
    }

    public List<Integer> getHitAddsEffects() {
        return hitAddsEffects;
    }

    public void setHitAddsEffects(List<Integer> hitAddsEffects) {
        this.hitAddsEffects = hitAddsEffects;
    }

    public List<Integer> getTemplateSizeEvolution() {
        return templateSizeEvolution;
    }

    public void setTemplateSizeEvolution(List<Integer> templateSizeEvolution) {
        this.templateSizeEvolution = templateSizeEvolution;
    }

    public int getSuppression() {
        return suppression;
    }

    public void setSuppression(int suppression) {
        this.suppression = suppression;
    }

    public String getRequiresSquad() {
        return requiresSquad;
    }

    public void setRequiresSquad(String requiresSquad) {
        this.requiresSquad = requiresSquad;
    }

    public String getConsumesSquadMember() {
        return consumesSquadMember;
    }

    public void setConsumesSquadMember(String consumesSquadMember) {
        this.consumesSquadMember = consumesSquadMember;
    }
}
