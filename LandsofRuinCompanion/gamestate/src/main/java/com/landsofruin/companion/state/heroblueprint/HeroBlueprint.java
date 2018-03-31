package com.landsofruin.companion.state.heroblueprint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhani on 28/12/15.
 */
@ObjectiveCName("HeroBlueprint")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeroBlueprint {

    public static final int STATUS_TYPE_PRODUCTION_READY = 0;
    public static final int STATUS_TYPE_BETA = 1;
    public static final int STATUS_TYPE_ALPHA = 2;

    private String id;
    private String name;
    private String cardImageUrl = "http://landsofruin.com/app_assets/androphage_card.png";
    private String portraitImageUrl = "http://landsofruin.com/app_assets/androphage_portrait.png";


    private List<Integer> skills = new ArrayList<>();
    private List<Integer> wargear = new ArrayList<>();
    private List<Integer> weapons = new ArrayList<>();
    private List<Ammo> ammo = new ArrayList<>();

    private List<String> canHaveSquads = new ArrayList<>();
    private String characterType = "1";
    private int minimumAPICompatibility = 0;
    private int status = STATUS_TYPE_ALPHA;
    private String roleIconURL;
    private int version;


    public HeroBlueprint() {
    }

    public List<Ammo> getAmmo() {
        return ammo;
    }

    public void setAmmo(List<Ammo> ammo) {
        this.ammo = ammo;
    }


    @Exclude
    public void setAmmoForWeapon(int weaponId, int ammoCount) {


        for (Ammo ammoForWeapon : ammo) {
            if (ammoForWeapon.getWeaponId() == weaponId) {
                ammoForWeapon.setAmmoCount(ammoCount);
                return;
            }
        }

        Ammo newAmmo = new Ammo();
        newAmmo.setAmmoCount(ammoCount);
        newAmmo.setWeaponId(weaponId);
        this.ammo.add(newAmmo);
    }

    @Exclude
    public int getAmmoForWeapon(int weaponId) {

        int ret = 0;
        for (Ammo ammoForWeapon : ammo) {
            if (ammoForWeapon.getWeaponId() == weaponId) {
                return ammoForWeapon.getAmmoCount();
            }
        }

        return ret;
    }


    public List<Integer> getWargear() {
        return wargear;
    }

    public void setWargear(List<Integer> wargear) {
        this.wargear = wargear;
    }

    public List<Integer> getSkills() {
        return skills;
    }

    public void setSkills(List<Integer> skills) {
        this.skills = skills;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void addWeapon(int weaponId) {
        this.weapons.add(weaponId);
    }


    public void removeWeapon(int weaponId) {
        if (this.weapons.contains(weaponId)) {
            this.weapons.remove((Integer) weaponId);
        }
    }

    public void addWargear(int wargearId) {
        this.wargear.add(wargearId);
    }


    public void removeWargear(int wargearId) {
        if (this.wargear.contains(wargearId)) {
            this.wargear.remove((Integer) wargearId);
        }
    }

    public void addSkill(int skillId) {
        this.skills.add(skillId);
    }


    public void removeSkill(int skillId) {
        if (this.skills.contains(skillId)) {
            this.skills.remove((Integer) skillId);
        }
    }

    public List<Integer> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<Integer> weapons) {
        this.weapons = weapons;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getPortraitImageUrl() {
        return portraitImageUrl;
    }

    public void setPortraitImageUrl(String portraitImageUrl) {
        this.portraitImageUrl = portraitImageUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMinimumAPICompatibility() {
        return minimumAPICompatibility;
    }

    public void setMinimumAPICompatibility(int minimumAPICompatibility) {
        this.minimumAPICompatibility = minimumAPICompatibility;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public List<String> getCanHaveSquads() {
        return canHaveSquads;
    }

    public void setCanHaveSquads(List<String> canHaveSquads) {
        this.canHaveSquads = canHaveSquads;
    }

    public String getRoleIconURL() {
        return roleIconURL;
    }

    public void setRoleIconURL(String roleIconURL) {
        this.roleIconURL = roleIconURL;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public static class Ammo {
        private int weaponId;
        private int ammoCount;

        public Ammo() {
        }

        public int getWeaponId() {
            return weaponId;
        }

        public void setWeaponId(int weaponId) {
            this.weaponId = weaponId;
        }

        public int getAmmoCount() {
            return ammoCount;
        }

        public void setAmmoCount(int ammoCount) {
            this.ammoCount = ammoCount;
        }
    }

}
