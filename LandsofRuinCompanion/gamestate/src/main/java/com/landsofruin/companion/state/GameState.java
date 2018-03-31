package com.landsofruin.companion.state;


import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.scenario.PlayerRole;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Object describing the current state of a game.
 */
@ObjectiveCName("GameState")
public class GameState {

    public static final int GAME_MODE_BASIC = 0;
    public static final int GAME_MODE_ADVANCED = 1;
    public static final int GAME_MODE_ONLINE = 2;


    public static final int VERSION = 1;

    private GameRuleConfigState gameRuleConfigState = new GameRuleConfigState();

    private String firebaseIdentifier;
    private String identifier;
    private String title;
    private int version;
    private boolean isServerSyncEnabled;
    private String serverIdentifier;
    private int transitionPosition;

    private boolean isTutorial;


    private List<PlayerState> players;
    private PhaseState phase;
    private WorldState world;
    private MapState map;
    private int scenario;

    private String battleReportServerId;


    private boolean phaseChangeUndoEnabled = false;
    private boolean phaseChangeUndoInEffect = false;
    private boolean phaseChangeUndoRequested = false;
    private String playerRequestingPhaseChangeUndo = null;
    private List<String> playerApprovedPhaseChangeUndo = new ArrayList<>();

    private int gameMode = GAME_MODE_ADVANCED;


    private transient LinkedList<CharacterState> temporaryCharactersForSetup = new LinkedList<>();

    public GameState() {
        this(UUID.randomUUID().toString(), "unnamed game");
    }

    public GameState(String identifier, String title) {
        this.identifier = identifier;
        this.title = title;
        this.version = VERSION;
        this.players = new ArrayList<>();

        this.phase = new PhaseState();
        this.world = new WorldState();
        this.transitionPosition = 0;
    }

    public void updateFrom(GameState currentGameState) {
        title = currentGameState.title;
        map = currentGameState.map;
        players = currentGameState.players;
        phase = currentGameState.phase;
        world = currentGameState.world;

        scenario = currentGameState.scenario;
        gameMode = currentGameState.gameMode;

        isServerSyncEnabled = currentGameState.isServerSyncEnabled;
        serverIdentifier = currentGameState.serverIdentifier;
        transitionPosition = currentGameState.transitionPosition;

        firebaseIdentifier = currentGameState.firebaseIdentifier;
    }

    public boolean isServerSyncEnabled() {
        return isServerSyncEnabled;
    }

    public void setServerSyncEnabled(boolean serverSyncEnabled) {
        this.isServerSyncEnabled = serverSyncEnabled;
    }

    public String getServerIdentifier() {
        return serverIdentifier;
    }

    public void setServerIdentifier(String serverIdentifier) {
        this.serverIdentifier = serverIdentifier;
    }

    public int nextTransitionPosition() {
        return ++transitionPosition;
    }

    public boolean needsSyncTokens() {
        for (PlayerState player : players) {
            if (player.hasServerAccount() && !player.hasSyncToken()) {
                return true;
            }
        }

        return false;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MapState getMap() {
        return map;
    }

    public void setMap(MapState map) {
        this.map = map;
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerState> players) {
        this.players = players;
    }


    public PhaseState getPhase() {
        return phase;
    }

    public WorldState getWorld() {
        return world;
    }


    public int getScenario() {
        return this.scenario;
    }

    public void setScenario(int scenarioId) {
        this.scenario = scenarioId;
    }

    @Exclude
    private int getPlayerCountForRole(int id) {
        int ret = 0;

        for (PlayerState player : players) {
            if (player.getScenarioPlayerRole() == id) {
                ++ret;
            }
        }

        return ret;

    }

    @Exclude
    public String getScenarioReadyValidationMessage() {
        Scenario scenario_ = LookupHelper.getInstance().getScenarioFor(this.getScenario());
        if (scenario_ == null) {
            return "No scenario selected";
        }


        StringBuffer sb = new StringBuffer();
        for (int roleId : scenario_.getPlayerRoles()) {
            PlayerRole role = LookupHelper.getInstance().getPlayerRoleFor(roleId);
            int playersInThisRole = getPlayerCountForRole(role.getId());

            if (role.getMaxCount() != PlayerRole.MAX_COUNT_UNLIMITED) {
                if (playersInThisRole > role.getMaxCount()) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append("Too many players in role " + role.getName() + ". Currently: " + playersInThisRole + " allowed maximum: " + role.getMaxCount());

                }
            }
            if (playersInThisRole < role.getMinCount()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append("Too few players in role " + role.getName() + ". Currently: " + playersInThisRole + " minimum: " + role.getMinCount());

            }


        }

        if (sb.length() <= 0) {
            return null;
        }

        return sb.toString();


    }

