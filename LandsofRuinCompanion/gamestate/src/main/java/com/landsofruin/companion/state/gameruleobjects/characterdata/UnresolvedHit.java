package com.landsofruin.companion.state.gameruleobjects.characterdata;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

@ObjectiveCName("UnresolvedHit")
public class UnresolvedHit {

    public final static int RANGE_CC = -1;
    public final static int RANGE_SHORT = 0;
    public final static int RANGE_MID = 1;
    public final static int RANGE_LONG = 2;

    public final static int TYPE_CC = 0;
    public final static int TYPE_SHOOTING = 1;
    public final static int TYPE_THROWABLE = 2;
    private int type;
    private int extraHits;
    private List<Integer> addsEffects;
    private String targetCharacterId;
    private String sourceCharacterId;
    private int sourceWargearId;

    // see constants
    private int range;
    private boolean hardCover;
    private boolean softCover;
    private boolean attackOfOpportunity;

    private boolean isMiss = false;


    /**
     * required empty constructor for firebase, don't use in code
     */
    public UnresolvedHit() {

    }

    public UnresolvedHit(int type, int extraHits) {
        super();
        this.type = type;
        this.extraHits = extraHits;
    }

    public List<Integer> getAddsEffect() {
        return addsEffects;
    }

    public void setAddsEffect(List<Integer> addsEffects) {
        this.addsEffects = addsEffects;
    }

    public int getType() {
        return type;

    }

    public int getExtraHits() {
        return extraHits;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UnresolvedHit) {
            return ((UnresolvedHit) o).type == this.type
                    && ((UnresolvedHit) o).extraHits == this.extraHits;
        }

        return false;
    }

    public String getTargetCharacterId() {
        return targetCharacterId;
    }

    public void setTargetCharacterId(String targetCharacterId) {
        this.targetCharacterId = targetCharacterId;
    }

    public String getSourceCharacterId() {
        return sourceCharacterId;
    }

    public void setSourceCharacterId(String sourceCharacterId) {
        this.sourceCharacterId = sourceCharacterId;
    }

    public int getSourceWargearId() {
        return sourceWargearId;
    }

    public void setSourceWargearId(int sourceWargearId) {
        this.sourceWargearId = sourceWargearId;
    }

    public void setIsMiss() {
        this.isMiss = true;
    }

    public boolean isMiss() {
        return isMiss;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setSoftCover(boolean softCover) {
        this.softCover = softCover;
    }

    public void setHardCover(boolean hardCover) {
        this.hardCover = hardCover;
    }

    public void setAttackOfOpportunity(boolean attackOfOpportunity) {
        this.attackOfOpportunity = attackOfOpportunity;
    }

    public int getRange() {
        return range;
    }

    public boolean isSoftCover() {
        return softCover;
    }

    public boolean isHardCover() {
        return hardCover;
    }

    public boolean isAttackOfOpportunity() {
        return attackOfOpportunity;
    }
}
