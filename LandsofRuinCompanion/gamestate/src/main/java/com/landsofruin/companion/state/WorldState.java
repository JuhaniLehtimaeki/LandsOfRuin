package com.landsofruin.companion.state;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.dice.DiceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@ObjectiveCName("WorldState")
public class WorldState {

    public static final int THREAD_LEVEL_REMOVED_PER_DISTANCE = 1;


    public static final int SPAWN_DIRECTION_topLeft = 0;
    public static final int SPAWN_DIRECTION_topCenter = 1;
    public static final int SPAWN_DIRECTION_topRight = 2;
    public static final int SPAWN_DIRECTION_bottomLeft = 3;
    public static final int SPAWN_DIRECTION_bottomCenter = 4;
    public static final int SPAWN_DIRECTION_bottomRight = 5;
    public static final int SPAWN_DIRECTION_rightTop = 6;
    public static final int SPAWN_DIRECTION_rightCenter = 7;
    public static final int SPAWN_DIRECTION_rightBottom = 8;
    public static final int SPAWN_DIRECTION_leftTop = 9;
    public static final int SPAWN_DIRECTION_leftCenter = 10;
    public static final int SPAWN_DIRECTION_leftBottom = 11;


    private List<HashMap<String, Integer>> zombieSpawns_Integer_Integer;

    private List<List<WorldSection>> worldSections_Array_WorldSection;
    private String zombieTargetRegion;
    private String zombieTargetCharacter;


    private ArrayList<Integer> diceRollReserveForZombieSpawn = new ArrayList<>(100);

    private int baseThreatLevel = 0;
    private int areaDangerLevel = 0;

    private int currentAmountOfZombies = 0;

    private List<ThrowableState> throwableStates_ThrowableState = new ArrayList<>();

    //doesn't need to be serialised
    private transient List<ThrowableState> temporarThrowableStates_ThrowableState = new ArrayList<>();

    private HashMap<String, HashMap<String, Integer>> preGameZombiesPerPlayerPerArea_String_Integer = new HashMap<>();
    private HashMap<String, Integer> zombieSleepersPerArea_String_Integer;


    public WorldState() {


        zombieSpawns_Integer_Integer = new ArrayList<>();
        for (int i = 0; i < 12; ++i) {
            zombieSpawns_Integer_Integer.add(new HashMap<String, Integer>());
        }

    }


    public static List<List<WorldSection>> generateInitialWorldSections(GameState gameState) {


        int areaDangerLevel = gameState.getWorld().getAreaDangerLevel();


        List<List<WorldSection>> ret = new ArrayList<>();

        for (int j = 0; j < 23; ++j) {
            ret.add(new ArrayList<WorldSection>());


            for (int i = 0; i < 23; ++i) {

                ret.get(j).add(new WorldSection());


                if (DiceUtils.rollDie(50) == 50) {
                    ret.get(j).get(i).setThreadLevel(DiceUtils.rollDie(20));
                }

            }
        }

        ret.get(10).get(10).setIsGameTableSection(true);
        ret.get(10).get(11).setIsGameTableSection(true);
        ret.get(10).get(12).setIsGameTableSection(true);

        ret.get(11).get(10).setIsGameTableSection(true);
        ret.get(11).get(11).setIsGameTableSection(true);
        ret.get(11).get(12).setIsGameTableSection(true);

        ret.get(12).get(10).setIsGameTableSection(true);
        ret.get(12).get(11).setIsGameTableSection(true);
        ret.get(12).get(12).setIsGameTableSection(true);

        int initialZombieCount = (int) (GameConstants.MAXIMUM_ZOMBIES_ON_100_DANGER_LEVEL * (areaDangerLevel / 100f));


        for (int i = 0; i < initialZombieCount; ++i) {
            int x = DiceUtils.rollDie(23) - 1;
            int y = DiceUtils.rollDie(23) - 1;

            ret.get(x).get(y).setTotalEnemies(ret.get(x).get(y).getTotalEnemies() + 1);
        }

        return ret;

    }


