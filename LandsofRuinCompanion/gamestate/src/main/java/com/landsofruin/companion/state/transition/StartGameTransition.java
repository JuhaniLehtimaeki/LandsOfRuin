package com.landsofruin.companion.state.transition;


import android.util.Log;
import android.util.SparseArray;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.TeamObjectiveState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.WorldSection;
import com.landsofruin.companion.state.WorldState;
import com.landsofruin.companion.state.battlereport.BattleReport;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.dice.DiceUtils;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link Transition} to start the game after the game setup phase has been
 * completed.
 */
@ObjectiveCName("StartGameTransition")
public class StartGameTransition implements Transition {
    private HashMap<String, List<TeamObjectiveState>> objectives_String_TeamObjectiveState;
    private List<Integer> playerOrder_Integer;
    private HashMap<String, HashMap<String, Integer>> zombiesPerPlayerPerArea_String_String_Integer;
    private HashMap<String, Integer> zombieSleepersPerArea_String_Integer;
    private List<List<WorldSection>> worldSections_WorldSection_WorldSection;

    public StartGameTransition() {
        playerOrder_Integer = new ArrayList<>();
    }

    private String battleReportId;

    @Override
    public boolean isRelevantForServerSync() {
        return true;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {

        gameState.getWorld().setWorldSections(worldSections_WorldSection_WorldSection);
        trigger(gameState);

        gameState.setBattleReportServerId(battleReportId);
        BattleReportLogger.getInstance().addBattleReportToUser(battleReportId);

    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {

        worldSections_WorldSection_WorldSection = WorldState.generateInitialWorldSections(gameState);
        gameState.getWorld().setWorldSections(worldSections_WorldSection_WorldSection);


        // this will put the zombies into mobs.
        for (int i = 0; i < 25; ++i) {
            worldSections_WorldSection_WorldSection = WorldState.tickWorldState(gameState, false);
            gameState.getWorld().setWorldSections(worldSections_WorldSection_WorldSection);
        }

        worldSections_WorldSection_WorldSection = WorldState.generateInitialAdditionalZombies(gameState);
        gameState.getWorld().setWorldSections(worldSections_WorldSection_WorldSection);


        createRamdomizedObjectives(gameState);
        createPlayerOrder(gameState);
        generatePreGameZombies(gameState);
        trigger(gameState);


        BattleReport battleReport = BattleReportLogger.getInstance().createNewBattleReport(gameState);
        gameState.setBattleReportServerId(battleReport.getId());

        battleReportId = battleReport.getId();

        BattleReportEvent event = new BattleReportEvent();
        event.setEventTitle("Game Started");
        BattleReportLogger.getInstance().logEvent(event, gameState);


    }

    private void trigger(GameState gameState) {
        gameState.setPhaseChangeUndoEnabled(false);
        reorderPlayers(gameState);

        PhaseState phase = gameState.getPhase();

        phase.setCurrentPlayer(gameState.getPlayers().get(0).getIdentifier());
        phase.setPrimaryPhase(PrimaryPhase.PRE_GAME);
        phase.setSecondaryPhase(SecondaryPhase.NONE);

        addObjectivesToTeams(gameState);

        gameState.getWorld().setPreGameZombiesPerPlayerPerArea(
                zombiesPerPlayerPerArea_String_String_Integer);

        gameState.getWorld().setZombieSleepersPerArea(zombieSleepersPerArea_String_Integer);

        for (PlayerState player : gameState.getPlayers()) {

            for (CharacterState character : player.getTeam().listAllTypesCharacters()) {
                character.clearActions();
            }
        }

        setLeaders(gameState);
    }


    private void setLeaders(GameState gameState) {

        String currentPlayerIdentifier = gameState.getPhase().getCurrentPlayer();
        TeamState team = gameState.findPlayerByIdentifier(currentPlayerIdentifier).getTeam();

        team.setCharacterInCommandId(null);
        team.setCharacterSecondInCommandId(null);

        for (CharacterState pc : team.listAllTypesCharacters()) {
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


            if (team.getCharacterInCommandId() == null) {
                team.setCharacterSecondInCommandId(pc.getIdentifier());
                continue;
            }


            CharacterState previousSecondInCommand = gameState.findCharacterByIdentifier(team.getCharacterInCommandId());


            if (previousSecondInCommand.getLeadership() < pc.getLeadership()) {
                team.setCharacterSecondInCommandId(pc.getIdentifier());
            }


        }


    }

    private void createPlayerOrder(GameState gameState) {

        SparseArray<LinkedList<Integer>> playerPriorities = new SparseArray<LinkedList<Integer>>();
        int index = 0;
        for (PlayerState player : gameState.getPlayers()) {

            int priority = LookupHelper.getInstance().getPlayerRoleFor(player.getScenarioPlayerRole()).getFirstTurnPriority();
            LinkedList<Integer> priorityList = playerPriorities.get(priority);
            if (priorityList == null) {
                priorityList = new LinkedList<>();
                playerPriorities.put(priority, priorityList);
            }
            priorityList.add(index);
            ++index;
        }

        for (int i = playerPriorities.size() - 1; i >= 0; --i) {

            List<Integer> playersToSelect = playerPriorities.get(playerPriorities.keyAt(i));

            while (!playersToSelect.isEmpty()) {
                int position = DiceUtils.rollDie(playersToSelect.size()) - 1;
                int player = playersToSelect.remove(position);
                playerOrder_Integer.add(player);
            }
        }

    }

    private void reorderPlayers(GameState gameState) {
        List<PlayerState> players = new ArrayList<PlayerState>();

        for (Integer position : playerOrder_Integer) {
            players.add(gameState.getPlayers().get(position));
        }

        gameState.setPlayers(players);
    }

    private void createRamdomizedObjectives(GameState gameState) {
        objectives_String_TeamObjectiveState = new HashMap<>();

        for (PlayerState player : gameState.getPlayers()) {

            LinkedList<TeamObjectiveState> objectivesForPlayer = new LinkedList<>();
            objectives_String_TeamObjectiveState.put(player.getIdentifier(), objectivesForPlayer);

            // map target
            List<RegionState> regions = gameState.getMap().getRegionsWithoutSpecials();
            RegionState objectiveSection = regions.get(DiceUtils
                    .rollDie(regions.size()) - 1);

            objectivesForPlayer.add(new TeamObjectiveState(
                    "Scavenge map section " + objectiveSection.getLabel(), ""));

            // kill target
            List<CharacterState> ownCharacters = player.getTeam()
                    .listAllTypesCharacters();
            CharacterState sourceCharacter = ownCharacters.get(DiceUtils
                    .rollDie(ownCharacters.size()) - 1);

            List<CharacterState> targets = new LinkedList<CharacterState>();

            for (PlayerState otherplayer : gameState.getPlayers()) {
                if (player.getIdentifier().equals(otherplayer.getIdentifier())) {
                    continue;
                }
                targets.addAll(otherplayer.getTeam().listAllTypesCharacters());

            }

            if (targets.size() > 0) {
                CharacterState targetCharacter = targets.get(DiceUtils
                        .rollDie(targets.size() - 1));
                objectivesForPlayer.add(new TeamObjectiveState("Kill "
                        + targetCharacter.getName() + " with "
                        + sourceCharacter.getName(), ""));
            }
        }
    }

    private void addObjectivesToTeams(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {
            List<TeamObjectiveState> objectivesForPlayer = objectives_String_TeamObjectiveState
                    .get(player.getIdentifier());
            for (TeamObjectiveState teamObjectiveState : objectivesForPlayer) {
                player.getTeam().addTeamObjective(teamObjectiveState);
            }
        }
    }

    private void generatePreGameZombies(GameState gameState) {

        int numberOfZombies = gameState.getWorld().getZombiesInTableArea();


        int numberOfSleeperZombies = 0;


        for (int i = 0; i < numberOfZombies; ++i) {
            if (DiceUtils.rollDie(2) == 2) {
                ++numberOfSleeperZombies;
            }
        }

        numberOfZombies = numberOfZombies - numberOfSleeperZombies;

        Log.e("pregame", "Pre-game zombies: " + numberOfZombies + " sleepers: "
                + numberOfSleeperZombies);

        zombiesPerPlayerPerArea_String_String_Integer = new HashMap<>();

        int numberOfPLayer = gameState.getPlayers().size();

        for (PlayerState player : gameState.getPlayers()) {
            zombiesPerPlayerPerArea_String_String_Integer.put(player.getIdentifier(),
                    new HashMap<String, Integer>());
        }

        gameState.getWorld().setCurrentAmountOfZombies(numberOfZombies);

        List<RegionState> regions = gameState.getMap().getRegionsWithoutSpecials();

        for (int i = 0; i < numberOfZombies; i++) {
            String toPlayer = gameState.getPlayers()
                    .get(DiceUtils.rollDie(numberOfPLayer) - 1).getIdentifier();

            HashMap<String, Integer> map = zombiesPerPlayerPerArea_String_String_Integer
                    .get(toPlayer);
            if (map == null) {
                map = new HashMap<>();
                zombiesPerPlayerPerArea_String_String_Integer.put(toPlayer, map);
            }

            String targetRegion = regions.get(
                    DiceUtils.rollDie(regions.size()) - 1).getIdentifier();

            Integer integer = map.get(targetRegion);
            if (integer == null) {
                map.put(targetRegion, 1);
            } else {
                map.put(targetRegion, integer + 1);
            }
        }

        zombieSleepersPerArea_String_Integer = new HashMap<>();
        for (int i = 0; i < numberOfSleeperZombies; i++) {

            String targetRegion = regions.get(
                    DiceUtils.rollDie(regions.size()) - 1).getIdentifier();

            Integer integer = zombieSleepersPerArea_String_Integer.get(targetRegion);
            if (integer == null) {
                zombieSleepersPerArea_String_Integer.put(targetRegion, 1);
            } else {
                zombieSleepersPerArea_String_Integer.put(targetRegion, integer + 1);
            }

        }
    }

}
