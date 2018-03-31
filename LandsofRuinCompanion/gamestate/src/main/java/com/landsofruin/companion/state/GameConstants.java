package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("GameConstants")
public abstract class GameConstants {
    public static final int ATTACK_OF_OPPORTUNITY_MODIFIER = 5;
    public static final int AIM_MODIFIER = 7;
    public static final int LIGHT_COVER_MODIFIER = 4;
    public static final int HEAVY_COVER_MODIFIER = 8;

    public static final int MAXIMUM_THREAT_LEVEL = 20;

    public static final int ADVANCE_BUTTON_LOCK_TIME = 2000;


    public static final int FULL_MORALE_POOL = 25;

    public static final int MAXIMUM_ZOMBIES_ON_100_DANGER_LEVEL = 75;
    public static final int FAT_ZOMBIE_SPAWN_CHANCE_PER_SPAWNING_ZOMBIE = 10; //out of 100
    public static final int FAST_ZOMBIE_SPAWN_CHANCE_PER_SPAWNING_ZOMBIE = 15; //out of 100


    public static final int FRENZY_WG_ID = 899000;
}