    public static List<List<WorldSection>> generateInitialAdditionalZombies(GameState gameState) {


        List<List<WorldSection>> ret = gameState.getWorld().getWorldSections();
        int initialZombieCount = (GameConstants.MAXIMUM_ZOMBIES_ON_100_DANGER_LEVEL / 10) + DiceUtils.rollDie((GameConstants.MAXIMUM_ZOMBIES_ON_100_DANGER_LEVEL / 10));


        for (int i = 0; i < initialZombieCount; ++i) {
            int x = DiceUtils.rollDie(3) + 9;
            int y = DiceUtils.rollDie(3) + 9;

            ret.get(x).get(y).setTotalEnemies(ret.get(x).get(y).getTotalEnemies() + 1);
        }

        return ret;

    }


    public static List<List<WorldSection>> tickWorldState(GameState gameState, boolean isNePhaseTransition) {
        List<List<WorldSection>> ret = gameState.getWorld().getWorldSections();


        // decay threat level
        for (int j = 0; j < 23; ++j) {

            for (int i = 0; i < 23; ++i) {
                WorldSection section = ret.get(j).get(i);

                section.setPullNorth(0);
                section.setPullSouth(0);
                section.setPullEast(0);
                section.setPullWest(0);

                section.setZombiesMoveInThisTurnFromEast(0);
                section.setZombiesMoveInThisTurnFromWest(0);
                section.setZombiesMoveInThisTurnFromSouth(0);
                section.setZombiesMoveInThisTurnFromNorth(0);

            }
        }

        // decay threat level
        for (int j = 0; j < 23; ++j) {

            for (int i = 0; i < 23; ++i) {
                WorldSection section = ret.get(j).get(i);


                if (section.isGameTableSection()) {
                    if (isNePhaseTransition) {
                        section.setThreadLevel(gameState.getWorld().getThreatLevel(gameState));
                    }
                } else {

                    int quarterThreatLevel = (int) (section.getThreadLevel() * 0.25);
                    if (quarterThreatLevel <= 2) {
                        quarterThreatLevel = 2;
                    }

                    section.setThreadLevel(Math.max(section.getThreadLevel() - quarterThreatLevel, 0));


                    // add new threat
                    if (DiceUtils.rollDie(200) == 200) {
                        section.setThreadLevel(section.getThreadLevel() + DiceUtils.rollDie(20));
                    }
                }

                if (section.getThreadLevel() > 0) {
                    setPullFactorToNeighbouringSections(section, j, i, ret);
                }


            }
        }


        // move zombies
        for (int j = 0; j < 23; ++j) {

            for (int i = 0; i < 23; ++i) {

                WorldSection section = ret.get(j).get(i);

                if (section.getTotalEnemies() <= 0) {
                    continue;
                }

                int direction = WorldSection.DIRECTION_CENTER;
                int maxPull = section.getThreadLevel();

                int targetI = 0;
                int targetJ = 0;


                if (section.getPullNorth() > maxPull || (section.getPullNorth() == maxPull && DiceUtils.rollDie(2) == 2)) {
                    maxPull = section.getPullNorth();
                    direction = WorldSection.DIRECTION_NORTH;

                    targetI = i;
                    targetJ = j - 1;
                }
                if (section.getPullSouth() > maxPull || (section.getPullSouth() == maxPull && DiceUtils.rollDie(2) == 2)) {
                    maxPull = section.getPullSouth();
                    direction = WorldSection.DIRECTION_SOUTH;

                    targetI = i;
                    targetJ = j + 1;
                }
                if (section.getPullWest() > maxPull || (section.getPullWest() == maxPull && DiceUtils.rollDie(2) == 2)) {
                    maxPull = section.getPullWest();
                    direction = WorldSection.DIRECTION_WEST;

                    targetI = i - 1;
                    targetJ = j;
                }
                if (section.getPullEast() > maxPull || (section.getPullEast() == maxPull && DiceUtils.rollDie(2) == 2)) {
                    maxPull = section.getPullEast();
                    direction = WorldSection.DIRECTION_EAST;

                    targetI = i + 1;
                    targetJ = j;
                }

                if (targetI >= 23) {
                    targetI = 22;
                }
                if (targetJ >= 23) {
                    targetJ = 22;
                }
                if (targetI < 0) {
                    targetI = 0;
                }
                if (targetJ < 0) {
                    targetJ = 0;
                }


// zombies move only if they're pulled twice to same direction
                if (direction != section.getCurrentDirection()) {
                    section.setCurrentDirection(direction);
                } else if (direction != WorldSection.DIRECTION_CENTER) {
                    // zombies will move


                    WorldSection targetSection = ret.get(targetJ).get(targetI);

                    int movingZombies = section.getTotalEnemies();


                    switch (direction) {
                        case WorldSection.DIRECTION_EAST:
                            targetSection.setZombiesMoveInThisTurnFromEast(movingZombies);
                            break;
                        case WorldSection.DIRECTION_WEST:
                            targetSection.setZombiesMoveInThisTurnFromWest(movingZombies);
                            break;
                        case WorldSection.DIRECTION_NORTH:
                            targetSection.setZombiesMoveInThisTurnFromNorth(movingZombies);
                            break;
                        case WorldSection.DIRECTION_SOUTH:
                            targetSection.setZombiesMoveInThisTurnFromSouth(movingZombies);
                            break;
                    }

                    section.setTotalEnemies(section.getTotalEnemies() - movingZombies);
                }

            }
        }

        // set new zombie number
        for (int j = 0; j < 23; ++j) {

            for (int i = 0; i < 23; ++i) {
                WorldSection section = ret.get(j).get(i);

                if (section.isGameTableSection()) {
                    if (isNePhaseTransition) {
                        section.setTotalEnemies(0);
                    }
                } else {
                    section.setTotalEnemies(section.getTotalEnemies() + section.getZombiesMoveInThisTurn());
                }

            }
        }


        return ret;
    }

