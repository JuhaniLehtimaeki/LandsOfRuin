package com.landsofruin.companion.provider.snapshots;

import android.content.ContentValues;
import android.util.SparseIntArray;

import com.landsofruin.companion.provider.contracts.CharacterContract;
import com.landsofruin.companion.tribemanagement.TribeCharacter;
import com.landsofruin.companion.utils.GsonProvider;

import java.util.ArrayList;
import java.util.List;

public class CharacterSnapshot {
    private String name;
    private String serverId;
    private int[] skills;
    private int[] wargear;
    private SparseIntArray ammo = new SparseIntArray();
    private String profilePictureUri;
    private String cardPictureUri;
    private String characterType;
    private List<String> squads = new ArrayList<>();
    private String heroIdentifier;
    private String blueprintId;
    private String roleIconURL;

    private CharacterSnapshot() {
    }

    public static CharacterSnapshot fromTribeCharacter(TribeCharacter tribeCharacter) {
        CharacterSnapshot ret = new CharacterSnapshot();

        ret.serverId = tribeCharacter.getId();
        ret.name = tribeCharacter.getName();
        ret.profilePictureUri = tribeCharacter.getPortraitImageUrl();
        ret.cardPictureUri = tribeCharacter.getCardImageUrl();
        ret.roleIconURL = tribeCharacter.getRoleIconURL();
        ret.characterType = tribeCharacter.getCharacterType();

        int[] wargear = new int[tribeCharacter.getWargear().size() + tribeCharacter.getWeapons().size()];

        int index = 0;
        for (Integer id : tribeCharacter.getWargear()) {
            if (id == null) {
                continue;
            }

            wargear[index] = id;
            ++index;
        }


        for (Integer id : tribeCharacter.getWeapons()) {
            wargear[index] = id;
            ++index;
        }

        ret.wargear = wargear;

        int[] skills = new int[tribeCharacter.getSkills().size()];
        index = 0;
        for (Integer id : tribeCharacter.getSkills()) {
            skills[index] = id;
            ++index;
        }

        ret.skills = skills;


        for (TribeCharacter.Ammo ammo : tribeCharacter.getAmmo()) {
            ret.ammo.put(ammo.getWeaponId(), ammo.getAmmoCount());
        }


        ArrayList<String> squads = new ArrayList<>();
        squads.addAll(tribeCharacter.getSquads());
        ret.setSquads(squads);


        ret.setHeroIdentifier(tribeCharacter.getHeroIdentifier());
        ret.setBlueprintId(tribeCharacter.getBlueprintId());

        return ret;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(CharacterContract.SERVER_ID, serverId);
        values.put(CharacterContract.NAME, name);
        values.put(CharacterContract.SNAPSHOT, GsonProvider.getGson().toJson(this));

        return values;
    }

    public String getName() {
        return name;
    }

    public String getServerId() {
        return serverId;
    }

    public int[] getSkills() {
        return skills;
    }

    public int[] getWargear() {
        return wargear;
    }

    public SparseIntArray getAmmo() {
        return ammo;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public String getCardPictureUri() {
        return cardPictureUri;
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

    public void setSquads(List<String> squads) {
        this.squads = squads;
    }

    public String getHeroIdentifier() {
        return heroIdentifier;
    }

    public void setHeroIdentifier(String heroIdentifier) {
        this.heroIdentifier = heroIdentifier;
    }

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    public String getRoleIconURL() {
        return roleIconURL;
    }

    public void setRoleIconURL(String roleIconURL) {
        this.roleIconURL = roleIconURL;
    }
}
