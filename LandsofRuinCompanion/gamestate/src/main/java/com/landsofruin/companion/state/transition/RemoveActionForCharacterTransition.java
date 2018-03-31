package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("RemoveActionForCharacterTransition")
public class RemoveActionForCharacterTransition implements Transition {
    private String characterIdentifier;
    private int actionId;

    public RemoveActionForCharacterTransition(String characterIdentifier, int actionId) {
        this.characterIdentifier = characterIdentifier;
        this.actionId = actionId;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return true;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        trigger(gameState);
    }

    private void trigger(GameState gameState) {
        CharacterState character = gameState.findCharacterByIdentifier(characterIdentifier);
        character.removeAllActionsOfId(actionId);

        Action action = LookupHelper.getInstance().getActionFor(actionId);
        if (action.getAssignToCloseSupportSquad() != 0) {
            if (character instanceof CharacterStateHero) {
                for (String squadId : ((CharacterStateHero) character).getSquads()) {
                    CharacterState squad = gameState.findCharacterByIdentifier(squadId);

                    CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(squad.getCharacterType());
                    if (characterType.getType() == CharacterType.TYPE_SQUAD_CLOSE_SUPPORT) {
                        squad.removeAllActionsOfId(action.getAssignToCloseSupportSquad());
                    }
                }

            }
        }
    }
}
