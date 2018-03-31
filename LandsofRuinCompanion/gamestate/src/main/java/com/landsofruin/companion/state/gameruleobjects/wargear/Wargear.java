package com.landsofruin.companion.state.gameruleobjects.wargear;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

@ObjectiveCName("Wargear")
public class Wargear {

    public static final String TYPE_CC = "CC";
    public static final String TYPE_RANGED = "Ranged";
    public static final String TYPE_DEFENSIVE = "Defensive";
    public static final String TYPE_THROWABLE = "Throwable";

    public static final String THROWABLE_CATEGORY_ARTILLERY = "Artillery";
    public static final String THROWABLE_CATEGORY_MINION = "Minion";
    public static final String THROWABLE_CATEGORY_GRENADE = "Grenade";
    public static final String THROWABLE_CATEGORY_RPG = "rpg";


    public static final String WEAPON_CATEGORY_PISTOL = "Pistol";
    public static final String WEAPON_CATEGORY_SMG = "SMG";
    public static final String WEAPON_CATEGORY_SHOTGUN = "Shotgun";
    public static final String WEAPON_CATEGORY_RIFLE = "Rifle";
    public static final String WEAPON_CATEGORY_LMG = "LMG";
    public static final String WEAPON_CATEGORY_FLAME = "Flame";
    public static final String WEAPON_CATEGORY_SNIPER_RIFLE = "Sniper Rifle";

    public static final String WEAPON_CATEGORY_EDGED = "Edged";
    public static final String WEAPON_CATEGORY_BLUNT = "Blunt";
    public static final String WEAPON_CATEGORY_HITECH = "Hitech";


    public static final int WARGEAR_TYPE_OFFENSIVE = 1;
    public static final int WARGEAR_TYPE_DEFENSIVE = 2;
    public static final int WARGEAR_TYPE_CONSUMABLE = 3;
    public static final int WARGEAR_TYPE_ACCESSORY = 4;

    private int wargearType;

    private int id;

    private int weaponId;
    private String type;
    private String category;
    private String name;
    private List<Integer> requiresSkills;
    private int camoModifier;

    private float weight;


    private int gearValue = 0;
    private int maintenanceGearValue = 0;

    public Wargear() {
    }

    public Wargear(int wargearType) {
        this.wargearType = wargearType;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int id) {
        this.weaponId = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getRequiresSkills() {
        return requiresSkills;
    }

    public void setRequiresSkills(List<Integer> requiresSkills) {
        this.requiresSkills = requiresSkills;
    }

    @Override
    public String toString() {
        return this.getName() + " - " + this.getType() + " - "
                + this.getCategory();
    }

    public int getId() {
        return id;
    }

    public void setId(int technicalID) {
        this.id = technicalID;
    }

    public int getWargearType() {
        return wargearType;
    }

    public void setWargearType(int wargearType) {
        this.wargearType = wargearType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Wargear) {
            if (((Wargear) o).getId() == this.id) {
                return true;
            }

        }

        return false;

    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getCamoModifier() {
        return camoModifier;
    }

    public void setCamoModifier(int camoModifier) {
        this.camoModifier = camoModifier;
    }

    @Exclude
    public boolean isTypeCC() {
        return TYPE_CC.equalsIgnoreCase(this.type);
    }

    @Exclude
    public boolean isTypeRanged() {
        return TYPE_RANGED.equalsIgnoreCase(this.type);
    }

    @Exclude
    public boolean isTypeDefensive() {
        return TYPE_DEFENSIVE.equalsIgnoreCase(this.type);
    }

    @Exclude
    public boolean isTypeThrowable() {
        return TYPE_THROWABLE
                .equalsIgnoreCase(this.type);
    }

    public int getGearValue() {
        return gearValue;
    }

    public void setGearValue(int gearValue) {
        this.gearValue = gearValue;
    }

    public int getMaintenanceGearValue() {
        return maintenanceGearValue;
    }

    public void setMaintenanceGearValue(int maintenanceGearValue) {
        this.maintenanceGearValue = maintenanceGearValue;
    }
}
