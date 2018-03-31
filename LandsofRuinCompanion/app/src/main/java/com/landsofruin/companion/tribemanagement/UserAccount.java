package com.landsofruin.companion.tribemanagement;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.landsofruin.companion.provider.clients.GameClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by juhani on 29/12/15.
 */
public class UserAccount {

    public static final int ACCOUNT_TYPE_NORMAL = 0;
    public static final int ACCOUNT_TYPE_BETA = 1;
    public static final int ACCOUNT_TYPE_ALPHA = 2;

    private String uuid;
    private String email;
    private List<String> unlockedCharacters = new ArrayList<>();
    private Map<String, Tribe> tribes = new HashMap<>();
    private Tribe defaultTribe;
    private String username;
    private Map<String, String> battleReports = new HashMap<>();
    private int accountType = ACCOUNT_TYPE_NORMAL;
    private Map<String, CharacterPicture> miniaturepics = new HashMap<>();
    private List<GameClient.GameInfo> games = new ArrayList<>();

    public UserAccount() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getUnlockedCharacters() {
        return unlockedCharacters;
    }

    @Exclude
    public Tribe getTribe() {
        return getDefaultTribe();
    }

    public Map<String, Tribe> getTribes() {
        return tribes;
    }

    public void setTribes(Map<String, Tribe> tribes) {
        this.tribes = tribes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, String> getBattleReports() {
        return battleReports;
    }

    public void setBattleReports(Map<String, String> battleReports) {
        this.battleReports = battleReports;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public Map<String, CharacterPicture> getMiniaturepics() {
        return miniaturepics;
    }

    public void setMiniaturepics(Map<String, CharacterPicture> miniaturepics) {
        this.miniaturepics = miniaturepics;
    }

    public Tribe getDefaultTribe() {
        return defaultTribe;
    }

    public void setDefaultTribe(Tribe defaultTribe) {
        this.defaultTribe = defaultTribe;
    }

    public List<GameClient.GameInfo> getGames() {
        return games;
    }

    public void setGames(List<GameClient.GameInfo> games) {
        this.games = games;
    }

    public void addNewGame(GameClient.GameInfo gameInfo) {
        this.games.add(gameInfo);
    }

    public GameClient.GameInfo findGameInfoWithId(String id) {

        Log.e("TMP", "trying to locate gameinfo " + id);

        if (id == null) {
            return null;
        }

        for (GameClient.GameInfo info : this.games) {
            if (id.equals(info.identifier)) {
                return info;
            }
        }

        return null;
    }


}
