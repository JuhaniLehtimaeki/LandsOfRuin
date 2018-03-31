package com.landsofruin.companion.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.gameruleobjects.scenario.PlayerRole;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.gameruleobjects.scenario.ScenarioObjective;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.ChangePlayerRoleTransition;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.scenarios.ScenariosManager;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ScenarioDetailsFragment extends Fragment {

    private Scenario scenario;
    private ViewGroup playerRolesContainer;
    private LayoutInflater inflater;

    public static ScenarioDetailsFragment newInstance(int scenarioId) {
        ScenarioDetailsFragment fragment = new ScenarioDetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt("id", scenarioId);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.scenario_details_fragment, parent, false);

        scenario = ScenariosManager.getInstance().getScenarioByID(getArguments().getInt("id"));
        ((TextView) view.findViewById(R.id.title)).setText(scenario.getName());
        ((TextView) view.findViewById(R.id.more_info)).setText(scenario.getDescription());

//        if (scenario.getScenarioImageUri() != null) {
//            Picasso.with(getActivity())
//                    .load(scenario.getScenarioImageUri())
//                    .into(((ImageView) view.findViewById(R.id.scenario_image)));
//        }

        playerRolesContainer = (ViewGroup) view.findViewById(R.id.player_roles_container);
        fillPlayerRoles();

        int minCount = scenario.getMinPlayerCount();
        int maxCount = scenario.getMaxPlayerCount();

        if (maxCount == -1) { // unlimited
            ((TextView) view.findViewById(R.id.number_of_players)).setText("For " + minCount + "+ players");
        } else if (maxCount == minCount) {
            ((TextView) view.findViewById(R.id.number_of_players)).setText("For " + minCount + " players");
        } else {
            ((TextView) view.findViewById(R.id.number_of_players)).setText("For " + minCount + " to " + maxCount + " players");
        }


        ((TextView) view.findViewById(R.id.threat_level)).setText("Threat level: " + scenario.getInitialThreatLevel() + " (0-20)");
        ((TextView) view.findViewById(R.id.danger_level)).setText("Area Danger level: " + scenario.getAreaDangerLevel() + " (0-100)");

        return view;
    }


    private void fillPlayerRoles() {
        playerRolesContainer.removeAllViews();

        for (int roleId : scenario.getPlayerRoles()) {

            final PlayerRole playerRole = LookupHelper.getInstance().getPlayerRoleFor(roleId);
            View view = inflater.inflate(R.layout.game_setup_one_player_role, playerRolesContainer, false);

            ((TextView) view.findViewById(R.id.name)).setText(playerRole.getName());

            int minCount = playerRole.getMinCount();
            int maxCount = playerRole.getMaxCount();

            if (maxCount == 0) { // unlimited
                ((TextView) view.findViewById(R.id.count)).setText("(" + minCount + "+)");
            } else if (maxCount == minCount) {
                ((TextView) view.findViewById(R.id.count)).setText("(" + minCount + ")");
            } else {
                ((TextView) view.findViewById(R.id.count)).setText("(" + minCount + " - " + maxCount + ")");
            }


            ((TextView) view.findViewById(R.id.description)).setText(playerRole.getDescription());
            ((TextView) view.findViewById(R.id.max_gear)).setText("" + playerRole.getMaxGearValue());


            ViewGroup objectivesContainer = ((ViewGroup) view.findViewById(R.id.objectives));
            playerRolesContainer.addView(view);

            List<Integer> objectives = playerRole.getScenarioObjectives();

            for (int i = 0; i < objectives.size(); ++i) {
                ScenarioObjective objective = ScenariosManager.getInstance().getScenarioObjectiveByID(objectives.get(i));

                View objectiveView = inflater.inflate(R.layout.game_setup_one_scenario_objective, objectivesContainer, false);

                TextView objectiveText = (TextView) objectiveView.findViewById(R.id.objective_checkbox);
                objectiveText.setText(objective.getName());
                ((TextView) objectiveView.findViewById(R.id.objective_description)).setText(objective.getDescription());


                objectivesContainer.addView(objectiveView);
            }

            final GameState gamestate = ((BaseGameActivity) getActivity()).getGame();

            ViewGroup players = (ViewGroup) view.findViewById(R.id.players);

            ArrayList<PlayerState> playersList = new ArrayList<>();
            playersList.addAll(gamestate.getPlayers());

            boolean isMeAdded = false;
            for (final PlayerState player : playersList) {
                if (player.getScenarioPlayerRole() == playerRole.getId()) {

                    View playerRoleView = LayoutInflater.from(getActivity()).inflate(R.layout.item_player_list_item_for_role, players, false);


                    TextView playerView = (TextView) playerRoleView.findViewById(R.id.player_name);

                    playerView.setText(player.getName());


                    players.addView(playerRoleView);

                    if (player.isMe()) {
                        isMeAdded = true;
                    }
                }
            }

            if (!isMeAdded) {
                View addMeHereButton = LayoutInflater.from(getActivity()).inflate(R.layout.scenario_details_add_me_here, players, false);
                addMeHereButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangePlayerRoleTransition transition = new ChangePlayerRoleTransition(gamestate.getMe().getIdentifier(), playerRole.getId());
                        ((BaseGameActivity) getActivity()).sendToServer(transition);
                    }
                });

                players.addView(addMeHereButton);
            }


            if (getActivity() instanceof GameSetupActivity) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangePlayerRoleTransition transition = new ChangePlayerRoleTransition(gamestate.getMe().getIdentifier(), playerRole.getId());
                        ((BaseGameActivity) getActivity()).sendToServer(transition);
                    }
                });

            }
        }
    }


    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        fillPlayerRoles();
    }
}
