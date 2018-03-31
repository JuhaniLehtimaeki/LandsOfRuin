package com.landsofruin.companion.state.transition;

import android.util.Log;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameLogItem;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.WorldSection;
import com.landsofruin.companion.state.WorldState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.dice.DiceUtils;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Advance to {@link PrimaryPhase#ZOMBIES}.
 */
@ObjectiveCName("ZombiesPhaseTransition")
public class ZombiesPhaseTransition implements Transition {

    private String zombieRegion;
    private String zombieTargetCharacter;
    private boolean zombiePhaseSkipped = false;
    private HashMap<String, Integer> attackingZombies_String_Integer = new HashMap<>();
    private HashMap<String, Integer> detectedAttackingZombies_String_Integer = new HashMap<>();
    private HashMap<String, Integer> removeZombieSleepersPerArea_String_Integer = new HashMap<>();
    private List<List<WorldSection>> worldSections_WorldSection_WorldSection;
    private ArrayList<Integer> rolls_Integer_Integer;


    private void generateDiceRollReserve() {
        rolls_Integer_Integer = new ArrayList<>(100);
        for (int i = 0; i < 100; ++i) {
            rolls_Integer_Integer.add(DiceUtils.rollDie(100));
        }
    }

    @Override
    public boolean isRelevantForServerSync() {
        return true;
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        if (gameState.isPhaseChangeUndoInEffect()) {
            trigger(gameState, true);
            return;
        }


        this.zombiePhaseSkipped = skipThisZombiePhase(gameState);
        if (zombiePhaseSkipped) {
            zombieRegion = null;
            zombieTargetCharacter = null;

        } else {
            generateDiceRollReserve();


            this.worldSections_WorldSection_WorldSection = WorldState.tickWorldState(gameState, true);

            gameState.getWorld().setWorldSections(this.worldSections_WorldSection_WorldSection);

            WorldState world = gameState.getWorld();

            world.simulateZombies(gameState);


            zombieRegion = world.getZombieTargetRegion();
            zombieTargetCharacter = world.getZombieTargetCharacter();

            simulateSleeperZombies(gameState);


        }


        trigger(gameState, true);

        BattleReportEvent event = new BattleReportEvent();
        event.setEventTitle("Environment Started");
        event.setEventType(BattleReportEvent.TYPE_PHASE_CHANGE);
        BattleReportLogger.getInstance().logEvent(event, gameState);
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        gameState.getWorld().setWorldSections(this.worldSections_WorldSection_WorldSection);

        trigger(gameState, false);

        for (PlayerState player : gameState.getPlayers()) {

            for (CharacterState charState : player.getTeam().listAllTypesCharacters()) {
                int attackingRotters = charState.getAttackingZombies();
                int detectedRotters = charState.getAttackingZombiesDetected();


                if (attackingRotters > 0 || detectedRotters > 0) {

                    StringBuffer message = new StringBuffer();
                    if (attackingRotters > 0) {
                        message.append("Attacking Rotters: " + attackingRotters + " ");
                    }
                    if (detectedRotters > 0) {
                        message.append("Detected Rotters: " + detectedRotters);
                    }

                    EventsHelper.getInstance().addGameLogItemToLog(
                            new GameLogItem("Rotter attack! - " + charState.getName(), message.toString(),
                                    IconConstantsHelper.ICON_ID_ZOMBIE_SPAWN, charState));
                }
            }
        }

    }


    private boolean skipThisZombiePhase(GameState gameState) {
        int players = gameState.getPlayers().size();

        double chanceToSkip = (players - 2) / (double) players;
        Log.d("skip zombie phase", "Chance to skip zombie phase: "
                + chanceToSkip);

        if (chanceToSkip <= 0) {
            Log.d("skip zombie phase",
                    "Not skipping Zombie phase as chance is <= 0");
            return false;
        }

        double random = Math.random();
        if (random < chanceToSkip) {
            Log.d("skip zombie phase", "Skipping Zombie phase. Random number: "
                    + random);
            return true;
        }

        Log.d("skip zombie phase", "Not skipping Zombie phase. Random number: "
                + random);
        return false;

    }


    private void setAttackingZombies(GameState gameState) {

        for (PlayerState player : gameState.getPlayers()) {

            for (CharacterState charState : player.getTeam().listAllTypesCharacters()) {
                charState.resetAttackingZombies();

                if (attackingZombies_String_Integer.containsKey(charState.getIdentifier())) {
                    charState.setAttackingZombies(attackingZombies_String_Integer
                            .get(charState.getIdentifier()));
                }

                if (detectedAttackingZombies_String_Integer.containsKey(charState
                        .getIdentifier())) {
                    charState
                            .setAttackingZombiesDetected(detectedAttackingZombies_String_Integer
                                    .get(charState.getIdentifier()));
                }
            }

        }

        HashMap<String, Integer> sleeperZombies = gameState.getWorld()
                .getZombieSleepersPerArea();

        for (String section : removeZombieSleepersPerArea_String_Integer.keySet()) {

            Integer sleepers = sleeperZombies.get(section);
            Integer removeSleepers = removeZombieSleepersPerArea_String_Integer.get(section);

            if (removeSleepers == null || removeSleepers <= 0) {

            } else {

                if (sleepers == null || sleepers <= 0) {
                    sleeperZombies.remove(section);
                } else {

                    int zombiesLeft = sleepers - removeSleepers;
                    if (zombiesLeft <= 0) {
                        sleeperZombies.remove(section);
                    } else {
                        sleeperZombies.put(section, zombiesLeft);
                    }
                }
            }

        }
        removeZombieSleepersPerArea_String_Integer.clear();

    }

    private void simulateSleeperZombies(GameState gameState) {
        WorldState world = gameState.getWorld();

        for (String section : world.getZombieSleepersPerArea().keySet()) {

            int zombies = world.getZombieSleepersPerArea().get(section);
            if (zombies <= 0) {
                continue;
            }

            List<CharacterState> charactersInRegion = gameState
                    .findCharactersInRegion(section);
            if (charactersInRegion == null || charactersInRegion.size() <= 0) {
                continue;
            }

            int numberofAttackingZombies = 0;
            for (int i = 0; i < zombies; ++i) {
                if (DiceUtils.rollDie(6) <= 3) { // tweak this
                    CharacterState target = charactersInRegion.get(DiceUtils
                            .rollDie(charactersInRegion.size()) - 1);

                    boolean detected = target
                            .tryToDetect(Zombie.SLEEPER_ZOMBIE_CAMO_RATING);

                    if (!detected) {
                        Integer number = this.attackingZombies_String_Integer.get(target
                                .getIdentifier());
                        if (number == null) {
                            this.attackingZombies_String_Integer
                                    .put(target.getIdentifier(), 1);
                        } else {
                            this.attackingZombies_String_Integer.put(target.getIdentifier(),
                                    number + 1);
                        }
                    } else {
                        Integer number = this.detectedAttackingZombies_String_Integer.get(target
                                .getIdentifier());
                        if (number == null) {
                            this.detectedAttackingZombies_String_Integer
                                    .put(target.getIdentifier(), 1);
                        } else {
                            this.detectedAttackingZombies_String_Integer.put(target.getIdentifier(),
                                    number + 1);
                        }
                    }

                    ++numberofAttackingZombies;

                }
            }

            if (numberofAttackingZombies > 0) {

            }
            removeZombieSleepersPerArea_String_Integer.put(section, numberofAttackingZombies);

        }

    }

    private void trigger(GameState gameState, boolean isServer) {
        gameState.setPhaseChangeUndoEnabled(true);

        if (gameState.isPhaseChangeUndoInEffect()) {

            PhaseState phase = gameState.getPhase();
            phase.setPrimaryPhase(PrimaryPhase.ZOMBIES);
            phase.setSecondaryPhase(SecondaryPhase.NONE);

            gameState.setPhaseChangeUndoInEffect(false);
            return;
        }

        WorldState world = gameState.getWorld();
        world.setDiceRollReserveForZombieSpawn(this.rolls_Integer_Integer);

        if (zombiePhaseSkipped) {
            world.setZombieSpawnsToEmpty();
        } else {
            world.setZombieSpawnsFromWorldState();
        }

        world.setZombieTargetRegion(zombieRegion);
        world.setZombieTargetCharacter(zombieTargetCharacter);

        PhaseState phase = gameState.getPhase();
        phase.setPrimaryPhase(PrimaryPhase.ZOMBIES);
        phase.setSecondaryPhase(SecondaryPhase.NONE);


        if (zombiePhaseSkipped) {
            phase.setSecondaryPhase(SecondaryPhase.ZOMBIE_PHASE_SKIPPED);
        } else {
            int newZombies = 0;
            for (HashMap<String, Integer> spawned : world.getZombieSpawns()) {

                for (String key : spawned.keySet()) {
                    newZombies += spawned.get(key);
                }


            }

            world.setCurrentAmountOfZombies(world.getCurrentAmountOfZombies()
                    + newZombies);

            handleThrowables(world, gameState, isServer);
            setAttackingZombies(gameState);

        }

    }

    private void handleThrowables(WorldState world, GameState gameState, boolean isServer) {

        for (ThrowableState throwable : world.getThrowableStates()) {
            throwable.advanceTurn();
            WargearConsumable wargear = LookupHelper.getInstance().getWargearFor(throwable);


            if (!isServer) {
                if (!Wargear.THROWABLE_CATEGORY_ARTILLERY.equals(wargear.getCategory()) && throwable.getTemplateSize() > 0) {
                    EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Explosion diameter changed", wargear.getName() + " diameter is now " + throwable.getTemplateSize() + "\"", IconConstantsHelper.ICON_ID_ROLE_ICON_DEMOLITIONS));
                }
            }

            for (String regionIdentifier : throwable.getRegions()) {
                RegionState region = gameState.getMap().findRegionByIdentifier(
                        regionIdentifier);
                region.addNoiseForPlayer(gameState.getPhase()
                        .getCurrentPlayer(), throwable.getAddNoiseThisTurn());


                if (Wargear.THROWABLE_CATEGORY_ARTILLERY.equals(wargear.getCategory()) && throwable.getTemplateSize() == WargearConsumable.TEMPLATE_SIZE_SECTION) {
                    for (CharacterState characterState : gameState.findCharactersInRegion(regionIdentifier)) {
                        characterState.addZombieTurnHitWargearId(throwable.getWargearId());

                        int suppression = wargear.getSuppression();
                        characterState.addSuppression(suppression);

                        PlayerState player = gameState.findPlayerByCharacterIdentifier(characterState.getIdentifier());
                        if (!isServer && player.isMe()) {
                            EventsHelper.getInstance().addGameLogItemToLog(new GameLogItem("Artillery hit", "" + characterState.getName() + " was hit by a artillery. " + suppression + "% suppression added.", IconConstantsHelper.ICON_ID_NEW_HIT, characterState));
                        }
                    }
                }
            }


        }

    }

}
