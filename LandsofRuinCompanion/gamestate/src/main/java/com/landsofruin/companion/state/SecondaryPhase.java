package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("SecondaryPhase")
public abstract class SecondaryPhase {

    public static final int NONE = 0;
    public static final int WAITING_FOR_PLAYERS = 1;
    public static final int WAITING_FOR_ACTION = 2;
    public static final int WAITING_FOR_DAMAGE_RESOLUTION = 3;
    public static final int WAITING_FOR_ATTACK = 4;
    public static final int ZOMBIE_PHASE_SKIPPED = 5;
    public static final int SYNC_CREATE_GAME = 6;
    public static final int SYNC_REQUEST_TOKENS = 7;
    public static final int SYNC_AUTHORIZE_GAME = 8;

    //setup
    public static final int SETUP_SCENARIO_ROLES = 9;
    public static final int SETUP_TABLE = 10;
    public static final int SETUP_TEAM = 11;
    public static final int SETUP_OVERVIEW = 12;
    public static final int SETUP_PLAYERS = 13;
    public static final int SETUP_MAP_SELECT = 14;
    public static final int SETUP_SCENARIO_SELECT = 15;

}
