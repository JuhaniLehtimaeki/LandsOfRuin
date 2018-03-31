package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.UUID;

@ObjectiveCName("Zombie")
public class Zombie extends CharacterState {
    public static final int ZOMBIE_ID_NORMAL = 1;
    public static final int ZOMBIE_ID_FAT = 2;
    public static final int ZOMBIE_ID_FAST = 3;


    public static final String PROFILE_PIC_URL = "http://landsofruin.com/app_assets/shambler_card.png";
    public static final String PROFILE_PIC_PROFILE_URL = "http://landsofruin.com/app_assets/shambler_portrait.png";


    public static final String FAST_PROFILE_PIC_URL = "http://landsofruin.com/app_assets/rager_card.png";
    public static final String FAST_PROFILE_PIC_PROFILE_URL = "http://landsofruin.com/app_assets/rager_portrait.png";


    public static final String FAT_PROFILE_PIC_URL = "http://landsofruin.com/app_assets/veteran_card.png";
    public static final String FAT_PROFILE_PIC_PROFILE_URL = "http://landsofruin.com/app_assets/veteran_portrait.png";


    public static final int SLEEPER_ZOMBIE_CAMO_RATING = 30;
    public static final int ZOMBIE_WEAPON_ID = 999007;
    public static final int ZOMBIE_FAST_WEAPON_ID = 999008;
    public static final int ZOMBIE_FAT_WEAPON_ID = 999009;
    public static final int ZOMBIE_HIT_TARGET_NUMBER = 8;


    private int zombieType = ZOMBIE_ID_NORMAL;


    public Zombie() {
        this(ZOMBIE_ID_NORMAL);
    }


    public Zombie(int zombieType) {
        super("ZOMBIE_" + zombieType + "_" + (UUID.randomUUID().toString()));
        this.zombieType = zombieType;

        switch (zombieType) {
            case ZOMBIE_ID_NORMAL:
                this.setName("Shambler Rotter");
                break;
            case ZOMBIE_ID_FAST:
                this.setName("Rager Rotter");
                break;
            case ZOMBIE_ID_FAT:
                this.setName("Veteran Rotter");
                break;
        }


    }

    @Override
    public ArrayList<String> getDefensiveModifiersExplanationsCC(GameState gamestate) {
        ArrayList<String> ret = new ArrayList<>();


        switch (zombieType) {
            case ZOMBIE_ID_NORMAL:
                break;
            case ZOMBIE_ID_FAST:
                ret.add("-3 for Rager Rotter");
                break;
            case ZOMBIE_ID_FAT:
                ret.add("+3 for Veteran Rotter");
                break;
        }

        return ret;
    }

    @Override
    public ArrayList<String> getDefensiveModifiersExplanationsShooting(GameState gamestate) {
        return getDefensiveModifiersExplanationsCC(gamestate);
    }

    @Override
    public int getCurrentTargetDefensive(GameState gamestate) {
        switch (zombieType) {
            case ZOMBIE_ID_NORMAL:
                return 0;
            case ZOMBIE_ID_FAST:
                return -3;
            case ZOMBIE_ID_FAT:
                return 3;
        }

        return 0;
    }


    public int getZombieType() {
        return zombieType;
    }


    @Override
    public String getProfilePictureUri() {

        switch (zombieType) {
            case ZOMBIE_ID_NORMAL:
                return PROFILE_PIC_PROFILE_URL;
            case ZOMBIE_ID_FAST:
                return FAST_PROFILE_PIC_PROFILE_URL;
            case ZOMBIE_ID_FAT:
                return FAT_PROFILE_PIC_PROFILE_URL;
        }
        return PROFILE_PIC_PROFILE_URL;

    }

    @Override
    public String getCardPictureUri() {

        switch (zombieType) {
            case ZOMBIE_ID_NORMAL:
                return PROFILE_PIC_URL;
            case ZOMBIE_ID_FAST:
                return FAST_PROFILE_PIC_URL;
            case ZOMBIE_ID_FAT:
                return FAT_PROFILE_PIC_URL;
        }
        return PROFILE_PIC_URL;
    }


    public static boolean isZombieWeapon(int weaponId) {
        return weaponId == ZOMBIE_WEAPON_ID || weaponId == ZOMBIE_FAST_WEAPON_ID || weaponId == ZOMBIE_FAT_WEAPON_ID;
    }
}
