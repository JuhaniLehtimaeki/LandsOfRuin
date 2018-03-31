package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.ArrayList;
import java.util.List;


@ObjectiveCName("PerformActionTransition")
public class PerformActionTransition implements Transition {
    private String characterIdentifierSelf;
    private String characterIdentifierFriendly;
    private String characterIdentifierEnemy;
    private int actionId;
    private int wargearId;

    public PerformActionTransition(CharacterState characterIdentifierSelf, CharacterState characterIdentifierFriendly, int actionId, Wargear wargear) {


        if (characterIdentifierFriendly != null) {
            this.characterIdentifierFriendly = characterIdentifierFriendly.getIdentifier();
        }

        if (characterIdentifierSelf != null) {
            this.characterIdentifierSelf = characterIdentifierSelf.getIdentifier();
        }

        if (wargear != null) {
            this.wargearId = wargear.getId();
        }

        this.actionId = actionId;
    }


    public PerformActionTransition(CharacterState characterIdentifierSelf, CharacterState characterIdentifierEnemy, int actionId) {
        this(characterIdentifierSelf, null, actionId, null);
        this.characterIdentifierEnemy = characterIdentifierEnemy.getIdentifier();

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

        BattleReportEvent event = new BattleReportEvent();
        event.setEventTitle("Action Performed");
        event.setActionId(actionId);
        event.addSourceCharacter(characterIdentifierSelf);
        event.addTargetCharacter(characterIdentifierFriendly);
        event.setEventType(BattleReportEvent.TYPE_PERFORM_ACTION);
        BattleReportLogger.getInstance().logEvent(event, gameState);
    }

    private void trigger(GameState gameState, boolean isServer) {
        gameState.setPhaseChangeUndoEnabled(false);
        CharacterState playerCharacterSelf = gameState.findCharacterByIdentifier(characterIdentifierSelf);
        CharacterState playerCharacterFriendly = gameState.findCharacterByIdentifier(characterIdentifierFriendly);
        CharacterState playerCharacterEnemy = gameState.findCharacterByIdentifier(characterIdentifierEnemy);


        playerCharacterSelf.addActionToCounted(actionId);


        performAction(playerCharacterSelf, playerCharacterFriendly, playerCharacterEnemy, gameState);


        Action action = LookupHelper.getInstance().getActionFor(actionId);


        if (!isServer) {
            PlayerState performingPlayer = gameState.findPlayerByCharacterIdentifier(characterIdentifierSelf);
            if (actionId == Action.ACTION_ID_REVEAL_ENEMY) {

                if (performingPlayer.isMe()) {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Enemy character revealed", playerCharacterEnemy.getName() + " was revealed and your opponent must now place the character on the table. The character is Surprised for 2 turns.", IconConstantsHelper.getInstance().getIconIdForAction(actionId), playerCharacterSelf, playerCharacterEnemy));
                } else {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Your hidden character revealed", playerCharacterEnemy.getName() + " was revealed and your  must now place the character on the table. The character is Surprised for 2 turns.", IconConstantsHelper.getInstance().getIconIdForAction(actionId), playerCharacterSelf, playerCharacterEnemy));
                }


            } else {

                if (performingPlayer.isMe()) {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(action.getName() + " performed", playerCharacterSelf.getName() + " performed " + action.getName(), IconConstantsHelper.getInstance().getIconIdForAction(actionId), playerCharacterSelf, playerCharacterFriendly));
                } else {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(action.getName() + " performed", playerCharacterSelf.getName() + " performed " + action.getName(), IconConstantsHelper.getInstance().getIconIdForAction(actionId), playerCharacterSelf, playerCharacterFriendly));

                }


            }
        }
    }


    public void performAction(CharacterState playerCharacterSelf, CharacterState playerCharacterFriendly, CharacterState playerCharacterEnemy, GameState gameState) {
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
            effects.addAll(playerCharacterSelf
                    .getCharacterEffects());

            for (CharacterEffect characterEffect : effects) {
                if (isTargetForAny(targetsEffectSelf, characterEffect.getId(), playerCharacterSelf)) {
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
                        characterEffect.getId(), playerCharacterFriendly)) {
                    effectsToRemove.add(characterEffect);
                }
            }

            for (CharacterEffect characterEffect : effectsToRemove) {
                playerCharacterFriendly.removeCharacterEffect(characterEffect);
            }

            for (int effect : targetsEffectFriendly) {
                if (effect == CharacterEffect.ID_WOUNDED_PLACEHOLDER) {
                    playerCharacterFriendly.clearModifiers();
                }
            }

        }


        List<Integer> targetsEffectEnemy = action.getTargetsEffectEnemy();

        if (isTarget(targetsEffectEnemy)) {
            ArrayList<CharacterEffect> effectsToRemove = new ArrayList<>();
            List<CharacterEffect> effects = playerCharacterEnemy
                    .getCharacterEffects();
            for (CharacterEffect characterEffect : effects) {
                if (isTargetForAny(targetsEffectEnemy,
                        characterEffect.getId(), playerCharacterEnemy)) {
                    effectsToRemove.add(characterEffect);
                }
            }

            for (CharacterEffect characterEffect : effectsToRemove) {
                playerCharacterEnemy.removeCharacterEffect(characterEffect);
            }
        }

        for (int characterEffect : action.getRemovesEffects()) {
            playerCharacterSelf.removeCharacterEffect(characterEffect);
        }


        List<Integer> addsEffectEnemy = action.getAddsEffectEnemy();
        if (playerCharacterEnemy != null && addsEffectEnemy.size() > 0) {

            for (int i = 0; i < addsEffectEnemy.size(); ++i) {
                CharacterEffect effect = CharacterEffectFactory
                        .createCharacterEffect(addsEffectEnemy.get(i), gameState.getPhase()
                                .getGameTurn());

                playerCharacterEnemy.addCharacterEffect(effect);

            }
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

    private boolean isTargetForAny(List<Integer> effects, int targetEffect, CharacterState playerCharacter) {
        for (int i : effects) {
            if (i == targetEffect) {
                return true;
            }

            if (i == CharacterEffect.ID_WOUNDED_PLACEHOLDER) {
                //this is not an actual effect but a placeholder for wounded
                if (playerCharacter != null && !playerCharacter.getModifiers().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTargetForAny(List<Integer> targetEffectSelf,
                                   ArrayList<CharacterEffect> effects) {

        for (CharacterEffect characterEffect : effects) {
            if (isTargetForAny(targetEffectSelf, characterEffect.getId(), null)) {
                return true;
            }
        }
        return false;

    }
}
