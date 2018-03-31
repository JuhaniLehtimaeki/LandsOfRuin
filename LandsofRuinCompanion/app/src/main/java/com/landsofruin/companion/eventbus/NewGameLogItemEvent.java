package com.landsofruin.companion.eventbus;

import com.landsofruin.companion.state.GameLogItem;

public class NewGameLogItemEvent {

    private GameLogItem item;

    public NewGameLogItemEvent(GameLogItem item) {
        this.item = item;
    }

    public GameLogItem getItem() {
        return item;
    }
}
