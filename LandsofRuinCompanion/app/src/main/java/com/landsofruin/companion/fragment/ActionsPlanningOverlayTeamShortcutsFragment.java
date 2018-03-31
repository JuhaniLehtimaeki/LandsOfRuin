package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.transition.AssignBulkActionTransition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;

/**
 * Fragment showing the game ended state
 */
public class ActionsPlanningOverlayTeamShortcutsFragment extends Fragment {

    private BaseGameActivity activity;
    private ViewGroup rootView;
    private CharacterState character;

    private LayoutInflater inflater;
    private ViewGroup quickActions;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        rootView = (ViewGroup) inflater.inflate(R.layout.actions_planning_team_shortcuts_overlay_fragment,
                parent, false);


        quickActions = (ViewGroup) rootView.findViewById(R.id.quick_actions);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
        updateUI();

    }

    @Override
    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        updateUI();
    }

    private boolean shouldBeVisible(GameState game) {

        // disabled for now
        return false;

//        if (!game.getPhase().isMine()) {
//            return false;
//        }
//
//        if (game.getPhase().getPrimaryPhase() != PrimaryPhase.ASSIGN_ACTIONS) {
//            return false;
//        }
//        return this.character == null;


    }

    private void updateUI() {
        GameState game = activity.getGame();


        if (shouldBeVisible(game)) {
            rootView.setVisibility(View.VISIBLE);
        } else {
            rootView.setVisibility(View.GONE);
            return;
        }

        quickActions.removeAllViews();
        TeamState team = activity.getGame().getMe().getTeam();
        if (team.getTeamStatus(game) >= TeamState.TEAM_STATUS_NORMAL) {


            View view = inflater.inflate(R.layout.one_quick_action, quickActions, false);

            ImageView icon = (ImageView) view
                    .findViewById(R.id.action_icon);
            icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(Action.ACTION_ID_AIM_AND_SHOOTING)));


            ((TextView) view.findViewById(R.id.title)).setText("Hold the line");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinkedList<Integer> actionIds = new LinkedList<>();
                    actionIds.add(18);
                    ((BaseGameActivity) getActivity())
                            .sendToServer(new AssignBulkActionTransition(activity.getGame().getMe().getIdentifier(), actionIds));

                }
            });

            ((TextView) view.findViewById(R.id.description)).setText("Aim and fire a ranged weapon. No movement.");
            ((TextView) view.findViewById(R.id.aps)).setText("20 AP");

            quickActions.addView(view);

        }
        if (team.getTeamStatus(game) >= TeamState.TEAM_STATUS_CONFUSION) {

            View view = inflater.inflate(R.layout.one_quick_action, quickActions, false);


            ImageView icon = (ImageView) view
                    .findViewById(R.id.action_icon);
            icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(Action.ACTION_ID_SHOOTING)));


            ((TextView) view.findViewById(R.id.title)).setText("Advance");
            ((TextView) view.findViewById(R.id.aps)).setText("10 AP");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LinkedList<Integer> actionIds = new LinkedList<>();
                    actionIds.add(2);
                    ((BaseGameActivity) getActivity())
                            .sendToServer(new AssignBulkActionTransition(activity.getGame().getMe().getIdentifier(), actionIds));


                }
            });

            ((TextView) view.findViewById(R.id.description)).setText("Move and fire a ranged weapon.");
            quickActions.addView(view);


            view = inflater.inflate(R.layout.one_quick_action, quickActions, false);


            icon = (ImageView) view
                    .findViewById(R.id.action_icon);
            icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(Action.ACTION_ID_CC)));

            ((TextView) view.findViewById(R.id.aps)).setText("5 AP");


            ((TextView) view.findViewById(R.id.title)).setText("Charge!");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LinkedList<Integer> actionIds = new LinkedList<Integer>();
                    actionIds.add(3);
                    ((BaseGameActivity) getActivity())
                            .sendToServer(new AssignBulkActionTransition(activity.getGame().getMe().getIdentifier(), actionIds));


                }
            });

            ((TextView) view.findViewById(R.id.description)).setText("Move and fight in Close Combat.");
            quickActions.addView(view);

        }
        if (team.getTeamStatus(game) >= TeamState.TEAM_STATUS_PANIC) {
//            View view = inflater.inflate(R.layout.one_quick_action, quickActions, false);
//
//            ((Button) view.findViewById(R.id.quick_action_button)).setText("Flee");
//            view.findViewById(R.id.quick_action_button).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    LinkedList<Integer> actionIds = new LinkedList<Integer>();
//                    actionIds.add(1001);
//                    ((BaseGameActivity) getActivity())
//                            .sendToServer(new AssignBulkActionTransition(activity.getGame().getMe().getIdentifier(), actionIds));
//
//
//                }
//            });
//
//            ((TextView) view.findViewById(R.id.description)).setText("Assign flee.");
//            quickActions.addView(view);
        }


    }

    @Subscribe
    public void onCharacterSelectedEvent(CharacterSelectedEvent event) {
        this.character = event.getCharacter();
        updateUI();
    }

}
