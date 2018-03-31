package com.landsofruin.companion.state.transition;

import android.util.Log;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AnimationHolder;
import com.landsofruin.companion.state.AttackLogItem;
import com.landsofruin.companion.state.CharacterBattleLogState;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MultipleAnimationsHolder;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.TeamState;
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

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.landsofruin.companion.state.transition.Transition} to end the game
 */
@ObjectiveCName("EndGameConfirmationSetPlayerReadyTransition")
public class EndGameConfirmationSetPlayerReadyTransition implements Transition {

    private String playerId;
    private boolean ready;

    private ArrayList<Integer> rollResults_Integer;

    public EndGameConfirmationSetPlayerReadyTransition(String playerId, boolean ready) {
        this.playerId = playerId;
        this.ready = ready;
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
        gameState.findPlayerByIdentifier(this.playerId).getConfirmEndGameState().setPlayerReady(this.ready);

        performEndGameIfAllPlayersReady(gameState, isServer);
    }

    private void performEndGameIfAllPlayersReady(GameState game, boolean isServer) {


        for (PlayerState player : game.getPlayers()) {
            if (!player.getConfirmEndGameState().isPlayerReady()) {
                return;
            }
        }

        // all players want to end the game. Execute damage resolution.


        applyDamageAndEffects(game, isServer);
        String currentPlayerIdentifier = game.getPhase().getCurrentPlayer();
        TeamState team = game.findPlayerByIdentifier(currentPlayerIdentifier).getTeam();
        for (CharacterState pc : team.listAllTypesCharacters()) {
            pc.clearUnresolvedHits();
        }

        for (PlayerState player : game.getPlayers()) {
            player.startPrepareEndGameState();
        }

    }


    private void applyDamageAndEffects(GameState gameState, boolean isServer) {
        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        PlayerState player = gameState.findPlayerByIdentifier(currentPlayerIdentifier);
        TeamState team = player.getTeam();


        MultipleAnimationsHolder animationsHolder = null;
        if (!isServer) {
            animationsHolder = new MultipleAnimationsHolder(true);
        }


        boolean useRolls = false;
        int rollIndex = 0;

        if (rollResults_Integer == null) {
            rollResults_Integer = new ArrayList<Integer>();
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

            ArrayList<String> publicDamageLog = new ArrayList<String>();
            ArrayList<String> privateDamageLog = new ArrayList<String>();

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
                        privateDamageLog.add("Killed by while unconscious");
                        publicDamageLog.add("Down");

                        if (animationsHolder != null) {
                            animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), newEffect.getIcon()));
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
                    if(isServer) {
                        BattleReportLogger.getInstance().logAttackEvent(new AttackLogItem(gameState.getPhase().getGameTurn() - 1,
                                pc.getIdentifier(), pc.getProfilePictureUri(),true, unresolvedHit.getSourceWargearId(), damageInt, unresolvedHit.getRange(), unresolvedHit.isHardCover(), unresolvedHit.isSoftCover(), unresolvedHit.isAttackOfOpportunity()), shooter, gameState);
                    }

                }
                if (unresolvedHit.getType() == UnresolvedHit.TYPE_SHOOTING) {
                    damage = LookupHelper.getInstance().getCCDamageLine(damageInt);
                } else {
                    damage = LookupHelper.getInstance().getCCDamageLine(damageInt);
                }

                team.addNegativePsychologyEffect(damage.getPsychologyEffect());

//                pc.addLatestDamageLine(new DamageLineState(damage
//                        .getDamageType(), damage.getDiceRoll()));
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

                    } else {
                        Log.w("damage", "Damage effect for id " + addEffect
                                + " was not found and is being ignoerd");
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

                        } else {
                            Log.w("damage", "Damage effect for id " + effect
                                    + " was not found and is being ignoerd");
                        }
                    }
                }

                privateDamageLog.add(damage.getEffectText());
                publicDamageLog.add(damage.getPublicEffectText());

            }


            List<String> effectChanges = pc.getEffectChangesLastTurn();
            if (effectChanges.size() > 0) {

            }


            if (characterIsBleedingAtStart && !pc.isBleeding()) {
                if (animationsHolder != null) {
                    animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_BLEEDING_STOPS));
                }
            }

            if (characterIsDownAtStart && !pc.isDown()) {

                //character did get up!
                if (animationsHolder != null) {
                    animationsHolder.addOneAnimationEffectHolder(new AnimationHolder(pc.getIdentifier(), IconConstantsHelper.ICON_ID_NOLONGER_DOWN));
                }
            }
        }
        if (animationsHolder != null) {
            EventsHelper.getInstance().queueAnimations(animationsHolder);
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


}
