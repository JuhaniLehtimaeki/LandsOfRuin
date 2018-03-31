package com.landsofruin.companion.objecthelpers;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.gametracker.R;

/**
 * Created by juhani on 11/8/13.
 */
public class IconMapperImpl implements com.landsofruin.companion.state.icons.IconMapper {


    private static SparseIntArray actionToIconResource = new SparseIntArray();
    private static SparseIntArray effectToIconResource = new SparseIntArray();

    private static SparseIntArray iconIDsToIconResource = new SparseIntArray();

    static {
        actionToIconResource.put(2, R.drawable.action_icon_2);
        actionToIconResource.put(3, R.drawable.action_icon_3);
        actionToIconResource.put(4, R.drawable.action_icon_4);
        actionToIconResource.put(5, R.drawable.action_icon_5);
        actionToIconResource.put(6, R.drawable.action_icon_4);
        actionToIconResource.put(7, R.drawable.action_icon_7);
        actionToIconResource.put(10, R.drawable.action_icon_10);
        actionToIconResource.put(11, R.drawable.action_icon_10);
        actionToIconResource.put(12, R.drawable.action_icon_12);
        actionToIconResource.put(13, R.drawable.action_icon_13);
        actionToIconResource.put(14, R.drawable.action_icon_14);
        actionToIconResource.put(15, R.drawable.action_icon_15);
        actionToIconResource.put(16, R.drawable.action_icon_16);
        actionToIconResource.put(17, R.drawable.action_icon_17);
        actionToIconResource.put(18, R.drawable.action_icon_18);
        actionToIconResource.put(19, R.drawable.action_icon_19);
        actionToIconResource.put(20, R.drawable.action_icon_20);
        actionToIconResource.put(21, R.drawable.action_icon_21);
        actionToIconResource.put(22, R.drawable.action_icon_22);
        actionToIconResource.put(23, R.drawable.action_icon_23);
        actionToIconResource.put(24, R.drawable.action_icon_24);
        actionToIconResource.put(1001, R.drawable.action_icon_1001);


        effectToIconResource.put(CharacterEffect.ID_PINNED, R.drawable.effect_icon_pinned);
        effectToIconResource.put(CharacterEffect.ID_DEAD, R.drawable.effect_icon_dead);
        effectToIconResource.put(CharacterEffect.ID_BLEEDING, R.drawable.effect_icon_bleeding);
        effectToIconResource.put(CharacterEffect.ID_CARRY_OBJECTIVE, R.drawable.effect_icon_objective);
        effectToIconResource.put(CharacterEffect.ID_FLEEING, R.drawable.effect_icon_shocked);
        effectToIconResource.put(CharacterEffect.ID_FRENZY, R.drawable.effect_icon_frenzy);
        effectToIconResource.put(CharacterEffect.ID_BERSERKER_CHARGE, R.drawable.effect_icon_frenzy);
        effectToIconResource.put(CharacterEffect.ID_HIDDEN, R.drawable.effect_icon_hidden);
        effectToIconResource.put(CharacterEffect.ID_ON_FIRE, R.drawable.effect_icon_onfire);
        effectToIconResource.put(CharacterEffect.ID_TAKE_COVER, R.drawable.effect_icon_cover);
        effectToIconResource.put(CharacterEffect.ID_UNCONSCIOUS, R.drawable.effect_icon_unconscious);
        effectToIconResource.put(CharacterEffect.ID_WEAPON_DEPLOYED, R.drawable.effect_icon_deploy);
        effectToIconResource.put(CharacterEffect.ID_SURPRICED, R.drawable.effect_icon_shocked);


        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ZOMBIE_ATTACK_REMOVED, R.drawable.zombie_attack_removed);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ZOMBIE_ATTACK_ADDED, R.drawable.zombie_attack);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ZOMBIE_SPAWN, R.drawable.zombie_spawn_normal);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ROLE_ICON_SNIPER, R.drawable.sniper);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ROLE_ICON_MEDIC, R.drawable.reclaimer_medic_symbol);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ROLE_ICON_PUSHER, R.drawable.balean_medic_symbol);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ROLE_ICON_HEAVY_WEAPONS, R.drawable.heavy_weapons);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ROLE_ICON_DEMOLITIONS, R.drawable.demolisions);

        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_NEW_HIT, R.drawable.hit);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_DOWN, R.drawable.effect_icon_down);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_NOLONGER_DOWN, R.drawable.effect_icon_nolonger_down);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_BLEEDING_STOPS, R.drawable.effect_icon_bleeding_stops);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_DETECTED, R.drawable.action_icon_24);

        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ACTION_PHASE, R.drawable.turn_action_mine);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_COMMAND_PHASE, R.drawable.turn_command_mine);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_SYNC_PHASE, R.drawable.turn_sync_mine);
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_ZOMBIE_PHASE, R.drawable.turn_environment_mine);


        //FIXME: this needs an icon!
        iconIDsToIconResource.put(IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, R.drawable.turn_action_mine);

    }


    public int getIconResourceForAction(int actionId) {
        return actionToIconResource.get(actionId);
    }

    public int getIconResourceForEffect(int effectId) {
        return effectToIconResource.get(effectId);
    }


    @Override
    public int getIconResourceForIcon(int id) {
        Integer ret = iconIDsToIconResource.get(id);
        if (ret != null) {
            return ret;
        }
        return 0;
    }

//these are only for the iOS implementation not used on Android

    @Override
    public String getStringIconResourceForAction(int actionId) {
        return null;
    }

    @Override
    public String getStringIconResourceForIcon(int id) {
        return null;
    }

    @Override
    public String getStringIconResourceForEffect(int effectId) {
        return null;
    }


}
