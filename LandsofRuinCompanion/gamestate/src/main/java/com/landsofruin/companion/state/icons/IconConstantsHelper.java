package com.landsofruin.companion.state.icons;

/**
 * Created by juhani on 01/07/15.
 */
public class IconConstantsHelper {



    private static int ID_SHIFT_FOR_EFFECTS = 10000;
    private static int ID_SHIFT_FOR_ACTIONS = 5000;
    private static IconConstantsHelper instance = new IconConstantsHelper();

    private IconConstantsHelper() {
    }


    public static IconConstantsHelper getInstance() {
        return instance;
    }

    private IconMapper iconMapper;

    public void setIconMapper(IconMapper iconMapper) {
        this.iconMapper = iconMapper;
    }

    public static int ICON_ID_ZOMBIE_ATTACK_REMOVED = 1;
    public static int ICON_ID_ZOMBIE_ATTACK_ADDED = 2;
    public static int ICON_ID_ZOMBIE_SPAWN = 20;

    //roles
    public static int ICON_ID_ROLE_ICON_SNIPER = 3;
    public static int ICON_ID_ROLE_ICON_MEDIC = 4;
    public static int ICON_ID_ROLE_ICON_PUSHER = 5;
    public static int ICON_ID_ROLE_ICON_HEAVY_WEAPONS = 6;
    public static int ICON_ID_ROLE_ICON_DEMOLITIONS = 7;


    public static int ICON_ID_NEW_HIT = 8;


    public static int ICON_ID_DOWN = 9;
    public static int ICON_ID_NOLONGER_DOWN = 10;

    public static int ICON_ID_BLEEDING_STOPS = 11;

    public static final int ICON_ID_DETECTED = 17;


    public static int ICON_ID_ACTION_PHASE = 12;
    public static int ICON_ID_SYNC_PHASE = 13;
    public static int ICON_ID_COMMAND_PHASE = 14;
    public static int ICON_ID_ZOMBIE_PHASE = 15;


    public static int ICON_ID_DEFAULT_LOG_ICON = 50;

    public int getIconIdForAction(int actionId) {
        return actionId + ID_SHIFT_FOR_ACTIONS;
    }

    public int getIconIdForEffect(int effectId) {
        return effectId + ID_SHIFT_FOR_EFFECTS;
    }

    public int getIconResourceFor(int contantId) {
        if (contantId > ID_SHIFT_FOR_EFFECTS) {
            return this.iconMapper.getIconResourceForEffect(contantId - ID_SHIFT_FOR_EFFECTS);
        }
        if (contantId > ID_SHIFT_FOR_ACTIONS) {
            return this.iconMapper.getIconResourceForAction(contantId - ID_SHIFT_FOR_ACTIONS);
        }

        return this.iconMapper.getIconResourceForIcon(contantId);

    }


    public String getStringIconResourceFor(int contantId) {
        if (contantId > ID_SHIFT_FOR_EFFECTS) {
            return this.iconMapper.getStringIconResourceForEffect(contantId - ID_SHIFT_FOR_EFFECTS);
        }
        if (contantId > ID_SHIFT_FOR_ACTIONS) {
            return this.iconMapper.getStringIconResourceForAction(contantId - ID_SHIFT_FOR_ACTIONS);
        }

        return this.iconMapper.getStringIconResourceForIcon(contantId);

    }


}
