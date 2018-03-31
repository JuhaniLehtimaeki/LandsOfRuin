package com.landsofruin.companion.tutorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.tutorial.TutorialConstants;
import com.landsofruin.gametracker.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.LinkedList;

import static com.landsofruin.companion.state.PrimaryPhase.ACTION;
import static com.landsofruin.companion.state.PrimaryPhase.ASSIGN_ACTIONS;
import static com.landsofruin.companion.state.PrimaryPhase.MOVE;
import static com.landsofruin.companion.state.PrimaryPhase.PRE_GAME;
import static com.landsofruin.companion.state.PrimaryPhase.ZOMBIES;
import static com.landsofruin.companion.state.PrimaryPhase.getLabel;

/**
 * Created by juhani on 22/12/14.
 */
public class TutorialUtils {


    private static final LinkedList<String> ALL_TUTORIAL_KEYS = new LinkedList<>();
    private static final HashMap<String, String> TUTORIAL_KEY_TO_NAME = new HashMap<>();

    static {
        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_GAME_SETUP_ADMIN);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_GAME_SETUP_ADMIN, "Game setup - Admin");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_GAME_SETUP_NON_ADMIN);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_GAME_SETUP_NON_ADMIN, "Game setup - Non admin");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_TURN_STRUCTURE);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_TURN_STRUCTURE, "Turn Structure");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_GAME_UI_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_GAME_UI_OVERVIEW, "Game UI overview");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_PRE_GAME_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_PRE_GAME_OVERVIEW, "Pregame phase");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_FIRST_ACTION_PHASE_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_FIRST_ACTION_PHASE_OVERVIEW, "Your first action phase");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_ACTION_PHASE_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_ACTION_PHASE_OVERVIEW, "Action Phase");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_MOVEMENT_PHASE_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_MOVEMENT_PHASE_OVERVIEW, "Movement Phase");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_COMMAND_PHASE_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_COMMAND_PHASE_OVERVIEW, "Command Phase");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_ZOMBIE_PHASE_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_ZOMBIE_PHASE_OVERVIEW, "Environment Phase");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_ENEMY_TURN_OVERVIEW);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_ENEMY_TURN_OVERVIEW, "Enemy turn overview");

        ALL_TUTORIAL_KEYS.add(TutorialConstants.KEY_ZOMBIE_MOVEMENT_RULES);
        TUTORIAL_KEY_TO_NAME.put(TutorialConstants.KEY_ZOMBIE_MOVEMENT_RULES, "Rotter movement rules");


    }


    private static final String KEY_TUTORIAL_MODE_ACTIVE = "KEY_TUTORIAL_MODE_ACTIVE";
    private static final String KEY_TUTORIAL_SEEN_PREFIX = "KEY_TUTORIAL_SEEN_PREFIX";


    private static TutorialUtils instance;
    private final SharedPreferences prefs;
    private Context context;

    private TutorialUtils(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static TutorialUtils getInstance(Context context) {
        if (instance == null) {
            instance = new TutorialUtils(context.getApplicationContext());
        }

        return instance;
    }

    public LinkedList<String> getAllTutorialKeys()

    {
        return ALL_TUTORIAL_KEYS;
    }

    /**
     * @return null if no tutorial screen should be shown in the gamestate
     */
    public View getViewForTutorial(String tutorialKey, LayoutInflater layoutInflater, ViewGroup parent) {

        int layoutId = getTutorialViewId(tutorialKey);

        if (layoutId == -1) {
            return null;
        }

        View view = layoutInflater.inflate(layoutId, parent, false);


        switch (tutorialKey) {

            case TutorialConstants.KEY_ZOMBIE_MOVEMENT_RULES:
                ((TextView) view.findViewById(R.id.shambler_text)).setText("Shambler 5\"");
                ((TextView) view.findViewById(R.id.rager_text)).setText("Rager 8\"");
                ((TextView) view.findViewById(R.id.veteran_text)).setText("Veteran 4\"");


                Picasso.with(context).load(Zombie.PROFILE_PIC_PROFILE_URL).into(((ImageView) view.findViewById(R.id.shambler_portrait)));
                Picasso.with(context).load(Zombie.FAST_PROFILE_PIC_PROFILE_URL).into(((ImageView) view.findViewById(R.id.rager_portrait)));
                Picasso.with(context).load(Zombie.FAT_PROFILE_PIC_URL).into(((ImageView) view.findViewById(R.id.veteran_portrait)));
                break;

        }

        return view;
    }


    public String getTutorialIdFor(int phaseId, boolean isMine, int turnocount) {
        switch (phaseId) {
            case ACTION:
                if (isMine) {
                    if (turnocount > 1) {
                        return TutorialConstants.KEY_ACTION_PHASE_OVERVIEW;
                    } else {
                        return TutorialConstants.KEY_FIRST_ACTION_PHASE_OVERVIEW;
                    }
                } else {
                    return TutorialConstants.KEY_ENEMY_TURN_OVERVIEW;
                }

            case MOVE:
                if (isMine) {
                    return TutorialConstants.KEY_MOVEMENT_PHASE_OVERVIEW;
                } else {
                    return TutorialConstants.KEY_ENEMY_TURN_OVERVIEW;
                }
            case ASSIGN_ACTIONS:
                if (isMine) {
                    return TutorialConstants.KEY_COMMAND_PHASE_OVERVIEW;
                } else {
                    return TutorialConstants.KEY_ENEMY_TURN_OVERVIEW;
                }
            case ZOMBIES:
                if (isMine) {
                    return TutorialConstants.KEY_ZOMBIE_PHASE_OVERVIEW;
                } else {
                    return TutorialConstants.KEY_ZOMBIE_PHASE_OVERVIEW;
                }
            case PRE_GAME:
                return TutorialConstants.KEY_PRE_GAME_OVERVIEW;
            default:
                throw new AssertionError("Phase " + getLabel(phaseId) + " has no tutoral");
        }
    }

    public String getTitleFor(String key) {
        return TUTORIAL_KEY_TO_NAME.get(key);
    }

    private int getTutorialViewId(String tutorialKey) {

        switch (tutorialKey) {
            case TutorialConstants.KEY_TURN_STRUCTURE:
                return R.layout.tutorial_turn_structure;
            case TutorialConstants.KEY_PRE_GAME_OVERVIEW:
                return R.layout.tutorial_pre_game;
            case TutorialConstants.KEY_FIRST_ACTION_PHASE_OVERVIEW:
                return R.layout.tutorial_first_action_phase;
            case TutorialConstants.KEY_ACTION_PHASE_OVERVIEW:
                return R.layout.tutorial_action_phase;
            case TutorialConstants.KEY_GAME_UI_OVERVIEW:
                return R.layout.tutorial_game_ui;
            case TutorialConstants.KEY_MOVEMENT_PHASE_OVERVIEW:
                return R.layout.tutorial_movement_phase;
            case TutorialConstants.KEY_COMMAND_PHASE_OVERVIEW:
                return R.layout.tutorial_command_phase;
            case TutorialConstants.KEY_ZOMBIE_PHASE_OVERVIEW:
                return R.layout.tutorial_zombie_phase;
            case TutorialConstants.KEY_ENEMY_TURN_OVERVIEW:
                return R.layout.tutorial_enemy_turn;
            case TutorialConstants.KEY_ZOMBIE_MOVEMENT_RULES:
                return R.layout.tutorial_zombie_movement_rules;

        }
        return -1;
    }

    public boolean isTutorialAlreadyShown(String key) {
        if (!isTutorialsEnabled()) {
            return true;
        }

        return prefs.getBoolean(KEY_TUTORIAL_SEEN_PREFIX + key, false);
    }


    public boolean isTutorialsEnabled() {
        return prefs.getBoolean(KEY_TUTORIAL_MODE_ACTIVE, true);
    }

    public void setTutorialsEnabled(boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_TUTORIAL_MODE_ACTIVE, enabled);
        editor.commit();
    }


    public void resetTuorialsSeen() {
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : ALL_TUTORIAL_KEYS) {
            editor.putBoolean(KEY_TUTORIAL_SEEN_PREFIX + key, false);
        }
        editor.commit();
    }
}
