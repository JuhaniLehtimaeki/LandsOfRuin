package com.landsofruin.gametracker.wargear;

import android.content.Context;

import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;

import java.util.HashMap;
import java.util.LinkedList;


public class WargearManager {

    private static final String WARGEAR_WEAPONS_FILE_PATH = "Wargear - Weapons.csv";
    private static final String WARGEAR_CC_FILE_PATH = "Wargear - Close-combat Weapons.csv";
    private static final String WARGEAR_DEFENSIVE_FILE_PATH = "Wargear - defensive.csv";
    private static final String WARGEAR_ACCESSORY_FILE_PATH = "Wargear - accessories.csv";
    private static final String WARGEAR_CONSUMABLES_FILE_PATH = "Wargear - consumables.csv";

    private static WargearManager instance = new WargearManager();
    private Context context;
    private LinkedList<Wargear> wargear = new LinkedList<>();

    private HashMap<Integer, Wargear> wargearLookupCache = new HashMap<>();

    private WargearManager() {
    }

    public static WargearManager getInstance() {
        return instance;
    }


    public LinkedList<Wargear> getNonOffensiveWargear() {
        LinkedList<Wargear> ret = new LinkedList<>();
        for (Wargear wg : wargear) {
            if (!(wg instanceof WargearOffensive)) {
                ret.add(wg);
            }
        }
        return ret;
    }

    public LinkedList<WargearOffensive> getOffensiveWargear() {
        LinkedList<WargearOffensive> ret = new LinkedList<>();


        LinkedList<Integer> handledWeaponIds = new LinkedList<>();

        for (Wargear wg : wargear) {
            if (wg instanceof WargearOffensive) {

                WargearOffensive oWg = (WargearOffensive) wg;

                if (!handledWeaponIds.contains(oWg.getWeaponId())) {
                    handledWeaponIds.add(oWg.getWeaponId());
                    ret.add(oWg);
                }
            }
        }
        return ret;
    }

    public LinkedList<Wargear> getWargear() {
        return wargear;
    }


    public LinkedList<Wargear> getWargearByWeaponID(int id) {
        LinkedList<Wargear> ret = new LinkedList<>();
        for (Wargear wargear_ : wargear) {
            if (id == wargear_.getWeaponId()) {
                ret.add(wargear_);
            }
        }
        return ret;
    }

    public Wargear getWargearById(int id) {

        Wargear ret = wargearLookupCache.get(id);
        if (ret != null) {
            return ret;
        }

        for (Wargear wargear_ : wargear) {
            if (id == wargear_.getId()) {
                wargearLookupCache.put(id, wargear_);
                return wargear_;
            }
        }
        return null;
    }


    public void clearData() {
        this.wargear.clear();
        this.wargearLookupCache.clear();
    }

    public void addData(Wargear wargear) {
        this.wargear.add(wargear);
    }


}
