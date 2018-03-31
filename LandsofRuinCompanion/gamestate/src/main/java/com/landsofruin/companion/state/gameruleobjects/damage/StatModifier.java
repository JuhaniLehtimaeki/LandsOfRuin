package com.landsofruin.companion.state.gameruleobjects.damage;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("StatModifier")
public class StatModifier {

    private int offensiveModifier = 0;
    private int defensiveModifier = 0;
    private int duration = 0;
    private String nonModifierRule;

    public StatModifier() {
    }

    public StatModifier(int offensiveModifier, int defensiveModifier,
                        int duration, String nonModifierRule) {
        super();
        this.offensiveModifier = offensiveModifier;
        this.defensiveModifier = defensiveModifier;
        this.duration = duration;
        this.nonModifierRule = nonModifierRule;
    }

    public int getOffensiveModifier() {
        return offensiveModifier;
    }

    public int getDefensiveModifier() {
        return defensiveModifier;
    }

    public int getDuration() {
        return duration;
    }

    public String getNonModifierRule() {
        return nonModifierRule;
    }

}