    @Exclude
    public int getZombiesInTableArea() {
        int ret = 0;

        ret += getWorldSections().get(10).get(10).getTotalEnemies();
        ret += getWorldSections().get(10).get(11).getTotalEnemies();
        ret += getWorldSections().get(10).get(12).getTotalEnemies();

        ret += getWorldSections().get(11).get(10).getTotalEnemies();
        ret += getWorldSections().get(11).get(11).getTotalEnemies();
        ret += getWorldSections().get(11).get(12).getTotalEnemies();

        ret += getWorldSections().get(12).get(10).getTotalEnemies();
        ret += getWorldSections().get(12).get(11).getTotalEnemies();
        ret += getWorldSections().get(12).get(12).getTotalEnemies();

        return ret;
    }

    @Exclude
    private static void setPullFactorToNeighbouringSections(WorldSection section, int j, int i, List<List<WorldSection>> allSections) {

        // NORTH
        int currentThreat = section.getThreadLevel() - THREAD_LEVEL_REMOVED_PER_DISTANCE;

        int distance = 1;
        while (currentThreat > 0) {

            int nextj = j - distance;
            if (nextj < 0) {
                break;
            }

            WorldSection centralCell = allSections.get(nextj).get(i);
            centralCell.setPullSouth(centralCell.getPullSouth() + currentThreat);

            addPullToHorizontalCrossCells(currentThreat, i, distance, nextj, allSections, true);


            currentThreat = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++distance;
        }


        //SOUTH
        currentThreat = section.getThreadLevel() - THREAD_LEVEL_REMOVED_PER_DISTANCE;

        distance = 1;
        while (currentThreat > 0) {

            int nextj = j + distance;
            if (nextj >= allSections.size()) {
                break;
            }

            WorldSection centralCell = allSections.get(nextj).get(i);


            centralCell.setPullNorth(centralCell.getPullNorth() + currentThreat);

            addPullToHorizontalCrossCells(currentThreat, i, distance, nextj, allSections, false);


            currentThreat = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++distance;
        }


        // WEST
        currentThreat = section.getThreadLevel() - THREAD_LEVEL_REMOVED_PER_DISTANCE;

        distance = 1;
        while (currentThreat > 0) {

            int nexti = i - distance;
            if (nexti < 0) {
                break;
            }

            WorldSection centralCell = allSections.get(j).get(nexti);
            centralCell.setPullEast(centralCell.getPullEast() + currentThreat);

            addPullToVerticalCrossCells(currentThreat, j, distance, nexti, allSections, true);


            currentThreat = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++distance;
        }


        // EAST
        currentThreat = section.getThreadLevel() - THREAD_LEVEL_REMOVED_PER_DISTANCE;

        distance = 1;
        while (currentThreat > 0) {
            int nexti = i + distance;
            if (nexti >= allSections.get(j).size()) {
                break;
            }

            WorldSection centralCell = allSections.get(j).get(nexti);
            centralCell.setPullWest(centralCell.getPullWest() + currentThreat);

            addPullToVerticalCrossCells(currentThreat, j, distance, nexti, allSections, false);


            currentThreat = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++distance;
        }

    }

