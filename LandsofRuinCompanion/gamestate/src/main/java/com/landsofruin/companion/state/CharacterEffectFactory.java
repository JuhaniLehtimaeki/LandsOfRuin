package com.landsofruin.companion.state;


import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

@ObjectiveCName("CharacterEffectFactory")
public class CharacterEffectFactory {

    public static CharacterEffect createCharacterEffect(int id, int currentTurn) {
        switch (id) {
            case CharacterEffect.ID_TAKE_COVER:
                return new TakeCoverEffect(currentTurn);

            case CharacterEffect.ID_DEAD:
                return new DeadEffect(currentTurn);

            case CharacterEffect.ID_BLEEDING:
                return new BleedingEffect(currentTurn);

            case CharacterEffect.ID_UNCONSCIOUS:
                return new UnconsciousEffect(currentTurn);

            case CharacterEffect.ID_PINNED:
                return new PinnedEffect(currentTurn);

            case CharacterEffect.ID_WEAPON_DEPLOYED:
                return new WeaponDeployedEffect(currentTurn);

            case CharacterEffect.ID_CARRY_OBJECTIVE:
                return new CarryObjectiveEffect(currentTurn);

            case CharacterEffect.ID_ON_FIRE:
                return new OnFireEffect(currentTurn);

            case CharacterEffect.ID_HIDDEN:
                return new HiddenEffect(currentTurn);

            case CharacterEffect.ID_FLEEING:
                return new FleeingEffect(currentTurn);

            case CharacterEffect.ID_FRENZY:
                return new FrenzyEffect(currentTurn);

            case CharacterEffect.ID_BERSERKER_CHARGE:
                return new BerserkerChargeEffect(currentTurn);

            case CharacterEffect.ID_SURPRICED:
                return new SurprisedEffect(currentTurn);


            case CharacterEffect.ID_WOUNDED_PLACEHOLDER:
                return new WoundedPlaceholderEffect(currentTurn);
            default:
                throw new IllegalArgumentException("No effect for id: " + id);
        }
    }

}
