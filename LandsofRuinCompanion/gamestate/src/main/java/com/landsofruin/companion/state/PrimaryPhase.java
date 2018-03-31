package com.landsofruin.companion.state;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.transition.ActionPhaseTransition;
import com.landsofruin.companion.state.transition.DamageResolutionPhaseTransition;
import com.landsofruin.companion.state.transition.MovePhaseTransition;
import com.landsofruin.companion.state.transition.StartGameTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.ZombiesPhaseTransition;

@ObjectiveCName("PrimaryPhase")
public abstract class PrimaryPhase {


    public static final int GAME_SETUP = 0;
    public static final int SYNC_SETUP = 1;
    public static final int PRE_GAME = 2;
    public static final int ACTION = 3;
    public static final int MOVE = 4;
    public static final int DAMAGE_RESOLUTION = 5;
    public static final int ASSIGN_ACTIONS = 6;
    public static final int ZOMBIES = 7;
    public static final int GAME_END = 8;


    @Exclude
    public static String getSubLabel(int phaseId) {
        switch (phaseId) {
            case GAME_SETUP:
                return "Setup the game";
            case SYNC_SETUP:
                return "Sync setup";
            case ACTION:
                return "Perform actions";
            case MOVE:
                return "Indicate where characters are";
            case DAMAGE_RESOLUTION:
                return "Damage";
            case ASSIGN_ACTIONS:
                return "Plan for your next turn";
            case ZOMBIES:
                return "Handle Rotters and Grenades";
            case GAME_END:
                return "Game end";
            case PRE_GAME:
                return "Deploy Rotters";
            default:
                throw new AssertionError("Phase " + getLabel(phaseId) + " has no label");
        }
    }


    @Exclude
    public static String getLabel(int phaseId) {
        switch (phaseId) {
            case GAME_SETUP:
                return "Game setup";
            case SYNC_SETUP:
                return "Sync setup";
            case ACTION:
                return "Action";
            case MOVE:
                return "Sync";
            case DAMAGE_RESOLUTION:
                return "Damage";
            case ASSIGN_ACTIONS:
                return "Command";
            case ZOMBIES:
                return "Environment";
            case GAME_END:
                return "Game end";
            case PRE_GAME:
                return "Setup & Deployment";
            default:
                throw new AssertionError("Phase " + getLabel(phaseId) + " has no label");
        }
    }

    @Exclude
    public static int getNextPhase(int phaseId) {
        switch (phaseId) {
            case GAME_SETUP:
                return ACTION;
            case SYNC_SETUP:
                return ACTION;
            case ACTION:
                return MOVE;
            case MOVE:
                return ASSIGN_ACTIONS;
            case ASSIGN_ACTIONS:
                return ZOMBIES;
            case ZOMBIES:
                return ACTION;
            case GAME_END:
                return GAME_END;
            case PRE_GAME:
                return ACTION;
            default:
                throw new AssertionError("Phase " + getLabel(phaseId) + " has no next phase");
        }
    }

    @Exclude
    public static int getPreviousPhase(int phaseId) {
        switch (phaseId) {
            case MOVE:
                return ACTION;
            case ASSIGN_ACTIONS:
                return MOVE;
            case ZOMBIES:
                return ASSIGN_ACTIONS;
            case ACTION:
                return ZOMBIES;
            case GAME_END:
                return GAME_END;
            default:
                throw new AssertionError("Phase " + getLabel(phaseId) + " has no previous phase");
        }
    }

    @Exclude
    public static Transition getAdvanceTransition(int phaseId) {
        switch (phaseId) {
            case SYNC_SETUP:
                return new StartGameTransition();
            case GAME_SETUP:
                return new StartGameTransition();
            case ACTION:
                return new MovePhaseTransition();
            case ASSIGN_ACTIONS:
                return new ZombiesPhaseTransition();
            case MOVE:
                return new DamageResolutionPhaseTransition();
            case ZOMBIES:
                return new ActionPhaseTransition(null);
            case GAME_END:
                throw new IllegalStateException("Can't advance from GAME_END state");
            default:
                throw new AssertionError("Phase " + getLabel(phaseId) + " has no advance transition assigned");
        }
    }
}