    private static void addPullToHorizontalCrossCells(int currentThreat, int i, int distance, int nextj, List<List<WorldSection>> allSections, boolean south) {
        int currentThreatToCross = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
        int crossDistance = 1;
        while (currentThreatToCross > 0) {

            int nexti = i - crossDistance;
            if (nexti < 0) {
                break;
            }

            if (distance == crossDistance) {
                //this is the intersection
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (south) {
                    nextLeftCell.setPullSouth(nextLeftCell.getPullSouth() + (currentThreatToCross / 2));
                } else {
                    nextLeftCell.setPullNorth(nextLeftCell.getPullNorth() + (currentThreatToCross / 2));
                }

                break;
            } else {
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (south) {
                    nextLeftCell.setPullSouth(nextLeftCell.getPullSouth() + currentThreatToCross);
                } else {
                    nextLeftCell.setPullNorth(nextLeftCell.getPullNorth() + currentThreatToCross);
                }
            }


            currentThreatToCross = currentThreatToCross - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++crossDistance;
        }


        currentThreatToCross = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
        crossDistance = 1;
        while (currentThreatToCross > 0) {

            int nexti = i + crossDistance;
            if (nexti >= allSections.get(nextj).size()) {
                break;
            }

            if (distance == crossDistance) {
                //this is the intersection
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (south) {
                    nextLeftCell.setPullSouth(nextLeftCell.getPullSouth() + (currentThreatToCross / 2));
                } else {
                    nextLeftCell.setPullNorth(nextLeftCell.getPullNorth() + (currentThreatToCross / 2));
                }

                break;
            } else {
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (south) {
                    nextLeftCell.setPullSouth(nextLeftCell.getPullSouth() + currentThreatToCross);
                } else {
                    nextLeftCell.setPullNorth(nextLeftCell.getPullNorth() + currentThreatToCross);
                }
            }


            currentThreatToCross = currentThreatToCross - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++crossDistance;
        }
    }

