package com.landsofruin.companion.cards.events;


/**
 * Created by juhani on 10/12/13.
 */
public class BlinkCardEvent {

    private String targetCharacterId;


    public BlinkCardEvent(String targetCharacterId) {
        this.targetCharacterId = targetCharacterId;

    }


    public String getTargetCharacterId() {
        return targetCharacterId;
    }
}
