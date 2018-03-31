package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.LinkedList;

@ObjectiveCName("AssignBulkActionTransition")
public class AssignBulkActionTransition implements Transition {
    private String playerId;
    private LinkedList<Integer> actionIds_Integer;

    public AssignBulkActionTransition(String playerId, LinkedList<Integer> actionIds) {
        this.playerId = playerId;
        this.actionIds_Integer = actionIds;
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
        PlayerState player = gameState.findPlayerByIdentifier(playerId);

        MultipleAnimationsHolder animationsHolder = null;
        if (!isServer && player.isMe()) {

            animationsHolder = new MultipleAnimationsHolder(false);


        }


        for (CharacterState character : player.getTeam().listAllTypesCharacters()) {
            character.clearActions();
            if (!character.isOnMap() || character.isDead() || character.isUnconsious() || character.isPinned()) {
                continue;
            }

            for (Integer actionId : actionIds_Integer) {
                character.addAction(actionId);

                if (animationsHolder != null) {
                    animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(character.getIdentifier(), IconConstantsHelper.getInstance().getIconIdForAction(actionId)));
                }

            }
        }

        if (animationsHolder != null) {
            EventsHelper.getInstance().queueAnimations(animationsHolder);
        }
    }
}
