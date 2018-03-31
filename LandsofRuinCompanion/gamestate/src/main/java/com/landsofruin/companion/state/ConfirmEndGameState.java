package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 1/25/14.
 */
@ObjectiveCName("ConfirmEndGameState")
public class ConfirmEndGameState {

    private boolean playerReady = false;

    public boolean isPlayerReady() {
        return playerReady;
    }

    public void setPlayerReady(boolean playerReady) {
        this.playerReady = playerReady;
    }

}
