package com.landsofruin.companion.state.transition;

import android.util.Log;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.AttackLogItem;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.dice.DiceUtils;
import com.landsofruin.companion.state.gameruleobjects.characterdata.CharacterStatModifier;
import com.landsofruin.companion.state.gameruleobjects.characterdata.UnresolvedHit;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.damage.DamageLine;
import com.landsofruin.companion.state.gameruleobjects.damage.StatModifier;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;
import com.landsofruin.companion.state.tutorial.TutorialConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Advance to {@link PrimaryPhase#DAMAGE_RESOLUTION}.
 */
@ObjectiveCName("DamageResolutionPhaseTransition")
public class DamageResolutionPhaseTransition implements Transition {
    private ArrayList<Integer> rollResults_Integer;

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
        event.setEventTitle("Command Phase");
        event.setEventType(BattleReportEvent.TYPE_PHASE_CHANGE);
        BattleReportLogger.getInstance().logEvent(event, gameState);
    }


    private void setLeaders(GameState gameState) {

        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        TeamState team = gameState.findPlayerByIdentifier(currentPlayerIdentifier).getTeam();

        team.setCharacterInCommandId(null);
        team.setCharacterSecondInCommandId(null);

        for (CharacterState pc : team.getHeroes()) {
            if (pc.isUnconsious() || pc.isDead()) {
                continue;
            }

            if (team.getCharacterInCommandId() == null) {
                team.setCharacterInCommandId(pc.getIdentifier());
                continue;
            }

            CharacterState previousLeader = gameState.findCharacterByIdentifier(team.getCharacterInCommandId());


            if (previousLeader.getLeadership() < pc.getLeadership()) {
                team.setCharacterInCommandId(pc.getIdentifier());

                pc = previousLeader;
            }


            if (team.getCharacterSecondInCommandId() == null) {
                team.setCharacterSecondInCommandId(pc.getIdentifier());
                continue;
            }


            CharacterState previousSecondInCommand = gameState.findCharacterByIdentifier(team.getCharacterInCommandId());


            if (previousSecondInCommand.getLeadership() < pc.getLeadership()) {
                team.setCharacterSecondInCommandId(pc.getIdentifier());
            }


        }


    }


    private void addRestOfTheNoise(GameState gameState) {
        TeamState team = gameState.findPlayerByIdentifier(gameState.getPhase().getCurrentPlayer()).getTeam();

        for (CharacterState pc : team.listAllTypesCharacters()) {
            if (!pc.isOnMap()) {
                continue;
            }

            for (String regionIdentifier : pc.getRegions()) {
                RegionState region = gameState.getMap()
                        .findRegionByIdentifier(regionIdentifier);
                region.addNoiseForPlayer(gameState.getPhase()
                        .getCurrentPlayer(), pc.getCurrentNoise() / 2);
            }
        }


    }

    private void trigger(GameState gameState, boolean isServer) {
        gameState.setPhaseChangeUndoEnabled(false);
        simulatePsychology(gameState, isServer);
        addRestOfTheNoise(gameState);
        applyDamageAndEffects(gameState, isServer);
        resetTeamSuppression(gameState);

        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        TeamState team = gameState.findPlayerByIdentifier(currentPlayerIdentifier).getTeam();
        for (CharacterState pc : team.listAllTypesCharacters()) {
            pc.clearUnresolvedHits();
        }
        setLeaders(gameState);
        advanceToPlanningPhase(gameState);
        clearTeamStates(gameState);
        handlePostResolutionEffects(gameState, isServer);

    }


    private void resetTeamSuppression(GameState gameState) {
        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        TeamState team = gameState.findPlayerByIdentifier(currentPlayerIdentifier).getTeam();

        for (CharacterState pc : team.listAllTypesCharacters()) {
            pc.resetSuppression();
        }
    }


    private void handlePostResolutionEffects(GameState gameState, boolean isServer) {
        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        PlayerState player = gameState.findPlayerByIdentifier(currentPlayerIdentifier);
        TeamState team = player.getTeam();


        for (CharacterState pc : team.listAllTypesCharacters()) {

            if (pc.isDead()) {
                continue;
            }
            for (CharacterEffect effect : pc.getCharacterEffects()) {

                String effectResult = effect.handlePostResolution(gameState, pc, isServer);
            }
        }
    }


    private void applyDamageAndEffects(GameState gameState, boolean isServer) {
        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        PlayerState player = gameState.findPlayerByIdentifier(currentPlayerIdentifier);
        TeamState team = player.getTeam();


        MultipleAnimationsHolder animationsHolder = null;
        if (!isServer && player.isMe()) {
            animationsHolder = new MultipleAnimationsHolder(true);
        }


        MultipleAnimationsHolder otherPlayerAnimationsHolder = null;
        if (!isServer && !player.isMe()) {
            otherPlayerAnimationsHolder = new MultipleAnimationsHolder(true);
        }

        boolean useRolls = false;
        int rollIndex = 0;

        if (rollResults_Integer == null) {
            rollResults_Integer = new ArrayList<>();
        } else {
            useRolls = true;
        }

        for (CharacterState pc : team.listAllTypesCharacters()) {


            boolean characterIsDownAtStart = pc.isDown();
            boolean characterIsBleedingAtStart = pc.isBleeding();


            pc.clearEffectChangesLastTurn();

            for (CharacterEffect effect : pc.getCharacterEffects()) {
                int roll;

                if (useRolls) {
                    roll = this.rollResults_Integer.get(rollIndex++);
                } else {
                    roll = DiceUtils.rollDie(2);
                    this.rollResults_Integer.add(roll);
                }

                String effectResult = effect.advanceOneTurn(gameState, pc, roll);
                if (effectResult != null) {
                    pc.addEffectChangeLastTurn(effectResult);
                }
            }

            cleanupInactiveEffects(team, gameState);

            boolean isUnconsious = false;

            for (CharacterEffect characterEffect : pc.getCharacterEffects()) {
                if (characterEffect.getId() == CharacterEffect.ID_UNCONSCIOUS) {
                    isUnconsious = true;
                }
            }
            if (isUnconsious) {

                List<UnresolvedHit> hits = pc.getUnresolvedHits();
                for (UnresolvedHit unresolvedHit : hits) {
                    if (unresolvedHit.getType() == UnresolvedHit.TYPE_CC) {
                        // kill a character that is unconscious and receives a
                        // CC hit

                        CharacterEffect newEffect = CharacterEffectFactory
                                .createCharacterEffect(
                                        CharacterEffect.ID_DEAD,
                                        gameState.getPhase().getGameTurn());
                        pc.addCharacterEffect(newEffect);

                        if (animationsHolder != null) {
                            animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), newEffect.getIcon()));
                        }

                        if (otherPlayerAnimationsHolder != null) {
                            otherPlayerAnimationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_DOWN));
                        }


                        if (!isServer) {
                            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Character was killed while unconscious", "" + pc.getName() + " is now dead.", IconConstantsHelper.getInstance().getIconIdForEffect(CharacterEffect.ID_DEAD), pc));
                        }

                        break;
                    }
                }

            }

            List<UnresolvedHit> hits = pc.getUnresolvedHits();
            for (UnresolvedHit unresolvedHit : hits) {
                int roll;

                if (useRolls) {
                    roll = this.rollResults_Integer.get(rollIndex++);
                } else {
                    roll = DiceUtils.rollDie(100);
                    this.rollResults_Integer.add(roll);
                }


                DamageLine damage;
                int damageInt = roll + (unresolvedHit.getExtraHits() * 10);
                if (damageInt < 1) {
                    damageInt = 1;
                }


                CharacterState shooter = gameState.findCharacterByIdentifier(unresolvedHit.getSourceCharacterId());
                //shooter is null for Zombie hits. So no problem.
                if (shooter != null) {
                    if (isServer) {
                        BattleReportLogger.getInstance().logAttackEvent(new AttackLogItem(gameState.getPhase().getGameTurn() - 1,
                                pc.getIdentifier(), pc.getProfilePictureUri(), true, unresolvedHit.getSourceWargearId(), damageInt, unresolvedHit.getRange(), unresolvedHit.isHardCover(), unresolvedHit.isSoftCover(), unresolvedHit.isAttackOfOpportunity()), shooter, gameState);
                    }

                }
                if (unresolvedHit.getType() == UnresolvedHit.TYPE_SHOOTING) {
                    damage = LookupHelper.getInstance().getShootingDamageLine(
                            damageInt);
                } else {
                    damage = LookupHelper.getInstance().getCCDamageLine(
                            damageInt);
                }

                team.addNegativePsychologyEffect(damage.getPsychologyEffect());

                pc.addAllModifiers(createCharacterModifiersFrom(damage, gameState));
                for (int addEffect : damage.getAddsEffects()) {

                    CharacterEffect newEffect = CharacterEffectFactory
                            .createCharacterEffect(addEffect, gameState
                                    .getPhase().getGameTurn());
                    if (newEffect != null) {
                        pc.addCharacterEffect(newEffect);

                        if (animationsHolder != null) {
                            animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), newEffect.getIcon()));
                        }

                        if (newEffect.getId() == CharacterEffect.ID_PINNED || newEffect.getId() == CharacterEffect.ID_UNCONSCIOUS || newEffect.getId() == CharacterEffect.ID_DEAD) {


                            if (otherPlayerAnimationsHolder != null) {
                                otherPlayerAnimationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_NOLONGER_DOWN));
                            }

                        }


                    } else {
                        Log.w("damage", "Damage effect for id " + addEffect
                                + " was not found and is being ignoerd");
                    }


                }

                if (!isServer) {
                    if (player.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(pc.getName() + " - " + damage.getPrivateEffectText(), "Roll: " + roll + " +" + (unresolvedHit.getExtraHits() * 10) + " =" + damageInt + "\n" + damage.getEffectText() + "\nNegative morale added due to damage: " + damage.getPsychologyEffect(), IconConstantsHelper.ICON_ID_NEW_HIT, pc));
                    } else {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(pc.getName() + " - " + damage.getPublicEffectText(), "", IconConstantsHelper.ICON_ID_NEW_HIT, pc));
                    }
                }


                if (unresolvedHit.getAddsEffect() != null) {
                    for (int effect : unresolvedHit.getAddsEffect()) {
                        CharacterEffect newEffect = CharacterEffectFactory
                                .createCharacterEffect(effect, gameState
                                        .getPhase().getGameTurn());
                        if (newEffect != null) {
                            pc.addCharacterEffect(newEffect);

                            if (animationsHolder != null) {
                                animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), newEffect.getIcon()));
                            }
                            if (newEffect.getId() == CharacterEffect.ID_PINNED || newEffect.getId() == CharacterEffect.ID_UNCONSCIOUS || newEffect.getId() == CharacterEffect.ID_DEAD) {


                                if (otherPlayerAnimationsHolder != null) {
                                    otherPlayerAnimationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_NOLONGER_DOWN));
                                }


                                if (!isServer) {
                                    if (!player.isMe()) {
                                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Character is Down", "" + pc.getName() + " is now Down.", IconConstantsHelper.ICON_ID_DOWN, pc));
                                    }
                                }
                            }

                            if (!isServer) {
                                if (player.isMe()) {
                                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("New character effect", "" + pc.getName() + " is now " + newEffect.getName(), IconConstantsHelper.getInstance().getIconIdForEffect(newEffect.getId()), pc));
                                }
                            }


                        } else {
                            Log.w("damage", "Damage effect for id " + effect
                                    + " was not found and is being ignoerd");
                        }
                    }
                }


            }

            if (characterIsBleedingAtStart && !pc.isBleeding()) {

                if (animationsHolder != null) {
                    animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_BLEEDING_STOPS));
                }

                if (!isServer) {
                    if (player.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem(pc.getName() + " - Bleeding stops", pc.getName() + " is no longer bleeding.", IconConstantsHelper.ICON_ID_BLEEDING_STOPS, pc));
                    }

                }

            }

            if (characterIsDownAtStart && !pc.isDown()) {
                //character did get up!
                if (animationsHolder != null) {
                    animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_NOLONGER_DOWN));
                }


                if (otherPlayerAnimationsHolder != null) {
                    otherPlayerAnimationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_NOLONGER_DOWN));
                }

                if (!isServer) {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Character got up", "" + pc.getName() + " is no longer Down", IconConstantsHelper.ICON_ID_NOLONGER_DOWN, pc));
                }
            }


        }


        if (animationsHolder != null) {
            EventsHelper.getInstance().queueAnimations(animationsHolder);
        }

        if (otherPlayerAnimationsHolder != null) {
            EventsHelper.getInstance().queueAnimations(otherPlayerAnimationsHolder);
        }
    }


    private ArrayList<CharacterStatModifier> createCharacterModifiersFrom(
            DamageLine damage, GameState gamestate) {
        ArrayList<CharacterStatModifier> ret = new ArrayList<>();

        ArrayList<StatModifier> mods = damage.getModifiers();
        for (StatModifier statModifier : mods) {
            int lastTurn = statModifier.getDuration();
            if (lastTurn == 0) {
                lastTurn = Integer.MAX_VALUE;
            } else {
                lastTurn = lastTurn + gamestate.getPhase().getGameTurn();
            }
            CharacterStatModifier modifier = new CharacterStatModifier(
                    "wounded", statModifier.getOffensiveModifier(),
                    statModifier.getDefensiveModifier(), lastTurn, damage.getLocations());
            ret.add(modifier);

        }

        return ret;
    }


    private void simulatePsychology(GameState gameState, boolean isServer) {
        String currentPlayerIdentifier = gameState.getPhase()
                .getCurrentPlayer();

        PlayerState currentPlayer = gameState.findPlayerByIdentifier(
                currentPlayerIdentifier);
        TeamState team = currentPlayer.getTeam();

        int highestLD = team.getCurrentLeaderLeadershipValue();

        int effect = team.getCurrentPositivePsychologyEffect()
                - team.getCurrentNegativePsychologyEffect();

        effect = effect + highestLD;


        int previousStatus = team.getTeamStatus(gameState);

        if (effect >= 0) {
            if (team.getPsychologyPool(gameState) <= 0) {
                if (previousStatus != TeamState.TEAM_STATUS_PANIC) {
                    if (!isServer && currentPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Team panicked", "Your team panicked. For the rest of the game you can't perform any actions.", IconConstantsHelper.ICON_ID_COMMAND_PHASE));
                    }
                }

                team.setTeamStatus(TeamState.TEAM_STATUS_PANIC);
            } else {


                if (previousStatus != TeamState.TEAM_STATUS_NORMAL) {
                    if (!isServer && currentPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Team morale normalised", "You can perform all actions again", IconConstantsHelper.ICON_ID_COMMAND_PHASE));
                    }
                }

                team.setTeamStatus(TeamState.TEAM_STATUS_NORMAL);
            }
        } else {
            if (gameState.getGameMode() != GameState.GAME_MODE_BASIC) {
                team.setPsychologyPool(team.getPsychologyPool(gameState) + effect);
            }

            if (team.getPsychologyPool(gameState) <= 0) {

                if (previousStatus != TeamState.TEAM_STATUS_PANIC) {
                    if (!isServer && currentPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Team panicked", "Your team panicked. For the rest of the game you can't perform any actions.", IconConstantsHelper.ICON_ID_COMMAND_PHASE));
                    }
                }


                team.setTeamStatus(TeamState.TEAM_STATUS_PANIC);
            } else {


                if (previousStatus != TeamState.TEAM_STATUS_CONFUSION) {
                    if (!isServer && currentPlayer.isMe()) {
                        EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Team confused", "Your team is confused. You cannot perform complex actions before your leader regains control.", IconConstantsHelper.ICON_ID_COMMAND_PHASE));
                    }
                }

                team.setTeamStatus(TeamState.TEAM_STATUS_CONFUSION);
            }
        }

        team.setLastTurnNegativePsychologyEffect(team.getCurrentNegativePsychologyEffect());
        team.setCurrentNegativePsychologyEffect(0);

        team.setLastTurnPositivePsychologyEffect(team.getCurrentPositivePsychologyEffect());
        team.setCurrentPositivePsychologyEffect(0);
    }

    private void cleanupInactiveEffects(TeamState team, GameState gameState) {
        List<CharacterState> characters = team.listAllTypesCharacters();
        for (CharacterState playerCharacter : characters) {
            List<CharacterEffect> effects = playerCharacter
                    .getCharacterEffects();

            boolean isDead = playerCharacter.isDead();
            boolean shouldRemoveFurtherDeadEffects = false; // to remove
            // multiple dead
            // effects

            ArrayList<CharacterEffect> effectsToRemove = new ArrayList<CharacterEffect>();
            for (CharacterEffect characterEffect : effects) {

                if (isDead) {
                    if (characterEffect.getId() == CharacterEffect.ID_DEAD) {
                        if (shouldRemoveFurtherDeadEffects) {
                            effectsToRemove.add(characterEffect);
                        } else {
                            shouldRemoveFurtherDeadEffects = true;
                        }
                    } else {
                        effectsToRemove.add(characterEffect);
                    }

                } else {

                    if (!characterEffect.isActive(gameState.getPhase()
                            .getGameTurn())) {
                        effectsToRemove.add(characterEffect);
                    }
                }
            }

            for (CharacterEffect characterEffect : effectsToRemove) {
                playerCharacter.removeCharacterEffect(characterEffect);
            }
        }
    }

    private void advanceToPlanningPhase(GameState gameState) {
        PhaseState phase = gameState.getPhase();
        phase.setPrimaryPhase(PrimaryPhase.ASSIGN_ACTIONS);
        phase.setSecondaryPhase(SecondaryPhase.NONE);
    }

    private void clearTeamStates(GameState gameState) {
        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        TeamState team = gameState.findPlayerByIdentifier(currentPlayerIdentifier).getTeam();

        for (CharacterState pc : team.listAllTypesCharacters()) {
            pc.clearActions();
        }
    }
}
