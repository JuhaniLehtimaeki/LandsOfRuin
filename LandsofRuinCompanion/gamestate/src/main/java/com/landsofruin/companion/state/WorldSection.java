package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 01/03/15.
 */
@ObjectiveCName("WorldSection")
public class WorldSection {

    public static final int DIRECTION_CENTER = 0;
    private int currentDirection = DIRECTION_CENTER;
    public static final int DIRECTION_SOUTH = 1;
    public static final int DIRECTION_NORTH = 2;
    public static final int DIRECTION_EAST = 3;
    public static final int DIRECTION_WEST = 4;
    private int totalEnemies = 0;
    private int threadLevel = 0;
    private boolean isGameTableSection = false;
    private int pullSouth = 0;
    private int pullNorth = 0;
    private int pullEast = 0;
    private int pullWest = 0;


    private int zombiesMoveInThisTurnFromSouth = 0;
    private int zombiesMoveInThisTurnFromNorth = 0;
    private int zombiesMoveInThisTurnFromEast = 0;
    private int zombiesMoveInThisTurnFromWest = 0;


    public void setIsGameTableSection(boolean isGameTableSection) {
        this.isGameTableSection = isGameTableSection;
    }

    public boolean isGameTableSection() {
        return isGameTableSection;
    }

    public int getThreadLevel() {
        return threadLevel;
    }

    public void setThreadLevel(int threadLevel) {
        this.threadLevel = threadLevel;
    }

    public int getTotalEnemies() {
        return totalEnemies;
    }

    public void setTotalEnemies(int totalEnemies) {
        this.totalEnemies = totalEnemies;
    }

    public int getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(int currentDirection) {
        this.currentDirection = currentDirection;
    }

    public int getPullWest() {
        return pullWest;
    }

    public void setPullWest(int pullWest) {
        this.pullWest = pullWest;
    }

    public int getPullEast() {
        return pullEast;
    }

    public void setPullEast(int pullEast) {
        this.pullEast = pullEast;
    }

    public int getPullNorth() {
        return pullNorth;
    }

    public void setPullNorth(int pullNorth) {
        this.pullNorth = pullNorth;
    }

    public int getPullSouth() {
        return pullSouth;
    }

    public void setPullSouth(int pullSouth) {
        this.pullSouth = pullSouth;
    }

    public int getZombiesMoveInThisTurn() {
        return this.zombiesMoveInThisTurnFromEast + this.zombiesMoveInThisTurnFromWest + this.zombiesMoveInThisTurnFromNorth + this.zombiesMoveInThisTurnFromSouth;
    }


    public void setZombiesMoveInThisTurnFromEast(int zombiesMoveInThisTurnFromEast) {
        this.zombiesMoveInThisTurnFromEast = zombiesMoveInThisTurnFromEast;
    }

    public void setZombiesMoveInThisTurnFromNorth(int zombiesMoveInThisTurnFromNorth) {
        this.zombiesMoveInThisTurnFromNorth = zombiesMoveInThisTurnFromNorth;
    }

    public void setZombiesMoveInThisTurnFromSouth(int zombiesMoveInThisTurnFromSouth) {
        this.zombiesMoveInThisTurnFromSouth = zombiesMoveInThisTurnFromSouth;
    }

    public void setZombiesMoveInThisTurnFromWest(int zombiesMoveInThisTurnFromWest) {
        this.zombiesMoveInThisTurnFromWest = zombiesMoveInThisTurnFromWest;
    }

    public int getZombiesMoveInThisTurnFromEast() {
        return zombiesMoveInThisTurnFromEast;
    }

    public int getZombiesMoveInThisTurnFromNorth() {
        return zombiesMoveInThisTurnFromNorth;
    }

    public int getZombiesMoveInThisTurnFromSouth() {
        return zombiesMoveInThisTurnFromSouth;
    }

    public int getZombiesMoveInThisTurnFromWest() {
        return zombiesMoveInThisTurnFromWest;
    }
}
