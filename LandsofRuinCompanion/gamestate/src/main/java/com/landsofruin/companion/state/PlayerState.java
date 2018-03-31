package com.landsofruin.companion.state;


import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.proxyobjecthelper.AccountHelper;

@ObjectiveCName("PlayerState")
public class PlayerState {

    public String identifier;
    private boolean isConnected;
    public String device;
    public String name;
    public TeamState team;
    public boolean isReady;
    private boolean isPreGameReady = false;
    private boolean isZombieReady = false;
    private StartingPosition startingPosition;
    private int scenarioPlayerRole;
    private boolean hasServerAccount;
    private String syncToken;

    private PrepareEndGameState prepareEndGameState;
    private ConfirmEndGameState confirmEndGameState;


    /**
     * required empty constructor for firebase. don't use in code!
     */
    public PlayerState() {

    }

    public PlayerState(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
        this.scenarioPlayerRole = -1;
        this.device = "";// TODO: need to be fixed
    }

    public StartingPosition getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(StartingPosition startingPosition) {
        this.startingPosition = startingPosition;
    }

    public String getDevice() {
        return device;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isPreGameReady() {
        return isPreGameReady;
    }

    public void setPreGameReady(boolean isPreGameReady) {
        this.isPreGameReady = isPreGameReady;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isMe() {
        return AccountHelper.getInstance().getAccountObjectHelper().isMe(identifier);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public void setServeAccount(boolean hasServerAccount) {
        this.hasServerAccount = hasServerAccount;
    }

    public boolean hasServerAccount() {
        return hasServerAccount;
    }

    public boolean hasSyncToken() {
        return syncToken != null;
    }

    public String getSyncToken() {
        return syncToken;
    }

    public void setSyncToken(String syncToken) {
        this.syncToken = syncToken;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof PlayerState)) {
            return false;
        }

        PlayerState otherPlayer = (PlayerState) object;

        return otherPlayer.identifier.equals(identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    public TeamState getTeam() {
        return team;
    }

    public void setTeam(TeamState team) {
        this.team = team;
    }

    public boolean isZombieReady() {
        return isZombieReady;
    }

    public void setZombieReady(boolean zombieGameReady) {
        isZombieReady = zombieGameReady;
    }


    public int getScenarioPlayerRole() {
        return scenarioPlayerRole;
    }

    public void setScenarioPlayerRole(int scenarioPlayerRole) {
        this.scenarioPlayerRole = scenarioPlayerRole;
    }


    public void startPrepareEndGameState() {
        this.prepareEndGameState = new PrepareEndGameState();
    }

    public PrepareEndGameState getPrepareEndGameState() {
        return prepareEndGameState;
    }


    public void startConfirmEndGameState() {
        this.confirmEndGameState = new ConfirmEndGameState();
    }


    public void cancelConfirmEndGameState() {
        this.confirmEndGameState = null;

    }

    public ConfirmEndGameState getConfirmEndGameState() {
        return confirmEndGameState;
    }


    public enum StartingPosition {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }


}