    private static void addPullToVerticalCrossCells(int currentThreat, int j, int distance, int nexti, List<List<WorldSection>> allSections, boolean isEast) {
        int currentThreatToCross = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
        int crossDistance = 1;
        while (currentThreatToCross > 0) {

            int nextj = j - crossDistance;
            if (nextj < 0) {
                break;
            }

            if (distance == crossDistance) {
                //this is the intersection
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (!isEast) {
                    nextLeftCell.setPullWest(nextLeftCell.getPullWest() + (currentThreatToCross / 2));
                } else {
                    nextLeftCell.setPullEast(nextLeftCell.getPullEast() + (currentThreatToCross / 2));
                }

                break;
            } else {
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (!isEast) {
                    nextLeftCell.setPullWest(nextLeftCell.getPullWest() + currentThreatToCross);
                } else {
                    nextLeftCell.setPullEast(nextLeftCell.getPullEast() + currentThreatToCross);
                }
            }


            currentThreatToCross = currentThreatToCross - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++crossDistance;
        }


        currentThreatToCross = currentThreat - THREAD_LEVEL_REMOVED_PER_DISTANCE;
        crossDistance = 1;
        while (currentThreatToCross > 0) {

            int nextj = j + crossDistance;
            if (nextj >= allSections.size()) {
                break;
            }

            if (distance == crossDistance) {
                //this is the intersection
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (!isEast) {
                    nextLeftCell.setPullWest(nextLeftCell.getPullWest() + (currentThreatToCross / 2));
                } else {
                    nextLeftCell.setPullEast(nextLeftCell.getPullEast() + (currentThreatToCross / 2));
                }

                break;
            } else {
                WorldSection nextLeftCell = allSections.get(nextj).get(nexti);
                if (!isEast) {
                    nextLeftCell.setPullWest(nextLeftCell.getPullWest() + currentThreatToCross);
                } else {
                    nextLeftCell.setPullEast(nextLeftCell.getPullEast() + currentThreatToCross);
                }
            }


            currentThreatToCross = currentThreatToCross - THREAD_LEVEL_REMOVED_PER_DISTANCE;
            ++crossDistance;
        }


    }

    public void clearTemporarThrowableStates() {
        this.temporarThrowableStates_ThrowableState.clear();
    }

    public List<HashMap<String, Integer>> getZombieSpawns() {
        return Collections.unmodifiableList(zombieSpawns_Integer_Integer);
    }

    @Exclude
    public void setZombieSpawnsToEmpty() {
        for (int i = 0; i < zombieSpawns_Integer_Integer.size(); ++i) {
            zombieSpawns_Integer_Integer.get(i).clear();
        }
    }


    private int rollIndex = 0;

    @Exclude
    private int getNextRoll() {
        if (rollIndex >= this.diceRollReserveForZombieSpawn.size()) {
            rollIndex = 0;
        }

        return this.diceRollReserveForZombieSpawn.get(rollIndex++);

    }

    private void createZombieSpawns(int totalZombies, HashMap<String, Integer> sectionMap) {
        sectionMap.clear();
        sectionMap.put("" + Zombie.ZOMBIE_ID_FAST, 0);
        sectionMap.put("" + Zombie.ZOMBIE_ID_FAT, 0);
        sectionMap.put("" + Zombie.ZOMBIE_ID_NORMAL, 0);


        for (int i = 0; i < totalZombies; ++i) {


            int zombieTypeRoll = getNextRoll();

            if (zombieTypeRoll > 100 - (GameConstants.FAST_ZOMBIE_SPAWN_CHANCE_PER_SPAWNING_ZOMBIE + GameConstants.FAT_ZOMBIE_SPAWN_CHANCE_PER_SPAWNING_ZOMBIE)) {

                if (zombieTypeRoll > 100 - GameConstants.FAST_ZOMBIE_SPAWN_CHANCE_PER_SPAWNING_ZOMBIE) {
                    sectionMap.put("" + Zombie.ZOMBIE_ID_FAST, sectionMap.get("" + Zombie.ZOMBIE_ID_FAST) + 1);
                } else {
                    sectionMap.put("" + Zombie.ZOMBIE_ID_FAT, sectionMap.get("" + Zombie.ZOMBIE_ID_FAT) + 1);
                }


            } else {
                sectionMap.put("" + Zombie.ZOMBIE_ID_NORMAL, sectionMap.get("" + Zombie.ZOMBIE_ID_NORMAL) + 1);
            }

        }

    }

