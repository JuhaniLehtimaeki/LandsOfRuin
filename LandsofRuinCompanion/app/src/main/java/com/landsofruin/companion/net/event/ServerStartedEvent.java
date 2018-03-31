package com.landsofruin.companion.net.event;

import com.landsofruin.companion.state.GameState;

public class ServerStartedEvent {
    private String gameTitle;
    private String gameIdentifier;
    private int port;

    public ServerStartedEvent(GameState gameState, int port) {
        this.gameIdentifier = gameState.getIdentifier();
        this.gameTitle = gameState.getTitle();
        this.port = port;
    }

    public String getGameIdentifier() {
        return gameIdentifier;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public int getPort() {
        return port;
    }
}
