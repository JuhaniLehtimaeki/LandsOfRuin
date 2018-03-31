package com.landsofruin.companion.state.gameruleobjects.characterdata;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

@ObjectiveCName("CharacterStatModifier")
public class CharacterStatModifier {

    private String description;
    private int offenciveModifier;
    private int defensiveModifier;
    private int lastEffectiveTurn;
    private List<Integer> locations;


    /**
     * required for firebase, don't use in code
     */
    public CharacterStatModifier() {

    }

    public CharacterStatModifier(String description, int offenciveModifier,
                                 int defensiveModifier, int lastEffectiveTurn, List<Integer> locations) {
        super();
        this.description = description;
        this.offenciveModifier = offenciveModifier;
        this.defensiveModifier = defensiveModifier;
        this.lastEffectiveTurn = lastEffectiveTurn;
        this.locations = locations;

    }

    public String getDescription() {
        return description;
    }

    public int getOffenciveModifier() {
        return offenciveModifier;
    }

    public int getDefensiveModifier() {
        return defensiveModifier;
    }

    public int getLastEffectiveTurn() {
        return lastEffectiveTurn;
    }

    public List<Integer> getLocations() {
        return locations;
    }

    @Override
    public String toString() {
        return "" + offenciveModifier + "/" + defensiveModifier + "," + lastEffectiveTurn;
    }

}
 