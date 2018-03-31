package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

@ObjectiveCName("AddRemoveZombiePhaseHitTransition")
public class AddRemoveZombiePhaseHitTransition implements Transition {
    private String characterIdentifier;
    private int wargearId;
    private boolean isRemove = false;

    public AddRemoveZombiePhaseHitTransition(String characterIdentifier, int wargearId, boolean isRemove) {
        this.characterIdentifier = characterIdentifier;
        this.wargearId = wargearId;
        this.isRemove = isRemove;
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
        if (isRemove) {
            character.removeZombieTurnHitWargearId(this.wargearId);
        } else {
            character.addZombieTurnHitWargearId(this.wargearId);
        }


        if (!isServer) {

            MultipleAnimationsHolder animationsHolder = new MultipleAnimationsHolder(false);
            animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(character.getIdentifier(), isRemove ? IconConstantsHelper.ICON_ID_ZOMBIE_ATTACK_REMOVED : IconConstantsHelper.ICON_ID_ZOMBIE_ATTACK_ADDED));

            EventsHelper.getInstance().queueAnimations(animationsHolder);

        }


    }
}
