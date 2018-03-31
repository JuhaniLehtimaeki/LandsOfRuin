package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.characterdata.UnresolvedHit;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;

import java.util.LinkedList;

@ObjectiveCName("FrenzyEffect")
public class FrenzyEffect extends CharacterEffect {


    private int currentTurn;

    public FrenzyEffect(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Override
    public boolean isActive(int turn) {
        return true;
    }

    @Override
    public int getOffensiveModifier() {
        return 0;
    }

    @Override
    public int getDefensiveModifierZombie() {
        return -1;
    }

    @Override
    public int getDefensiveModifierCC() {
        return 2;
    }

    @Override
    public int getDefensiveModifierShooting() {
        return -3;
    }

    @Override
    public String getName() {
        return "Frenzy";
    }

    @Override
    public float getMovementModifierPercentage() {
        return 0;
    }

    @Override
    public int getMovementModifier() {
        return 0;
    }

    @Override
    public int getId() {
        return CharacterEffect.ID_FRENZY;
    }

    @Override
    public String getDescription() {
        return "frenzy";
    }



    @Override
    public String advanceOneTurn(GameState game, CharacterState character, int dieRoll) {
        return null;
    }

    @Override
    public String handlePostResolution(GameState game, CharacterState character, boolean isServer) {
        WargearOffensive wargear = (WargearOffensive) LookupHelper.getInstance().getWargearFor(GameConstants.FRENZY_WG_ID);
        UnresolvedHit hit = new UnresolvedHit(UnresolvedHit.TYPE_CC, wargear.getDiceLightInfantry() - 1);


        if (!isServer && game.findPlayerByCharacterIdentifier(character.getIdentifier()).isMe()) {
            MultipleAnimationsHolder holder = new MultipleAnimationsHolder(true);
            holder.addOneAnimationEffectHolder(new AnimationHolder(character.getIdentifier(), IconConstantsHelper.ICON_ID_NEW_HIT));
        }


        character.addUnresolvedHit(hit);

        return "Frenzy damage";
    }

    @Override
    public LinkedList<String> getEffectLog() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("Unconscious");
        return ret;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public float getAPModifierPercentage() {
        return 0;
    }

    @Override
    public int getAPModifier() {
        return 0;
    }

    @Override
    public boolean canStack() {
        return false;
    }

    @Override
    public int getSetTurn() {
        return currentTurn;
    }

    @Override
    public float getSuppressionResistance() {
        return 1;
    }

    @Override
    public float getOffensiveModifierResistance() {
        return 1;
    }
}