    @Exclude
    public void setZombieSpawnsFromWorldState() {
        this.rollIndex = 0;

        setZombieSpawnsToEmpty();


        WorldSection topleft = this.worldSections_Array_WorldSection.get(10).get(10);
        createZombieSpawns(topleft.getZombiesMoveInThisTurnFromEast(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_topLeft));
        createZombieSpawns(topleft.getZombiesMoveInThisTurnFromSouth(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_leftTop));


        WorldSection top = this.worldSections_Array_WorldSection.get(10).get(11);
        createZombieSpawns(top.getZombiesMoveInThisTurnFromSouth(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_topCenter));


        WorldSection topRight = this.worldSections_Array_WorldSection.get(10).get(12);
        createZombieSpawns(topRight.getZombiesMoveInThisTurnFromSouth(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_topRight));
        createZombieSpawns(topRight.getZombiesMoveInThisTurnFromWest(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_rightTop));


        WorldSection right = this.worldSections_Array_WorldSection.get(11).get(12);
        createZombieSpawns(right.getZombiesMoveInThisTurnFromWest(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_rightCenter));


        WorldSection bottomLeft = this.worldSections_Array_WorldSection.get(12).get(10);
        createZombieSpawns(bottomLeft.getZombiesMoveInThisTurnFromEast(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_bottomLeft));
        createZombieSpawns(bottomLeft.getZombiesMoveInThisTurnFromNorth(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_leftBottom));


        WorldSection bottom = this.worldSections_Array_WorldSection.get(12).get(11);
        createZombieSpawns(bottom.getZombiesMoveInThisTurnFromNorth(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_bottomCenter));


        WorldSection bottomRight = this.worldSections_Array_WorldSection.get(12).get(12);
        createZombieSpawns(bottomRight.getZombiesMoveInThisTurnFromNorth(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_bottomRight));
        createZombieSpawns(bottomRight.getZombiesMoveInThisTurnFromWest(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_rightBottom));

        WorldSection left = this.worldSections_Array_WorldSection.get(11).get(10);
        createZombieSpawns(left.getZombiesMoveInThisTurnFromEast(), zombieSpawns_Integer_Integer.get(SPAWN_DIRECTION_leftCenter));


    }

    public String getZombieTargetRegion() {
        return zombieTargetRegion;
    }

    public void setZombieTargetRegion(String regionIdentifier) {
        this.zombieTargetRegion = regionIdentifier;
    }

    public String getZombieTargetCharacter() {
        return zombieTargetCharacter;
    }

    public void setZombieTargetCharacter(String zombieTargetCharacter) {
        this.zombieTargetCharacter = zombieTargetCharacter;
    }

    public boolean hasZombieTargetCharacter() {
        return zombieTargetCharacter != null;
    }

    public void simulateZombies(GameState gameState) {

        determineRegion(gameState);
        determineTarget(gameState);
    }

    public List<List<WorldSection>> getWorldSections() {
        return worldSections_Array_WorldSection;
    }

    public void setWorldSections(List<List<WorldSection>> worldSections) {
        this.worldSections_Array_WorldSection = worldSections;
    }

    private void determineRegion(GameState gameState) {
        List<RegionState> regions = gameState.getMap().getRegionsWithoutSpecials();

        List<RegionState> loudestRegions = new LinkedList<>();
        for (RegionState regionState : regions) {
            int noise = regionState.getTotalNoise();

            Log.d("Zombie AI", "Region: " + regionState.getLabel() + " noise: "
                    + noise);

            if (loudestRegions.isEmpty()
                    || loudestRegions.get(0).getTotalNoise() < noise) {
                loudestRegions.clear();
                loudestRegions.add(regionState);
            } else if (!loudestRegions.isEmpty()
                    && loudestRegions.get(0).getTotalNoise() == noise) {
                loudestRegions.add(regionState);
            }

        }

        // roll for the loudest ones
        int region = DiceUtils.rollDie(loudestRegions.size()) - 1;
        zombieTargetRegion = loudestRegions.get(region).getIdentifier();

        Log.d("Zombie AI",
                "Target region selected : "
                        + loudestRegions.get(region).getLabel() + " noise: "
                        + loudestRegions.get(region).getTotalNoise());

    }

