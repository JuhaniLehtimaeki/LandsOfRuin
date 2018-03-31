package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.HashMap;

/**
 * Created by juhani on 17/05/16.
 */
@ObjectiveCName("SquadMember")
public class SquadMember {

    // we need some value here or squads without ammo will be optimised out by firebase
    private int memberSize = 1;

    private HashMap<String, Integer> ammo = new HashMap<>();

    public int getAmmoFor(int id) {
        try {
            return this.ammo.get(id);
        } catch (Exception e) {
            return 0;
        }
    }

    public void reduceAmmoBy(int id, int ammo) {
        int newAmount = getAmmoFor(id) - ammo;
        if (newAmount < 0) {
            newAmount = 0;
        }
        setAmmoForWeapon(id, newAmount);
    }

    public void setAllAmmo(HashMap<String, Integer> ammo) {
        this.ammo = ammo;
    }

    public void setAmmoForWeapon(int id, int ammo) {

        if (ammo < 0) {
            ammo = 0;
        }
        this.ammo.put("" + id, ammo);
    }


    public HashMap<String, Integer> getAmmo() {
        return ammo;
    }

    public void setAmmo(HashMap<String, Integer> ammo) {
        this.ammo = ammo;
    }

    public int getMemberSize() {
        return memberSize;
    }

    public void setMemberSize(int memberSize) {
        this.memberSize = memberSize;
    }
}
