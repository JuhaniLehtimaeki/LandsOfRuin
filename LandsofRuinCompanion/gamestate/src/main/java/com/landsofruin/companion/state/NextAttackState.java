package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@ObjectiveCName("NextAttackState")
public class NextAttackState {
    public static final String META_KEY_NUMBER_OF_SHOOTERS = "META_KEY_NUMBER_OF_SHOOTERS";
    public static final String META_KEY_SHORT_RANGE = "META_KEY_SHORT_RANGE";
    public static final String META_KEY_MID_RANGE = "META_KEY_MID_RANGE";
    public static final String META_KEY_LONG_RANGE = "META_KEY_LONG_RANGE";
    public static final String META_KEY_SOFT_COVER = "META_KEY_SOFT_COVER";
    public static final String META_KEY_HARD_COVER = "META_KEY_HARD_COVER";
    public static final String META_KEY_ATTACK_OF_OPPORTUNITY = "META_KEY_ATTACK_OF_OPPORTUNITY";
    public static final String META_KEY_MISS = "META_KEY_MISS";
    public static final String META_KEY_HIT = "META_KEY_HIT";
    public static final String META_KEY_CRIT = "META_KEY_CRIT";

    private String characterAttackingId;
    private LinkedList<String> targets_String = new LinkedList<>();
    private int actionId;
    private int wargearId;
    private HashMap<String, HashMap<String, Boolean>> characterMetadata_String_String_Boolean = new HashMap<>();
    private HashMap<String, Boolean> metadata_String_Boolean = new HashMap<>();
    private List<String> readyPlayers_String = new ArrayList<>();
    private int numberOfShooters = 1;


    public NextAttackState(String characterAttackingId, int actionId, int wargearId) {
        super();
        this.characterAttackingId = characterAttackingId;
        this.actionId = actionId;
        this.wargearId = wargearId;
    }

    public boolean isMeAttacking(GameState game) {
        return game.findPlayerByCharacterIdentifier(characterAttackingId).isMe();
    }

    public void readyPlayer(String playerId) {
        this.readyPlayers_String.add(playerId);
    }

    public boolean isAllPlayersReady(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {
            if (!isPlayerReady(player.getIdentifier())) {
                return false;
            }
        }
        return true;
    }

    public void unreadyAllPlayers(String exceptThisPlayer) {
        boolean isPlayerReady = this.isPlayerReady(exceptThisPlayer);

        this.readyPlayers_String.clear();
        if (isPlayerReady) {
            this.readyPlayers_String.add(exceptThisPlayer);
        }
    }

    public void unreadyAllPlayers() {
        this.readyPlayers_String.clear();
    }

    public boolean isPlayerReady(String playerId) {
        return this.readyPlayers_String.contains(playerId);
    }

    public boolean isMeDefending(GameState game) {

        for (String characterId : targets_String) {
            if (game.findPlayerByCharacterIdentifier(characterId) == null) {
                return false;
            }

            if (game.findPlayerByCharacterIdentifier(characterId).isMe()) {
                return true;
            }
        }
        return false;
    }

    public String getCharacterAttackingId() {
        return characterAttackingId;
    }

    public int getActionId() {
        return actionId;
    }

    public LinkedList<String> getTargets() {
        return targets_String;
    }

    public int getWargearId() {
        return wargearId;
    }

    public HashMap<String, HashMap<String, Boolean>> getCharacterMetadata() {
        return characterMetadata_String_String_Boolean;
    }

    public void setCharacterMetadata(HashMap<String, HashMap<String, Boolean>> characterMetadata) {
        this.characterMetadata_String_String_Boolean = characterMetadata;
    }

    public void setTargets(LinkedList<String> targets) {
        this.targets_String = targets;
        Collections.sort(this.targets_String);
        this.characterMetadata_String_String_Boolean.clear();
    }

    public HashMap<String, Boolean> getMetadata() {
        return metadata_String_Boolean;
    }

    public void setMetadata(HashMap<String, Boolean> metadata) {
        this.metadata_String_Boolean = metadata;
    }

    public int getNumberOfShooters() {
        return numberOfShooters;
    }

    public void setNumberOfShooters(int numberOfShooters) {
        this.numberOfShooters = numberOfShooters;
    }
}
