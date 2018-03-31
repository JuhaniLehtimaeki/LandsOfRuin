package com.landsofruin.gametracker.damage.data;

import android.util.SparseArray;

import com.landsofruin.companion.state.gameruleobjects.damage.DamageLine;

public class DamageDataManager {

    private static final DamageDataManager instance = new DamageDataManager();

    private SparseArray<DamageLine> ccDamage = new SparseArray<>();
    private SparseArray<DamageLine> shootingDamage = new SparseArray<>();

    private DamageDataManager() {

    }

    public static DamageDataManager getInstance() {
        return instance;
    }


    public DamageLine getShootingDamageLine(int number) {
        if (number > 100) {
            number = 100;
        }
        return shootingDamage.get(number);
    }

    public DamageLine getCCDamageLine(int number) {
        if (number > 100) {
            number = 100;
        }

        return ccDamage.get(number);
    }


    public void clearData() {
        this.ccDamage.clear();
        this.shootingDamage.clear();
    }

    public void addCCData(DamageLine damageLine) {
        this.ccDamage.put(damageLine.getDiceRoll(), damageLine);
    }

    public void addShootingData(DamageLine damageLine) {
        this.shootingDamage.put(damageLine.getDiceRoll(), damageLine);
    }


}
