package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.AttackLogItem;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.NextAttackState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.dice.DiceUtils;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.characterdata.UnresolvedHit;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.psychology.PsychologyConstants;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Move a character on the map.
 */
@ObjectiveCName("PerformShootingTransition")
public class PerformShootingTransition implements Transition {
    private String characterId;
    private ArrayList<UnresolvedHit> hits_UnresolvedHit;
    private int wargearId;
    private int actionID;
    private String performingPlayer;
    private ArrayList<Integer> diceRollsForPercentage = new ArrayList<>();

    public PerformShootingTransition(int actionID, String characterId, ArrayList<UnresolvedHit> hits, int wargearId, String performingPlayer) {
        this.characterId = characterId;
        this.hits_UnresolvedHit = hits;
        this.wargearId = wargearId;
        this.actionID = actionID;
        this.performingPlayer = performingPlayer;
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
        gameState.setPhaseChangeUndoEnabled(false);
        // Check that everyone is ready. If not just set the performing player ready and complete
        NextAttackState actionState = gameState.getPhase().getNextAttackState();
        if (actionState != null) {
            actionState.readyPlayer(this.performingPlayer);


            for (PlayerState player : gameState.getPlayers()) {
                if (!actionState.isPlayerReady(player.getIdentifier())) {
                    if (player.isMe() && !isServer) {
                        EventsHelper.getInstance().opponentIsWaitingForYou();
                    }
                    return;
                }
            }


        }

        MultipleAnimationsHolder animationsHolder = null;
        if (!isServer) {
            animationsHolder = new MultipleAnimationsHolder(true);
        }


        TeamState attackingTeam = gameState.findTeamByCharacterIdentifier(characterId);


        CharacterState shooter = gameState.findCharacterByIdentifier(characterId);

        Wargear wargear = LookupHelper.getInstance().getWargearFor(wargearId);

        performAction(shooter, null, null, gameState);

        int percentageRoll;
        if (isServer) {
            percentageRoll = DiceUtils.rollDie(100);
            this.diceRollsForPercentage.add(percentageRoll);
        } else {
            percentageRoll = this.diceRollsForPercentage.remove(0);
        }


        for (UnresolvedHit hit : this.hits_UnresolvedHit) {
            CharacterState character = gameState.findCharacterByIdentifier(hit.getTargetCharacterId());

            TeamState team = gameState.findTeamByCharacterIdentifier(hit.getTargetCharacterId());

            if (attackingTeam != null) {
                attackingTeam.addPositivePsychologyEffect(PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT);
            }

            if (team == null) {
                // this is a zombie!

                if (shooter != null) {
                    if (isServer) {
                        BattleReportLogger.getInstance().logAttackEvent(new AttackLogItem(gameState.getPhase().getGameTurn(),
                                null, Zombie.PROFILE_PIC_URL, !hit.isMiss(), hit.getSourceWargearId(), 100, hit.getRange(), hit.isHardCover(), hit.isSoftCover(), hit.isAttackOfOpportunity()), shooter, gameState);
                    }

                    if (attackingTeam != null && !hit.isMiss()) {
                        attackingTeam.addPositivePsychologyEffect(PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT);
                    }

                    PlayerState player = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());

                    if (!isServer && !hit.isMiss()) {

                        if (player.isMe()) {
                            int psychologyEffect = PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT;

                            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter killed", "Rotter hit and killed. Added " + psychologyEffect + " to morale pool.", IconConstantsHelper.ICON_ID_ZOMBIE_PHASE, shooter, new Zombie()));
                        } else {
                            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter killed", "Rotter hit and killed.", IconConstantsHelper.ICON_ID_ZOMBIE_PHASE, shooter, new Zombie()));
                        }
                    }
                }


                continue;
            }

            team.addNegativePsychologyEffect(PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED);
            int suppression = 0;

            if (wargear instanceof WargearOffensive) {
                WargearOffensive wg = ((WargearOffensive) wargear);

                character.addSuppression(wg.getSuppresionWithoutHititng());

                suppression = wg.getSuppresionWithoutHititng();
            }

            if (hit.isMiss()) {

                // log miss here as it'll be forgotten from now on.
                // hits_UnresolvedHit are logged when damage is resolved
                if (shooter != null) {

                    if (isServer) {
                        BattleReportLogger.getInstance().logAttackEvent(new AttackLogItem(gameState.getPhase().getGameTurn(),
                                character.getIdentifier(), character.getProfilePictureUri(), false, hit.getSourceWargearId(), -1, hit.getRange(), hit.isHardCover(), hit.isSoftCover(), hit.isAttackOfOpportunity()), shooter, gameState);
                    }
                }

                if (!isServer) {
                    PlayerState shoterPlayer = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());
                    PlayerState targetPlayer = gameState.findPlayerByCharacterIdentifier(character.getIdentifier());

                    if (shoterPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Fire and miss", "Attack misses. " + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT + " to morale pool for attacking.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                    } else if (targetPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Fire and miss", "Attack misses. -" + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + " to morale pool for being targeted.\n" +
                                "Suppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                    }


                }

                continue;
            }


            if (animationsHolder != null) {
                animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(character.getIdentifier(), IconConstantsHelper.ICON_ID_NEW_HIT));
            }

            team.addNegativePsychologyEffect(PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT);
            attackingTeam.addPositivePsychologyEffect(PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT);


