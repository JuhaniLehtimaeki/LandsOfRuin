package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.proxyobjecthelper.AccountHelper;

import java.util.List;

/**
 * State representing the current phase of the game.
 * <p/>
 * - Primary Phase: ACTION, ASSIGN ACTIONS, .. - Secondary Phase:
 * WAITING_FOR_ACTION, WAITING_FOR_DEFENSE, ..
 */
@ObjectiveCName("PhaseState")
public class PhaseState {
    private int primaryPhase;
    private int secondaryPhase;
    private String currentPlayerIdentifier;

    private int gameTurn;

    private NextAttackState nextAttackState;

    public PhaseState() {
        primaryPhase = PrimaryPhase.GAME_SETUP;
        secondaryPhase = SecondaryPhase.SETUP_PLAYERS;

        gameTurn = 1;
    }


    public PhaseState getNext(GameState gameState) {
        PhaseState state = new PhaseState();

        state.setPrimaryPhase(PrimaryPhase.getNextPhase(getPrimaryPhase()));
        state.gameTurn = getGameTurn();

        if (getPrimaryPhase() != PrimaryPhase.PRE_GAME && state.getPrimaryPhase() == PrimaryPhase.ACTION) {
            state.setCurrentPlayer(getNextPlayer(gameState, getCurrentPlayer()).getIdentifier());

            if (gameState.isFirstPlayer(state.getCurrentPlayer())) {
                state.gameTurn = getGameTurn() + 1;
            }
        } else {
            state.setCurrentPlayer(getCurrentPlayer());
        }

        return state;
    }


    public PhaseState getPrevious(GameState gameState) {
        PhaseState state = new PhaseState();

        state.setPrimaryPhase(PrimaryPhase.getPreviousPhase(getPrimaryPhase()));
        state.gameTurn = getGameTurn();

        if (state.getPrimaryPhase() == PrimaryPhase.ZOMBIES) {
            state.setCurrentPlayer(getPreviousPlayer(gameState, getCurrentPlayer()).getIdentifier());
        } else {
            state.setCurrentPlayer(getCurrentPlayer());
        }

        return state;
    }

    public PlayerState getNextPlayer(GameState gameState) {
        String currentPlayerIdentifier = gameState.getPhase()
                .getCurrentPlayer();
        return getNextPlayer(gameState, currentPlayerIdentifier);
    }

    private PlayerState getNextPlayer(GameState gameState,
                                      String currentPlayerIdentifier) {
        List<PlayerState> players = gameState.getPlayers();

        int currentPosition = 0;
        for (int i = 0; i < players.size(); i++) {
            PlayerState player = players.get(i);
            if (player.getIdentifier().equals(currentPlayerIdentifier)) {
                currentPosition = i;
                break;
            }
        }

        int nextPosition = (currentPosition + 1) % players.size();

        return players.get(nextPosition);
    }

    private PlayerState getPreviousPlayer(GameState gameState,
                                          String currentPlayerIdentifier) {
        List<PlayerState> players = gameState.getPlayers();

        int currentPosition = 0;
        for (int i = 0; i < players.size(); i++) {
            PlayerState player = players.get(i);
            if (player.getIdentifier().equals(currentPlayerIdentifier)) {
                currentPosition = i;
                break;
            }
        }


        int nextPosition = (currentPosition - 1);
        if (nextPosition < 0) {
            nextPosition = players.size() - 1;
        }

        return players.get(nextPosition);
    }

    public PhaseState copy() {
        PhaseState copy = new PhaseState();

        copy.setPrimaryPhase(getPrimaryPhase());
        copy.setSecondaryPhase(getSecondaryPhase());
        copy.setCurrentPlayer(getCurrentPlayer());
        copy.setGameTurn(getGameTurn());

        return copy;
    }

    public int getPrimaryPhase() {
        return primaryPhase;
    }

    public void setPrimaryPhase(int phase) {
        this.primaryPhase = phase;
    }

    public int getSecondaryPhase() {
        return secondaryPhase;
    }

    public void setSecondaryPhase(int phase) {
        this.secondaryPhase = phase;
    }

    public String getCurrentPlayer() {
        return currentPlayerIdentifier;
    }

    public void setCurrentPlayer(String playerIdentifier) {
        this.currentPlayerIdentifier = playerIdentifier;
    }

    public boolean isSetupPhase() {
        return primaryPhase == PrimaryPhase.GAME_SETUP;
    }

    public boolean isZombiePhase() {
        return primaryPhase == PrimaryPhase.ZOMBIES;
    }

    public boolean isCommandPhase() {
        return primaryPhase == PrimaryPhase.ASSIGN_ACTIONS;
    }

    public boolean isSyncPhase() {
        return primaryPhase == PrimaryPhase.MOVE;
    }

    public boolean isActionPhase() {
        return primaryPhase == PrimaryPhase.ACTION;
    }

    public boolean isMine() {
        return AccountHelper.getInstance().getAccountObjectHelper().isMe(currentPlayerIdentifier);
    }

    public boolean isPreGamePhase() {
        return primaryPhase == PrimaryPhase.PRE_GAME;
    }

    public int getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(int gameTurn) {
        this.gameTurn = gameTurn;
    }

    public void incrementGameTurn() {
        gameTurn++;
    }

    /**
     * @return NULL if waiting for nothing
     */
    public NextAttackState getNextAttackState() {
        return nextAttackState;
    }

    public void setNextAttackState(NextAttackState nextAttackState) {
        this.nextAttackState = nextAttackState;
    }
}
