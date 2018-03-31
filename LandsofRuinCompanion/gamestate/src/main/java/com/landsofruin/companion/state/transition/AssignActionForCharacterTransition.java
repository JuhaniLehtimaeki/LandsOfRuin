package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("AssignActionForCharacterTransition")
public class AssignActionForCharacterTransition implements Transition {
    private String characterIdentifier;
    private int actionId;

    public AssignActionForCharacterTransition(String characterIdentifier, int actionId) {
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


        BattleReportEvent event = new BattleReportEvent();
        event.setEventTitle("Action Assigned");
        event.setActionId(actionId);
        event.addSourceCharacter(characterIdentifier);
        event.setEventType(BattleReportEvent.TYPE_ASSIGN_ACTION);
        BattleReportLogger.getInstance().logEvent(event, gameState);
    }

    private void trigger(GameState gameState) {
        CharacterState character = gameState.findCharacterByIdentifier(characterIdentifier);
        character.addAction(actionId);

        Action action = LookupHelper.getInstance().getActionFor(actionId);
        if (action.getAssignToCloseSupportSquad() != 0) {
            if (character instanceof CharacterStateHero) {
                for (String squadId : ((CharacterStateHero) character).getSquads()) {
                    CharacterState squad = gameState.findCharacterByIdentifier(squadId);

                    CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(squad.getCharacterType());
                    if (characterType.getType() == CharacterType.TYPE_SQUAD_CLOSE_SUPPORT) {
                        squad.addAction(action.getAssignToCloseSupportSquad());
                    }
                }

            }
        }


    }
}