            if (wargear instanceof WargearOffensive) {
                WargearOffensive wg = ((WargearOffensive) wargear);

                character.addSuppression(wg.getSuppression());
                suppression = suppression + wg.getSuppression();

            } else if (wargear instanceof WargearConsumable) {
                WargearConsumable wg = ((WargearConsumable) wargear);

                character.addSuppression(wg.getSuppression());
                suppression = suppression + wg.getSuppression();
            }
            if (character instanceof CharacterStateSquad) {
                boolean killed = ((CharacterStateSquad) character).killASquadMember();

                if (!isServer && killed) {
                    PlayerState shooterPlayer = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());
                    PlayerState targetPlayer = gameState.findPlayerByCharacterIdentifier(character.getIdentifier());

                    if (shooterPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Squad member killed", "Attack hits the target and kills a squad member." + (PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT) + " to morale pool for attacking and hitting.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                    } else if (targetPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Squad member killed", "Attack hits the target and kills a squad member. -" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\n" +
                                "Suppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                    }
                }

            } else if (character instanceof CharacterStateHero && ((CharacterStateHero) character).getMinionCount(team) > 0) {


                for (CharacterStateSquad squad : team.getSquads()) {
                    if (((CharacterStateHero) character).getSquads().contains(squad.getIdentifier())) {


                        CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(squad.getCharacterType());
                        if (characterType.getType() == CharacterType.TYPE_SQUAD_SLAVE) {
                            // deal damage to the minions automatically

                            ((CharacterStateHero) character).loseMinion(team);

                            if (!isServer) {
                                PlayerState shooterPlayer = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());
                                PlayerState targetPlayer = gameState.findPlayerByCharacterIdentifier(character.getIdentifier());

                                if (shooterPlayer.isMe()) {
                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Minion killed", "Attack hits the target and kills a minion." + (PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT) + " to morale pool for attacking and hitting.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                } else if (targetPlayer.isMe()) {
                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Minion killed", "Attack hits the target and kills a minion. -" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\n" +
                                            "Suppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                }
                            }

                        } else if (characterType.getType() == CharacterType.TYPE_SQUAD_CLOSE_SUPPORT) {
                            // close support has hit chance of 50% to minions

                            if (percentageRoll < 50) {
                                character.addUnresolvedHit(hit);


                                if (!isServer) {
                                    PlayerState shoterPlayer = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());
                                    PlayerState targetPlayer = gameState.findPlayerByCharacterIdentifier(character.getIdentifier());

                                    if (shoterPlayer.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Attack hits", "Unresolved hit added to the target. " + (PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT) + " to morale pool for attacking and hitting.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                    } else if (targetPlayer.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Attack hits", "Unresolved hit added to the target. -" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being targeted.\n" +
                                                "Suppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                    }
                                }
                            } else {
                                ((CharacterStateHero) character).loseMinion(team);

                                if (!isServer) {
                                    PlayerState shooterPlayer = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());
                                    PlayerState targetPlayer = gameState.findPlayerByCharacterIdentifier(character.getIdentifier());

                                    if (shooterPlayer.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Minion killed", "Attack hits the target and kills a minion." + (PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT) + " to morale pool for attacking and hitting.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                    } else if (targetPlayer.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Minion killed", "Attack hits the target and kills a minion. -" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\n" +
                                                "Suppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                    }
                                }
                            }


                        } else {
                            // deal damage to character
                            character.addUnresolvedHit(hit);


                            if (!isServer) {
                                PlayerState shoterPlayer = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());
                                PlayerState targetPlayer = gameState.findPlayerByCharacterIdentifier(character.getIdentifier());

                                if (shoterPlayer.isMe()) {
                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Attack hits", "Unresolved hit added to the target. " + (PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT) + " to morale pool for attacking and hitting.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                } else if (targetPlayer.isMe()) {
                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Attack hits", "Unresolved hit added to the target. -" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being targeted.\n" +
                                            "Suppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                                }
                            }
                        }


                        break;
                    }
                }


            } else {
                character.addUnresolvedHit(hit);


                if (!isServer) {
                    PlayerState shoterPlayer = gameState.findPlayerByCharacterIdentifier(shooter.getIdentifier());
                    PlayerState targetPlayer = gameState.findPlayerByCharacterIdentifier(character.getIdentifier());

                    if (shoterPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Attack hits", "Unresolved hit added to the target. " + (PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_SHOOT + PsychologyConstants.POSITIVE_PSYCHOLOGY_EFFECT_TARGET_HIT) + " to morale pool for attacking and hitting.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                    } else if (targetPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Attack hits", "Unresolved hit added to the target. -" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being targeted.\n" +
                                "Suppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_DEFAULT_LOG_ICON, shooter, character));
                    }
                }
            }


        }

        gameState.getPhase().setSecondaryPhase(SecondaryPhase.WAITING_FOR_ACTION);
        gameState.getPhase().setNextAttackState(null);


        if (animationsHolder != null) {
            EventsHelper.getInstance().queueAnimations(animationsHolder);
        }
    }


    public void performAction(CharacterState playerCharacterSelf, CharacterState playerCharacterFriendly, CharacterState characterEnemy, GameState gameState) {
        Wargear wargear = LookupHelper.getInstance().getWargearFor(wargearId);


        Action action = LookupHelper.getInstance().getActionFor(actionID);

        if (wargear instanceof WargearOffensive) {
            if (wargear != null
                    && ((WargearOffensive) wargear).getBulletsPerAction() > 0) {
                playerCharacterSelf.reduceAmmoBy(
                        wargear.getWeaponId(), ((WargearOffensive) wargear).getBulletsPerAction());
            }

            if (wargear != null) {

                int noise = ((WargearOffensive) wargear)
                        .getNoiseLevel(playerCharacterSelf);


                if (playerCharacterSelf instanceof CharacterStateSquad) {
                    noise = noise * ((CharacterStateSquad) playerCharacterSelf).getSquadSize();
                }

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
            ArrayList<CharacterEffect> effectsToRemove = new ArrayList<CharacterEffect>();
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