    @Exclude
    public boolean isScenarioPlayerRolesReady() {
        Scenario scenario_ = LookupHelper.getInstance().getScenarioFor(this.getScenario());
        if (scenario_ == null) {
            return false;
        }

        for (int roleId : scenario_.getPlayerRoles()) {
            PlayerRole role = LookupHelper.getInstance().getPlayerRoleFor(roleId);
            int playersInThisRole = getPlayerCountForRole(role.getId());

            if (role.getMaxCount() != PlayerRole.MAX_COUNT_UNLIMITED) {
                if (playersInThisRole > role.getMaxCount()) {
                    return false;
                }
            }
            if (playersInThisRole < role.getMinCount()) {
                return false;
            }


        }


        return true;
    }

    @Exclude
    public boolean isReady() {


        if (map.getRegionsWithoutSpecials().size() == 0) {
            return false;
        }

        return isPlayerPositionRoleReady();
    }

    @Exclude
    public boolean isPlayerPositionRoleReady() {
        for (PlayerState player : players) {
            if (!player.isConnected() || !player.isReady()) {
                return false;
            }

            if (player.getTeam() == null
                    || player.getTeam().listAllTypesCharacters().size() == 0) {
                return false;
            }

            if (player.getStartingPosition() == null) {
                return false;
            }


            if (player.getScenarioPlayerRole() == -1) {
                return false;
            }
        }

        return isScenarioPlayerRolesReady();
    }

    @Exclude
    public PlayerState getMe() {
        for (PlayerState player : players) {
            if (player.isMe()) {
                return player;
            }
        }

        return null;
    }

    @Exclude
    public List<CharacterState> getOwnCharacters() {
        List<CharacterState> characters = new LinkedList<>();

        PlayerState player = getMe();
        if (player != null && player.getTeam() != null) {
            characters.addAll(player.getTeam().listAllTypesCharacters());
        }

        return characters;
    }

    @Exclude
    public List<CharacterState> getEnemyCharacters() {
        List<CharacterState> characters = new LinkedList<>();

        for (PlayerState player : players) {
            if (!player.isMe() && player.getTeam() != null) {

                List<CharacterState> oneTeamCharacters = new LinkedList<>();
                oneTeamCharacters.addAll(player.getTeam().listAllTypesCharacters());

                Collections.sort(oneTeamCharacters, new Comparator<CharacterState>() {
                    @Override
                    public int compare(CharacterState characterState, CharacterState characterState2) {
                        return characterState.getName().compareTo(characterState2.getName());
                    }
                });

                characters.addAll(oneTeamCharacters);
            }
        }

        return characters;
    }

    public CharacterState findCharacterByIdentifier(String identifier) {
        CharacterState character = null;


        for (PlayerState player : players) {
            if (player.getTeam() == null) {
                continue;
            }
            for (CharacterState currentCharacter : player.getTeam()
                    .listAllTypesCharacters()) {
                if (currentCharacter.getIdentifier().equals(identifier)) {
                    character = currentCharacter;

                    break;
                }
            }
        }

        return character;
    }

    public TeamState findTeamByCharacterIdentifier(String characterIdentifier) {
        for (PlayerState player : players) {

            if (player.getTeam() == null) {
                continue;
            }
            for (CharacterState currentCharacter : player.getTeam()
                    .listAllTypesCharacters()) {
                if (currentCharacter.getIdentifier()
                        .equals(characterIdentifier)) {
                    return player.getTeam();
                }
            }
        }

        return null;
    }

    public PlayerState findPlayerByCharacterIdentifier(String characterIdentifier) {
        for (PlayerState player : players) {
            for (CharacterState currentCharacter : player.getTeam()
                    .listAllTypesCharacters()) {
                if (currentCharacter.getIdentifier()
                        .equals(characterIdentifier)) {
                    return player;
                }
            }
        }

        return null;
    }

    public PlayerState findPlayerByIdentifier(String identifier) {
        PlayerState player = null;

        for (PlayerState currentPlayer : players) {
            if (currentPlayer.getIdentifier().equals(identifier)) {
                player = currentPlayer;
                break;
            }
        }

        return player;
    }

