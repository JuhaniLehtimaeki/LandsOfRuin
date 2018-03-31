package com.landsofruin.companion.state.tutorial;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 13/01/15.
 */
@ObjectiveCName("TutorialConstants")
public abstract class TutorialConstants {

    //setup
    public static final String KEY_GAME_SETUP_ADMIN = "KEY_GAME_SETUP_ADMIN";
    public static final String KEY_GAME_SETUP_NON_ADMIN = "KEY_GAME_SETUP_NON_ADMIN";

    //general
    public static final String KEY_GAME_UI_OVERVIEW = "KEY_GAME_UI_OVERVIEW";

    // game phases
    public static final String KEY_PRE_GAME_OVERVIEW = "KEY_PRE_GAME_OVERVIEW";
    public static final String KEY_FIRST_ACTION_PHASE_OVERVIEW = "KEY_FIRST_ACTION_PHASE_OVERVIEW";
    public static final String KEY_ACTION_PHASE_OVERVIEW = "KEY_ACTION_PHASE_OVERVIEW";
    public static final String KEY_ENEMY_TURN_OVERVIEW = "KEY_ENEMY_TURN_OVERVIEW";
    public static final String KEY_MOVEMENT_PHASE_OVERVIEW = "KEY_MOVEMENT_PHASE_OVERVIEW";
    public static final String KEY_COMMAND_PHASE_OVERVIEW = "KEY_COMMAND_PHASE_OVERVIEW";
    public static final String KEY_ZOMBIE_PHASE_OVERVIEW = "KEY_ZOMBIE_PHASE_OVERVIEW";


    // non in-game tutorial pages
    public static final String KEY_TURN_STRUCTURE = "KEY_TURN_STRUCTURE";
    public static final String KEY_ZOMBIE_MOVEMENT_RULES = "KEY_ZOMBIE_MOVEMENT_RULES";
}
