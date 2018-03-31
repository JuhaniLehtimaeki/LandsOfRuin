package com.landsofruin.companion.tribemanagement;

import com.google.firebase.database.Exclude;
import com.landsofruin.companion.state.heroblueprint.HeroBlueprint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by juhani on 03/01/16.
 */
public class TribeCharacter {

    private String id;
    private String blueprintId;
    private String name;
    private String defaultCardImageUrl;
    private String defaultPortraitImageUrl;
    private String cardImageUrl;
    private String portraitImageUrl;
    private String characterType;
    private String roleIconURL;
    private int spawnedFromBlueprintVersion;

    private List<Integer> skills = new ArrayList<>();
    private List<Integer> wargear = new ArrayList<>();
    private List<Integer> weapons = new ArrayList<>();
    private List<Ammo> ammo = new ArrayList<>();
    private List<String> squads = new ArrayList<>();


    private String heroIdentifier;

    public TribeCharacter() {
    }


    public List<Ammo> getAmmo() {
        return ammo;
    }

    public void setAmmo(List<Ammo> ammo) {
        this.ammo = ammo;
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

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    public String getDefaultCardImageUrl() {
        return defaultCardImageUrl;
    }

    public void setDefaultCardImageUrl(String defaultCardImageUrl) {
        this.defaultCardImageUrl = defaultCardImageUrl;
    }

    public String getDefaultPortraitImageUrl() {
        return defaultPortraitImageUrl;
    }

    public void setDefaultPortraitImageUrl(String defaultPortraitImageUrl) {
        this.defaultPortraitImageUrl = defaultPortraitImageUrl;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public List<String> getSquads() {
        return squads;
    }

    public void addSquad(String squad) {
        squads.add(squad);
    }

    public void setSquads(List<String> squads) {
        this.squads = squads;
    }

    public String getHeroIdentifier() {
        return heroIdentifier;
    }

    public void setHeroIdentifier(String heroIdentifier) {
        this.heroIdentifier = heroIdentifier;
    }

    public String getRoleIconURL() {
        return roleIconURL;
    }

    public void setRoleIconURL(String roleIconURL) {
        this.roleIconURL = roleIconURL;
    }

    public int getSpawnedFromBlueprintVersion() {
        return spawnedFromBlueprintVersion;
    }

    public void setSpawnedFromBlueprintVersion(int spawnedFromBlueprintVersion) {
        this.spawnedFromBlueprintVersion = spawnedFromBlueprintVersion;
    }


    public static class Ammo {
        private int weaponId;
        private int ammoCount;

        public Ammo() {
        }

        public Ammo(int weaponId, int ammoCount) {
            this.weaponId = weaponId;
            this.ammoCount = ammoCount;
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


    public static TribeCharacter createFromBlueprint(HeroBlueprint heroBlueprint) {
        TribeCharacter ret = new TribeCharacter();

        ret.setBlueprintId(heroBlueprint.getId());
        ret.setName(heroBlueprint.getName());
        ret.setDefaultCardImageUrl(heroBlueprint.getCardImageUrl());
        ret.setCardImageUrl(heroBlueprint.getCardImageUrl());
        ret.setPortraitImageUrl(heroBlueprint.getPortraitImageUrl());
        ret.setDefaultPortraitImageUrl(heroBlueprint.getPortraitImageUrl());
        ret.setSpawnedFromBlueprintVersion(heroBlueprint.getVersion());

        List<Integer> skills = new LinkedList<>();
        skills.addAll(heroBlueprint.getSkills());
        ret.setSkills(skills);

        List<Integer> wargear = new LinkedList<>();
        wargear.addAll(heroBlueprint.getWargear());
        ret.setWargear(wargear);

        List<Integer> weapons = new LinkedList<>();
        weapons.addAll(heroBlueprint.getWeapons());
        ret.setWeapons(weapons);

        for (HeroBlueprint.Ammo ammo : heroBlueprint.getAmmo()) {
            ret.ammo.add(new Ammo(ammo.getWeaponId(), ammo.getAmmoCount()));
        }
        ret.roleIconURL = heroBlueprint.getRoleIconURL();
        ret.characterType = heroBlueprint.getCharacterType();
        return ret;
    }
}
