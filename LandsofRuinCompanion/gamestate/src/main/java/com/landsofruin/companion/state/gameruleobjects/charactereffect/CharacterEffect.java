package com.landsofruin.companion.state.gameruleobjects.charactereffect;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.icons.IconConstantsHelper;

import java.util.LinkedList;

@ObjectiveCName("CharacterEffect")
public abstract class CharacterEffect {
    public static final int ID_TAKE_COVER = 2;
    public static final int ID_PINNED = 4;
    public static final int ID_BLEEDING = 5;
    public static final int ID_UNCONSCIOUS = 10;
    public static final int ID_DEAD = 11;
    public static final int ID_WEAPON_DEPLOYED = 12;
    public static final int ID_CARRY_OBJECTIVE = 13;
    public static final int ID_ON_FIRE = 14;
    public static final int ID_HIDDEN = 15;
    public static final int ID_FLEEING = 16;
    public static final int ID_FRENZY = 17;
    public static final int ID_BERSERKER_CHARGE = 18;
    public static final int ID_SURPRICED = 19;


    // this is not an actual effect used. It's a value to enable medic heal any wound
    public static final int ID_WOUNDED_PLACEHOLDER = 99;


    public static int[] ALL_EFFECTS = new int[]
            {ID_TAKE_COVER, ID_PINNED, ID_BLEEDING, ID_UNCONSCIOUS, ID_DEAD, ID_WEAPON_DEPLOYED, ID_CARRY_OBJECTIVE, ID_ON_FIRE,
                    ID_HIDDEN, ID_FLEEING, ID_FRENZY, ID_BERSERKER_CHARGE, ID_WOUNDED_PLACEHOLDER};

    public abstract String getName();

    public abstract String getDescription();

    public abstract int getId();

    public abstract boolean isActive(int turn);

    public abstract int getOffensiveModifier();

    public abstract int getDefensiveModifierZombie();

    public abstract int getDefensiveModifierCC();

    public abstract int getDefensiveModifierShooting();

    public abstract float getMovementModifierPercentage();

    public abstract int getMovementModifier();

    public abstract float getAPModifierPercentage();

    public abstract int getAPModifier();


    public int getIcon() {
        return IconConstantsHelper.getInstance().getIconIdForEffect(getId());
    }

    public abstract String advanceOneTurn(GameState game, CharacterState character, int dieRoll);

    public abstract String handlePostResolution(GameState game, CharacterState character, boolean isServer);

    public abstract LinkedList<String> getEffectLog();

    public abstract boolean canStack();

    public abstract int getSetTurn();

    public abstract float getSuppressionResistance();

    public abstract float getOffensiveModifierResistance();

}
