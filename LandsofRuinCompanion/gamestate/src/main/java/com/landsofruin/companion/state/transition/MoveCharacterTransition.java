package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.List;

@ObjectiveCName("MoveCharacterTransition")
public class MoveCharacterTransition implements Transition {
    private String characterIdentifier;
    private List<String> regionIdentifiers_String;
    private PointState point;

    public MoveCharacterTransition(String characterIdentifier, List<String> regionIdentifiers, PointState point) {
        this.characterIdentifier = characterIdentifier;
        this.regionIdentifiers_String = regionIdentifiers;
        this.point = point;
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

        if (character.isHidden() && gameState.getPhase().getPrimaryPhase() != PrimaryPhase.PRE_GAME) {
            return;
        }


        character.updatePosition(point, regionIdentifiers_String);
        character.getBattleLogState().addMovementHistoryPoint(gameState.getPhase().getGameTurn(), point);
        character.setMovedOnMapThisTurn(true);


        if (gameState.getPhase().getPrimaryPhase() == PrimaryPhase.PRE_GAME) {
            character.addPregameDefaultEffects();
        }


    }
}