    @Exclude
    public boolean isFirstPlayer(String identifier) {
        return players.get(0).getIdentifier().equals(identifier);
    }

    public List<CharacterState> findCharactersInRegion(String regionIdentifier) {
        List<CharacterState> characters = new ArrayList<CharacterState>();

        for (PlayerState player : players) {
            for (CharacterState character : player.getTeam().listAllTypesCharacters()) {
                if (character.isOnMap()
                        && character.getRegions().contains(regionIdentifier)) {
                    characters.add(character);
                }
            }
        }

        return characters;
    }

    public boolean isPhaseChangeUndoEnabled() {
        return phaseChangeUndoEnabled;
    }

    public void setPhaseChangeUndoEnabled(boolean phaseChangeUndoEnabled) {
        this.phaseChangeUndoEnabled = phaseChangeUndoEnabled;
    }

    public boolean isPhaseChangeUndoInEffect() {
        return phaseChangeUndoInEffect;
    }

    public void setPhaseChangeUndoInEffect(boolean phaseChangeUndoInEffect) {
        this.phaseChangeUndoInEffect = phaseChangeUndoInEffect;
    }


    public boolean isPhaseChangeUndoRequested() {
        return phaseChangeUndoRequested;
    }

    public void setPhaseChangeUndoRequested(boolean phaseChangeUndoRequested) {
        this.phaseChangeUndoRequested = phaseChangeUndoRequested;
    }

    public String getPlayerRequestingPhaseChangeUndo() {
        return playerRequestingPhaseChangeUndo;
    }

    public void setPlayerRequestingPhaseChangeUndo(String playerRequestingPhaseChangeUndo) {
        this.playerRequestingPhaseChangeUndo = playerRequestingPhaseChangeUndo;
    }

    public List<String> getPlayerApprovedPhaseChangeUndo() {
        return playerApprovedPhaseChangeUndo;
    }

    public void resetPlayerApprovedPhaseChangeUndo() {
        this.playerApprovedPhaseChangeUndo.clear();
    }

    @Exclude
    public void setPlayerApprovedPhaseChangeUndo(String playerApprovedPhaseChangeUndo, boolean approved) {
        if (approved) {
            if (!this.playerApprovedPhaseChangeUndo.contains(playerApprovedPhaseChangeUndo)) {
                this.playerApprovedPhaseChangeUndo.add(playerApprovedPhaseChangeUndo);
            }

        } else {
            if (this.playerApprovedPhaseChangeUndo.contains(playerApprovedPhaseChangeUndo)) {
                this.playerApprovedPhaseChangeUndo.remove(playerApprovedPhaseChangeUndo);
            }
        }
    }

    @Exclude
    public boolean isGameModeAdvanced() {
        return gameMode == GAME_MODE_ADVANCED || gameMode == GAME_MODE_ONLINE;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {

        if (gameMode != GAME_MODE_BASIC && gameMode != GAME_MODE_ADVANCED && gameMode != GAME_MODE_ONLINE) {
            throw new IllegalArgumentException("Use GAME_MODE_XXXXXXX constants to set this");
        }

        this.gameMode = gameMode;

        if (this.gameMode == GAME_MODE_ONLINE) {
            isServerSyncEnabled = true;
        } else {
            isServerSyncEnabled = false;
        }
    }


    public void addTemporaryCharactersForSetup(CharacterState characterState) {
        this.temporaryCharactersForSetup.add(characterState);
    }

    public void clearTemporaryCharactersForSetup() {
        this.temporaryCharactersForSetup.clear();
    }

    public CharacterState findTemporaryCharactersForSetup(String id) {
        for (CharacterState characterState : temporaryCharactersForSetup) {
            if (characterState.getIdentifier().equals(id)) {
                return characterState;
            }
        }

        return null;
    }

    public GameRuleConfigState getGameRuleConfigState() {
        return gameRuleConfigState;
    }

    public String getBattleReportServerId() {
        return battleReportServerId;
    }

    public void setBattleReportServerId(String battleReportServerId) {
        this.battleReportServerId = battleReportServerId;
    }

    public String getFirebaseIdentifier() {
        return firebaseIdentifier;
    }

    public void setFirebaseIdentifier(String firebaseIdentifier) {
        this.firebaseIdentifier = firebaseIdentifier;
    }

    public boolean isTutorial() {
        return isTutorial;
    }

    public void setTutorial(boolean tutorial) {
        isTutorial = tutorial;
    }
}
