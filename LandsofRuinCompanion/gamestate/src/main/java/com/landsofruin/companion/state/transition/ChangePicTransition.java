package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("ChangePicTransition")
public class ChangePicTransition implements Transition {
    private String characterIdentifier;
    private String profileUrl;
    private String cardUrl;

    public ChangePicTransition(String characterIdentifier, String profileUrl, String cardUrl) {
        this.characterIdentifier = characterIdentifier;
        this.profileUrl = profileUrl;
        this.cardUrl = cardUrl;
    }


    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    private void trigger(GameState gameState) {
        CharacterState character = gameState.findCharacterByIdentifier(characterIdentifier);
        character.setProfilePictureUri(profileUrl);
        character.setCardPictureUri(cardUrl);

    }
}