    private void determineTarget(GameState gameState) {
        List<CharacterState> charactersInRegion = gameState
                .findCharactersInRegion(zombieTargetRegion);

        if (charactersInRegion.size() == 0) {
            zombieTargetCharacter = null;
        } else {

            List<CharacterState> loudestCharacter = new LinkedList<CharacterState>();
            for (CharacterState characterState : charactersInRegion) {
                if (characterState.isHidden() || characterState.isDead()
                        || characterState.isUnconsious()) {
                    continue;
                }
                int noise = characterState.getCurrentNoise();
                if (loudestCharacter.isEmpty()
                        || loudestCharacter.get(0).getCurrentNoise() < noise) {
                    loudestCharacter.clear();
                    loudestCharacter.add(characterState);
                } else if (!loudestCharacter.isEmpty()
                        && loudestCharacter.get(0).getCurrentNoise() == noise) {
                    loudestCharacter.add(characterState);
                }

            }

            if (loudestCharacter.isEmpty()) {
                zombieTargetCharacter = null;
            } else {

                // roll for the loudest ones
                int character = DiceUtils.rollDie(loudestCharacter.size()) - 1;
                zombieTargetCharacter = loudestCharacter.get(character)
                        .getIdentifier();
            }
        }
    }

    @Exclude
    public int getThreatLevel(GameState gameState) {
        int cumulativeNoise = baseThreatLevel * 200;

        for (CharacterState character : gameState.getEnemyCharacters()) {
            cumulativeNoise += character.getCumulativeNoise();
        }

        for (CharacterState character : gameState.getOwnCharacters()) {
            cumulativeNoise += character.getCumulativeNoise();
        }

        int threatLevel = cumulativeNoise / 200;
        if (threatLevel > GameConstants.MAXIMUM_THREAT_LEVEL) {
            threatLevel = GameConstants.MAXIMUM_THREAT_LEVEL;
        }

        return threatLevel;
    }

    public int getBaseThreatLevel() {
        return baseThreatLevel;
    }

    public void setBaseThreatLevel(int baseThreatLevel) {
        this.baseThreatLevel = baseThreatLevel;
    }

    public int getCurrentAmountOfZombies() {
        return currentAmountOfZombies;
    }

    public void setCurrentAmountOfZombies(int currentAmountOfZombies) {
        this.currentAmountOfZombies = currentAmountOfZombies;
    }

    public List<ThrowableState> getThrowableStates() {
        return throwableStates_ThrowableState;
    }

    public void removeThrowbleState(ThrowableState remove) {
        this.throwableStates_ThrowableState.remove(remove);
    }

    public void addThrowableState(ThrowableState throwableState) {
        this.throwableStates_ThrowableState.add(throwableState);
    }

    public void addTemporarThrowableState(ThrowableState throwableState) {
        this.temporarThrowableStates_ThrowableState.add(throwableState);
    }

    public List<ThrowableState> getTemporarThrowableStates() {
        return temporarThrowableStates_ThrowableState;
    }

    public HashMap<String, HashMap<String, Integer>> getPreGameZombiesPerPlayerPerArea() {
        return preGameZombiesPerPlayerPerArea_String_Integer;
    }

    public void setPreGameZombiesPerPlayerPerArea(
            HashMap<String, HashMap<String, Integer>> preGameZombiesPerPlayerPerArea) {
        this.preGameZombiesPerPlayerPerArea_String_Integer = preGameZombiesPerPlayerPerArea;
    }

    public HashMap<String, Integer> getZombieSleepersPerArea() {
        return zombieSleepersPerArea_String_Integer;
    }

    public void setZombieSleepersPerArea(
            HashMap<String, Integer> zombieSleepersPerArea) {
        this.zombieSleepersPerArea_String_Integer = zombieSleepersPerArea;
    }

    public int getAreaDangerLevel() {
        return areaDangerLevel;
    }

    public void setAreaDangerLevel(int areaDangerLevel) {
        this.areaDangerLevel = areaDangerLevel;
    }

    public void setDiceRollReserveForZombieSpawn(ArrayList diceRollReserveForZombieSpawn) {
        this.diceRollReserveForZombieSpawn = diceRollReserveForZombieSpawn;
    }
}
