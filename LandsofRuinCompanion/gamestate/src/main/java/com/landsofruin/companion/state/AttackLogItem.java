package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 16/02/16.
 */
@ObjectiveCName("AttackLogItem")
public class AttackLogItem {

    private int turn;
    private String targetCharacter;
    private String targetCharacterPortraitUrl;
    private boolean hit;
    private int damage;
    private int wgId;
    private int range;
    private boolean hardCover;
    private boolean softCover;
    private boolean attackOfOpportunity;

    public AttackLogItem() {
    }

    public AttackLogItem(int turn, String targetCharacter, String targetCharacterPortraitUrl, boolean hit, int wgId, int damage, int range, boolean hardCover, boolean softCover, boolean attackOfOpportunity) {
        this.turn = turn;
        this.targetCharacter = targetCharacter;
        this.hit = hit;
        this.wgId = wgId;
        this.damage = damage;
        this.range = range;
        this.hardCover = hardCover;
        this.softCover = softCover;
        this.attackOfOpportunity = attackOfOpportunity;
        this.targetCharacterPortraitUrl = targetCharacterPortraitUrl;
    }

    public int getDamage() {
        return damage;
    }

    public int getTurn() {
        return turn;
    }

    public String getTargetCharacter() {
        return targetCharacter;
    }

    public void setTargetCharacter(String targetCharacter) {
        this.targetCharacter = targetCharacter;
    }

    public int getWgId() {
        return wgId;
    }


    public boolean isHit() {
        return hit;
    }

    public int getRange() {
        return range;
    }

    public boolean isHardCover() {
        return hardCover;
    }

    public boolean isSoftCover() {
        return softCover;
    }

    public boolean isAttackOfOpportunity() {
        return attackOfOpportunity;
    }

    public String getTargetCharacterPortraitUrl() {
        return targetCharacterPortraitUrl;
    }

    public void setTargetCharacterPortraitUrl(String targetCharacterPortraitUrl) {
        this.targetCharacterPortraitUrl = targetCharacterPortraitUrl;
    }
}

