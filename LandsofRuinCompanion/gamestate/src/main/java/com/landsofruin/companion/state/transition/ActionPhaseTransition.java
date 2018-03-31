package com.landsofruin.companion.state.transition;

import android.util.Log;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.dice.DiceUtils;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Advance to {@link PrimaryPhase#ACTION}.
 */
@ObjectiveCName("ActionPhaseTransition")
public class ActionPhaseTransition implements Transition {
    private ArrayList<Integer> diceRolls_Integer = new ArrayList<>();
    private ArrayList<Integer> diceRollsForPercentage = new ArrayList<>();
    private HashMap<String, String> charactersDetectedByPlayer_String_String = new HashMap<>();
    private String playerId;

    public ActionPhaseTransition(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean isRelevantForServerSync() {
        return true;
    }


    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        gameState.setPhaseChangeUndoEnabled(false);

        gameState.findPlayerByIdentifier(playerId).setZombieReady(true);
        for (PlayerState player : gameState.getPlayers()) {
            if (!player.isZombieReady()) {
                if (player.isMe()) {
                    EventsHelper.getInstance().opponentIsWaitingForYou();
                }
                return;
            }
        }


        for (PlayerState player : gameState.getPlayers()) {
            player.setZombieReady(false);
        }


        handleZombieDamages(gameState, false);

        trigger(gameState, false);

        if (gameState.getPhase().isMine() && gameState.getPhase().getGameTurn() > 1) {

            EventsHelper.getInstance().myTurnStarted();
        }


        if (gameState.getPhase().isMine()) {

            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Your turn (" + gameState.getPhase().getGameTurn() + ") !", null, IconConstantsHelper.ICON_ID_ACTION_PHASE));
        }


    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        gameState.setPhaseChangeUndoEnabled(false);
        gameState.findPlayerByIdentifier(playerId).setZombieReady(true);
        for (PlayerState player : gameState.getPlayers()) {
            if (!player.isZombieReady()) {
                return;
            }
        }


        for (PlayerState player : gameState.getPlayers()) {
            player.setZombieReady(false);
        }


        handleZombieDamages(gameState, true);

        checkForDetection(gameState);
        trigger(gameState, true);

        BattleReportEvent event = new BattleReportEvent();
        event.setEventTitle("Action Phase");
        event.setEventType(BattleReportEvent.TYPE_PHASE_CHANGE);
        BattleReportLogger.getInstance().logEvent(event, gameState);
    }

    private void handleZombieDamages(GameState gameState, boolean isServer) {
        for (PlayerState player : gameState.getPlayers()) {


            TeamState team = player.getTeam();

            MultipleAnimationsHolder animationsHolder = null;
            if (!isServer && player.isMe()) {
                animationsHolder = new MultipleAnimationsHolder(true);
            }

            for (CharacterState charState : player.getTeam().listAllTypesCharacters()) {


                for (int wargearId : charState.getZombieTurnHitsWargearIds()) {


                    if (charState.isDead()) {
                        if (!isServer && player.isMe()) {
                            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Ignoring hit to a dead character", "" + charState.getName() + " is already dead. Ignoring any new hits.", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                        }
                        continue;
                    }

                    if (wargearId == Zombie.ZOMBIE_WEAPON_ID || wargearId == Zombie.ZOMBIE_FAT_WEAPON_ID || wargearId == Zombie.ZOMBIE_FAST_WEAPON_ID) {
                        team.addNegativePsychologyEffect(PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED);

                        WargearOffensive wargear = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargearId));
                        charState.addSuppression(wargear.getSuppresionWithoutHititng());

                        int targetNumber = Zombie.ZOMBIE_HIT_TARGET_NUMBER + charState.getCurrentDefensiveModifierZombies(gameState);
                        int roll;
                        int percentageRoll;

                        if (isServer) {
                            roll = DiceUtils.rollDie(20);
                            this.diceRolls_Integer.add(roll);

                            percentageRoll = DiceUtils.rollDie(100);
                            this.diceRollsForPercentage.add(percentageRoll);
                        } else {
                            roll = this.diceRolls_Integer.remove(0);
                            percentageRoll = this.diceRollsForPercentage.remove(0);
                        }


                        if (roll >= targetNumber) {
                            //hit!
                            UnresolvedHit hit = new UnresolvedHit(UnresolvedHit.TYPE_CC, wargear.getDiceLightInfantry() - 1);
                            hit.setSourceWargearId(wargearId);


                            int suppression = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargearId)).getSuppression();
                            charState.addSuppression(suppression);

                            if (charState instanceof CharacterStateSquad) {
                                boolean killed = ((CharacterStateSquad) charState).killASquadMember();

                                if (killed) {
                                    if (!isServer && player.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter Hit" + " - Squad Member killed", "Attack hits the target and kills a squad member.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                    } else if (!isServer) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter Hit" + " - Squad Member killed", "Attack hits the target and kills a squad member.", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                    }
                                }

                            } else if (charState instanceof CharacterStateHero && ((CharacterStateHero) charState).getMinionCount(team) > 0) {


                                for (CharacterStateSquad squad : team.getSquads()) {
                                    if (((CharacterStateHero) charState).getSquads().contains(squad.getIdentifier())) {


                                        CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(squad.getCharacterType());
                                        if (characterType.getType() == CharacterType.TYPE_SQUAD_SLAVE) {
                                            // deal damage to the minions automatically

                                            ((CharacterStateHero) charState).loseMinion(team);

                                            if (!isServer && player.isMe()) {
                                                EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter hit - Minion killed", "Attack hits the target and kills a minion.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                            } else if (!isServer) {
                                                EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter hit - Minion killed", "Attack hits the target and kills a minion.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                            }

                                        } else if (characterType.getType() == CharacterType.TYPE_SQUAD_CLOSE_SUPPORT) {
                                            // close support has hit chance of 50% to minions

                                            if (percentageRoll < 50) {
                                                charState.addUnresolvedHit(hit);


                                                if (!isServer && player.isMe()) {
                                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter hit", "" + charState.getName() + " was hit by a Rotter.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                                }
                                            } else {
                                                ((CharacterStateHero) charState).loseMinion(team);

                                                if (!isServer && player.isMe()) {
                                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter hit - Minion killed", "Attack hits the target and kills a minion.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                                } else if (!isServer) {
                                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter hit - Minion killed", "Attack hits the target and kills a minion.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                                }
                                            }


                                        } else {
                                            // deal damage to character
                                            charState.addUnresolvedHit(hit);


                                            if (!isServer && player.isMe()) {
                                                EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter hit", "" + charState.getName() + " was hit by a Rotter.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                            }
                                        }


                                        break;
                                    }
                                }


                            } else {
                                charState.addUnresolvedHit(hit);


                                if (!isServer && player.isMe()) {
                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter hit", "" + charState.getName() + " was hit by a Rotter.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                }
                            }

                            if (animationsHolder != null) {
                                animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(charState.getIdentifier(), IconConstantsHelper.ICON_ID_NEW_HIT));
                            }
                            team.addNegativePsychologyEffect(PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT);


                        } else {
                            if (!isServer && player.isMe()) {
                                EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Rotter missed", "" + charState.getName() + " -" + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + " to morale pool for being hit." +
                                        "\n" + wargear.getSuppresionWithoutHititng() + "% suppression added from the miss.", IconConstantsHelper.ICON_ID_ZOMBIE_ATTACK_REMOVED, charState));
                            }
                        }


                    } else {

                        Wargear wg = LookupHelper.getInstance().getWargearFor(wargearId);
                        if (wg instanceof WargearConsumable) {


                            team.addNegativePsychologyEffect(PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED);

                            int targetNumber = 5 + charState.getCurrentDefensiveModifierExplosives(gameState);
                            int roll;
                            if (isServer) {
                                roll = DiceUtils.rollDie(20);
                                this.diceRolls_Integer.add(roll);
                            } else {
                                roll = this.diceRolls_Integer.remove(0);
                            }


                            int suppression = ((WargearConsumable) wg).getSuppression();

                            if (roll >= targetNumber) {
                                //hit!
                                UnresolvedHit hit = new UnresolvedHit(UnresolvedHit.TYPE_THROWABLE, ((WargearConsumable) wg).getHitPower() - 1);
                                hit.setSourceWargearId(wargearId);

                                if (charState instanceof CharacterStateSquad) {
                                    boolean killed = ((CharacterStateSquad) charState).killASquadMember();

                                    if (killed) {
                                        if (!isServer && player.isMe()) {
                                            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(wg.getName() + " - Squad Member killed", "Attack hits the target and kills a squad member.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                        } else if (!isServer) {
                                            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(wg.getName() + " - Squad Member killed", "Attack hits the target and kills a squad member.", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                        }
                                    }

                                } else if (charState instanceof CharacterStateHero && ((CharacterStateHero) charState).getMinionCount(team) > 0) {

                                    ((CharacterStateHero) charState).loseMinion(team);


                                    if (!isServer && player.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(wg.getName() + " - Minion killed", "Attack hits the target and kills a minion.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                    } else if (!isServer) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(wg.getName() + " - Minion killed", "Attack hits the target and kills a minion.", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                    }


                                } else {
                                    charState.addUnresolvedHit(hit);


                                    if (!isServer && player.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(wg.getName() + " hit", "" + charState.getName() + " was hit.\n-" + (PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_BEING_TARGETED + PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT) + " to morale pool for being hit.\nSuppression added: " + suppression + "%", IconConstantsHelper.ICON_ID_NEW_HIT, charState));
                                    }
                                }


                                charState.addSuppression(suppression);

                                if (animationsHolder != null) {
                                    animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(charState.getIdentifier(), IconConstantsHelper.ICON_ID_NEW_HIT));
                                }

                                team.addNegativePsychologyEffect(PsychologyConstants.NEGATIVE_PSYCHOLOGY_EFFECT_RECEIVING_HIT);
                            }

                        }

                    }


                }
                charState.resetZombieTurnHitsWargearIds();
            }

            if (animationsHolder != null) {
                EventsHelper.getInstance().queueAnimations(animationsHolder);
            }

        }
    }


    private void trigger(GameState gameState, boolean isServer) {

        resetAttackingZombies(gameState);
        advancePhase(gameState);
        prepareTeam(gameState);
        markDetections(gameState, isServer);
        cleanupExplosions(gameState);

    }


    private void cleanupExplosions(GameState gameState) {
        LinkedList<ThrowableState> remove = new LinkedList<>();

        for (ThrowableState throwable : gameState.getWorld().getThrowableStates()) {
            if (throwable.shouldBeRemovedStartOfAction()) {
                remove.add(throwable);
            }
        }

        for (ThrowableState throwableState : remove) {
            gameState.getWorld().removeThrowbleState(throwableState);
        }
    }

    private void resetAttackingZombies(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {

            for (CharacterState charState : player.getTeam().listAllTypesCharacters()) {
                charState.resetAttackingZombies();
            }
        }
    }

    private void markDetections(GameState gameState, boolean isServer) {
        for (String characterId : charactersDetectedByPlayer_String_String.keySet()) {


            String detectedBy = charactersDetectedByPlayer_String_String.get(characterId);

            CharacterState detectedChar = gameState.findCharacterByIdentifier(characterId);
            detectedChar.addDetectedByPlayer(detectedBy);


            if (!isServer && gameState.getMe().getIdentifier().equals(detectedBy)) {
                EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Hidden enemy detected!", "" + detectedChar.getName() + " was detected. You can force reveal them as an action.", IconConstantsHelper.ICON_ID_DETECTED, detectedChar));
            }
        }

    }

    private void checkForDetection(GameState gameState) {

        String nextPlayerIdentifier = gameState.getPhase().getNextPlayer(gameState).getIdentifier();
        for (PlayerState player : gameState.getPlayers()) {

            if (player.getIdentifier().equals(nextPlayerIdentifier)) {
                continue;
            }


            Log.d("detection", "Detecting for player: " + player.getName());

            for (CharacterState charState : player.getTeam().listAllTypesCharacters()) {
                if (!charState.isHidden()) {
                    continue;
                }

                Log.d("detection", "Detecting character: " + charState.getName());

                for (String mapSection : charState.getRegions()) {

                    // find other characters in the same section
                    PlayerState player_ = gameState
                            .findPlayerByIdentifier(nextPlayerIdentifier);


                    for (CharacterState charState_ : player_.getTeam()
                            .listAllTypesCharacters()) {
                        if (charState_.isDead() || charState_.isUnconsious() || !charState_.isOnMap()) {
                            continue;
                        }
                        boolean isInSameSection = false;

                        for (String mapSection_ : charState_.getRegions()) {
                            if (mapSection.equals(mapSection_)) {

                                Log.d("detection", "Found another character in same map section character: " + charState_.getName());

                                isInSameSection = true;
                                break;
                            }
                        }

                        if (isInSameSection) {
                            boolean found = charState_.tryToDetect(charState
                                    .getCamoRating());

                            if (found) {

                                charactersDetectedByPlayer_String_String.put(
                                        charState.getIdentifier(),
                                        player_.getIdentifier());
                                break;
                            }
                        }

                    }

                }

            }
        }
    }

    private void advancePhase(GameState gameState) {
        PhaseState phase = gameState.getPhase();
        phase.setPrimaryPhase(PrimaryPhase.ACTION);
        phase.setSecondaryPhase(SecondaryPhase.WAITING_FOR_ACTION);

        String nextPlayerIdentifier = phase.getNextPlayer(gameState)
                .getIdentifier();
        phase.setCurrentPlayer(nextPlayerIdentifier);

        if (gameState.isFirstPlayer(nextPlayerIdentifier)) {
            phase.incrementGameTurn();


        }
    }

    private void prepareTeam(GameState gameState) {
        String currentPlayerIdentifier = gameState.getPhase()
                .getCurrentPlayer();
        TeamState team = gameState.findPlayerByIdentifier(
                currentPlayerIdentifier).getTeam();

        cleanupInactiveEffects(team, gameState);
        cleanupUnusedActionPoints(team);

        List<RegionState> regions = gameState.getMap().getRegionsWithoutSpecials();
        for (RegionState regionState : regions) {
            regionState.resetNoiseForPlayer(currentPlayerIdentifier);
        }

        for (CharacterState character : team.listAllTypesCharacters()) {
            character.setCurrentNoise(0);

            character.setActionPointsAssignedForMovement(character
                    .getRemainingActionPoints(gameState));

            character.setMovedOnMapThisTurn(false);

        }
    }

    private void cleanupInactiveEffects(TeamState team, GameState gameState) {
        List<CharacterState> characgters = team.listAllTypesCharacters();
        for (CharacterState playerCharacter : characgters) {
            List<CharacterEffect> effects = playerCharacter
                    .getCharacterEffects();

            ArrayList<CharacterEffect> effectsToRemove = new ArrayList<>();
            for (CharacterEffect characterEffect : effects) {
                if (!characterEffect.isActive(gameState.getPhase()
                        .getGameTurn())) {
                    effectsToRemove.add(characterEffect);
                }
            }

            for (CharacterEffect characterEffect : effectsToRemove) {
                playerCharacter.removeCharacterEffect(characterEffect);
            }
        }
    }

    private void cleanupUnusedActionPoints(TeamState team) {
        List<CharacterState> characgters = team.listAllTypesCharacters();
        for (CharacterState playerCharacter : characgters) {

            playerCharacter.resetUnusedActionPoints();
        }
    }
}
