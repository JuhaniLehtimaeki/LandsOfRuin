package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.ArrayList;
import java.util.List;

@ObjectiveCName("CreateThrowableTransition")
public class CreateThrowableTransition implements Transition {
    private String characterIdentifierSelf;
    private PointState point;
    private int wargearId;
    private int actionId;

    public CreateThrowableTransition(int wargearId, PointState point, String characterIdentifierSelf, int actionId) {
        this.wargearId = wargearId;
        this.point = point;
        this.characterIdentifierSelf = characterIdentifierSelf;
        this.actionId = actionId;
    }

    public PointState getPoint() {
        return point;
    }

    public int getWargearId() {
        return wargearId;
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


        ThrowableState newThrowable = new ThrowableState(this.wargearId, gameState.findPlayerByCharacterIdentifier(characterIdentifierSelf).getIdentifier());

        ArrayList<String> regions = new ArrayList<>();
        for (RegionState region : gameState.getMap().findRegionsByPoint(point)) {
            regions.add(region.getIdentifier());
        }

        newThrowable.updatePosition(point, regions);

        gameState.getWorld().addThrowableState(newThrowable);
        CharacterState characterSelf = gameState.findCharacterByIdentifier(characterIdentifierSelf);

        Wargear wargear = LookupHelper.getInstance().getWargearFor(wargearId);

        performAction(characterSelf, null, null, gameState);

        if (!isServer) {
            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Consumable spent", characterSelf.getName() + " threw a " + wargear.getName(), IconConstantsHelper.ICON_ID_ROLE_ICON_DEMOLITIONS, characterSelf));
        }

    }

    public void performAction(CharacterState playerCharacterSelf, CharacterState playerCharacterFriendly, CharacterState characterEnemy, GameState gameState) {
        Wargear wargear = LookupHelper.getInstance().getWargearFor(wargearId);


        Action action = LookupHelper.getInstance().getActionFor(actionId);

        if (wargear instanceof WargearOffensive) {
            if (wargear != null
                    && ((WargearOffensive) wargear).getBulletsPerAction() > 0) {
                playerCharacterSelf.reduceAmmoBy(
                        wargear.getWeaponId(), ((WargearOffensive) wargear).getBulletsPerAction());
            }

            if (wargear != null) {

                int noise = ((WargearOffensive) wargear)
                        .getNoiseLevel(playerCharacterSelf);

                playerCharacterSelf.addNoise(noise);
                playerCharacterSelf.setCurrentNoise(playerCharacterSelf
                        .getCurrentNoise() + noise);

                if (playerCharacterSelf.getRegions() != null) {

                    for (String regionIdentifier : playerCharacterSelf
                            .getRegions()) {
                        RegionState region = gameState.getMap()
                                .findRegionByIdentifier(regionIdentifier);
                        region.addNoiseForPlayer(gameState.getPhase()
                                .getCurrentPlayer(), noise / 2); // add only 50% noise to the current section. Rest is added after movement.
                    }
                }
            }
        } else if (wargear instanceof WargearConsumable) {
            playerCharacterSelf
                    .removeConsumableWargear((WargearConsumable) wargear, gameState.findTeamByCharacterIdentifier(playerCharacterSelf.getIdentifier()));
        }

        playerCharacterSelf.reduceUnusedActionPoints(action.getActionPoints());

        List<Integer> addEffectFriendly = action.getAddsEffectFriendly();
        if (playerCharacterFriendly != null && addEffectFriendly.size() > 0) {

            for (int i = 0; i < addEffectFriendly.size(); ++i) {
                CharacterEffect effect = CharacterEffectFactory
                        .createCharacterEffect(addEffectFriendly.get(i), gameState.getPhase()
                                .getGameTurn());

                playerCharacterFriendly.addCharacterEffect(effect);

            }
        }

        List<Integer> targetsEffectSelf = action.getTargetsEffectSelf();
        if (isTarget(targetsEffectSelf)) {

            ArrayList<CharacterEffect> effects = new ArrayList<>();
            effects.addAll(playerCharacterSelf.getCharacterEffects());


            for (CharacterEffect characterEffect : effects) {
                if (isTargetForAny(targetsEffectSelf, characterEffect.getId())) {
                    playerCharacterSelf.removeCharacterEffect(characterEffect);
                }
            }

        }

        List<Integer> addEffectSelf = action.getAddsEffectSelf();
        if (isTarget(addEffectSelf)) {

            for (int id : addEffectSelf) {
                CharacterEffect effect = CharacterEffectFactory
                        .createCharacterEffect(id, gameState.getPhase()
                                .getGameTurn());

                playerCharacterSelf.addCharacterEffect(effect);
            }

        }

        List<Integer> targetsEffectFriendly = action.getTargetsEffectFriendly();

        if (isTarget(targetsEffectFriendly)) {
            ArrayList<CharacterEffect> effectsToRemove = new ArrayList<>();
            List<CharacterEffect> effects = playerCharacterFriendly
                    .getCharacterEffects();
            for (CharacterEffect characterEffect : effects) {
                if (isTargetForAny(targetsEffectFriendly,
                        characterEffect.getId())) {
                    effectsToRemove.add(characterEffect);
                }
            }

            for (CharacterEffect characterEffect : effectsToRemove) {
                playerCharacterFriendly.removeCharacterEffect(characterEffect);
            }

        }

        for (int characterEffect : action.getRemovesEffects()) {

            playerCharacterSelf.removeCharacterEffect(characterEffect);

        }

        playerCharacterSelf
                .addActionToPerformed(action.getId());
    }


    private boolean isTarget(List<Integer> effects) {
        for (int i : effects) {
            if (i > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isTargetForAny(List<Integer> effects, int targetEffect) {
        for (int i : effects) {
            if (i == targetEffect) {
                return true;
            }
        }
        return false;
    }

    private boolean isTargetForAny(List<Integer> targetEffectSelf,
                                   ArrayList<CharacterEffect> effects) {

        for (CharacterEffect characterEffect : effects) {
            if (isTargetForAny(targetEffectSelf, characterEffect.getId())) {
                return true;
            }
        }
        return false;

    }
}
