package com.landsofruin.companion.state;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

@ObjectiveCName("TeamState")
public class TeamState {
    public static final int TEAM_STATUS_NORMAL = 3;
    private int teamStatus = TEAM_STATUS_NORMAL;
    public static final int TEAM_STATUS_CONFUSION = 2;
    public static final int TEAM_STATUS_PANIC = 1;
    private String tribeIdentifier;
    private String tribeName;
    private List<CharacterStateHero> heroes;
    private List<CharacterStateSquad> squads;
    private List<TeamObjectiveState> teamObjectives = new ArrayList<>();
    private int currentPositivePsychologyEffect;
    private int currentNegativePsychologyEffect;
    private int lastTurnPositivePsychologyEffect = 0;
    private int lastTurnNegativePsychologyEffect = 0;
    private int psychologyPool = GameConstants.FULL_MORALE_POOL;
    private String characterInCommandId;
    private String characterSecondInCommandId;
    private String smallTribeLogoUri = "http://landsofruin.com/app_assets/reclaimers_large.png";
    private String largeTribeLogoUri = "http://landsofruin.com/app_assets/reclaimers_large.png";

    @Exclude
    private transient ArrayList<CharacterState> allCharactersWithSquads;


    /**
     * required empty constructor for firebase. don't use in code!
     */
    public TeamState(){
        this.heroes = new ArrayList<>();
        this.squads = new ArrayList<>();
    }

    public TeamState(String tribeIdentifier, String smallTribeLogoUri, String largeTribeLogoUri) {
        this.heroes = new ArrayList<>();
        this.squads = new ArrayList<>();
        this.tribeIdentifier = tribeIdentifier;
        this.smallTribeLogoUri = smallTribeLogoUri;
        this.largeTribeLogoUri = largeTribeLogoUri;
    }


    public String getTribeName() {
        return tribeName;
    }

    public void setTribeName(String tribeName) {
        this.tribeName = tribeName;
    }

    public String getTribeIdentifier() {
        return tribeIdentifier;
    }


    public List<CharacterStateHero> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<CharacterStateHero> characters) {
        this.heroes = characters;
        allCharactersWithSquads = null;
    }

    public int getCurrentPositivePsychologyEffect() {
        return currentPositivePsychologyEffect;
    }

    public void setCurrentPositivePsychologyEffect(int currentPositivePsychologyEffect) {
        this.currentPositivePsychologyEffect = currentPositivePsychologyEffect;
    }

    public void addPositivePsychologyEffect(int positivePsychologyEffect) {
        this.currentPositivePsychologyEffect += positivePsychologyEffect;
    }

    public int getCurrentNegativePsychologyEffect() {
        return currentNegativePsychologyEffect;
    }

    public void setCurrentNegativePsychologyEffect(int currentNegativePsychologyEffect) {
        this.currentNegativePsychologyEffect = currentNegativePsychologyEffect;
    }

    public void addNegativePsychologyEffect(int negativePsychologyEffect) {
        this.currentNegativePsychologyEffect += negativePsychologyEffect;
    }

    public int getLastTurnNegativePsychologyEffect() {
        return lastTurnNegativePsychologyEffect;
    }

    public void setLastTurnNegativePsychologyEffect(int lastTurnNegativePsychologyEffect) {
        this.lastTurnNegativePsychologyEffect = lastTurnNegativePsychologyEffect;
    }

    public int getLastTurnPositivePsychologyEffect() {
        return lastTurnPositivePsychologyEffect;
    }

    public void setLastTurnPositivePsychologyEffect(int lastTurnPositivePsychologyEffect) {
        this.lastTurnPositivePsychologyEffect = lastTurnPositivePsychologyEffect;
    }

    public int getPsychologyPool(GameState game) {
        // in basic mode ignore morale
        if (game.getGameMode() == GameState.GAME_MODE_BASIC) {
            return GameConstants.FULL_MORALE_POOL;
        }

        return psychologyPool;
    }

    public void setPsychologyPool(int psychologyPool) {
        this.psychologyPool = psychologyPool;
    }

    public int getTeamStatus(GameState game) {

        // in basic mode ignore morale
        if (game.getGameMode() == GameState.GAME_MODE_BASIC) {
            return TEAM_STATUS_NORMAL;
        }

        return teamStatus;
    }

    public void setTeamStatus(int teamStatus) {
        this.teamStatus = teamStatus;
    }

    public List<TeamObjectiveState> getTeamObjectives() {
        return teamObjectives;
    }

    public void addTeamObjective(TeamObjectiveState objective) {
        this.teamObjectives.add(objective);
    }

    public String getCharacterInCommandId() {
        return characterInCommandId;
    }

    public void setCharacterInCommandId(String characterInCommandId) {
        this.characterInCommandId = characterInCommandId;
    }

    public String getCharacterSecondInCommandId() {
        return characterSecondInCommandId;
    }

    public void setCharacterSecondInCommandId(String characterSecondInCommandId) {
        this.characterSecondInCommandId = characterSecondInCommandId;
    }

    public String getSmallTribeLogoUri() {
        return smallTribeLogoUri;
    }

    public String getLargeTribeLogoUri() {
        return largeTribeLogoUri;
    }

    @Exclude
    public int getTeamGearValue() {
        int ret = 0;
        for (CharacterState characterState : this.getHeroes()) {
            ret += characterState.getCurrentGearValue();
        }

        for (CharacterState characterState : this.getSquads()) {
            ret += characterState.getCurrentGearValue();
        }

        return ret;
    }

    public List<CharacterStateSquad> getSquads() {
        return squads;
    }

    public void setSquads(List<CharacterStateSquad> squads) {
        this.squads = squads;
        allCharactersWithSquads = null;

    }

    @Exclude
    public List<CharacterState> listAllTypesCharacters() {


        if (allCharactersWithSquads == null || allCharactersWithSquads.isEmpty()) {
            allCharactersWithSquads = new ArrayList<>();


            for(CharacterStateHero hero: heroes){
                allCharactersWithSquads.add(hero);

                for(String squad: hero.getSquads()){

                    for(CharacterState characterState: squads){
                        if(squad.equals(characterState.getIdentifier())){
                            allCharactersWithSquads.add(characterState);
                        }
                    }


                }


            }
        }
        return allCharactersWithSquads;
    }

    @Exclude
    public int getCurrentLeaderLeadershipValue() {
        int highestLD = 0;
        for (CharacterState character : getHeroes()) {
            if (character.isDead() || character.isUnconsious()) {
                continue;
            }
            int ld = character.getLeadership();
            if (ld > highestLD) {
                highestLD = ld;
            }
        }
        return highestLD;
    }

}
