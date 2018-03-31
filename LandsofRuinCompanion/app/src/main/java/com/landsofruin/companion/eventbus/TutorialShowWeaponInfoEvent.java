package com.landsofruin.companion.eventbus;

public class TutorialShowWeaponInfoEvent {

    private int weaponId;

    public TutorialShowWeaponInfoEvent(int weaponId) {
        this.weaponId = weaponId;
    }

    public int getWeaponId() {
        return weaponId;
    }
}
