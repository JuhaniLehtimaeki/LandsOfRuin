package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("MoveCharacterOffTableTransition")
public class MoveCharacterOffTableTransition implements Transition {
    private String characterIdentifier;

    public MoveCharacterOffTableTransition(String characterIdentifier) {
        this.characterIdentifier = characterIdentifier;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return true;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState, false);
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState, true);
    }

    private void trigger(GameState gameState, boolean isServer) {
        CharacterState character = gameState.findCharacterByIdentifier(characterIdentifier);
        if (character.isHidden() && gameState.getPhase().getPrimaryPhase() != PrimaryPhase.PRE_GAME) {
            return;
        }


        character.updatePositionOffTable();

        if (gameState.getPhase().getPrimaryPhase() == PrimaryPhase.PRE_GAME) {
            character.removePregameDefaultEffects();
        } else {

            if (!isServer) {
                PlayerState performingPlayer = gameState.findPlayerByCharacterIdentifier(characterIdentifier);

                if (performingPlayer.isMe()) {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Character left the table.", character.getName() + " left the table.", IconConstantsHelper.ICON_ID_SYNC_PHASE, character));
                } else {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Character left the table.", character.getName() + " left the table.", IconConstantsHelper.ICON_ID_SYNC_PHASE, character));
                }
            }
        }
    }
}
