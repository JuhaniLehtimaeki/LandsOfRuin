package com.landsofruin.companion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.ChangeScenarioTransition;
import com.landsofruin.companion.state.transition.PlayerReadySetupScenarioTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.scenarios.ScenariosManager;
import com.squareup.otto.Subscribe;

public class GameSetupScenarioRoleSelectFragment extends Fragment {


    private int currentScenario = -1;
    private View doneButton;
    private TextView validationText;


    public static GameSetupScenarioRoleSelectFragment newInstance() {
        GameSetupScenarioRoleSelectFragment fragment = new GameSetupScenarioRoleSelectFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_setup_select_scenario_role, parent, false);

        view.findViewById(R.id.non_admin_fragment_container);


        if (getGame() == null) {
            getActivity().finish();
            return view;
        }


        validationText = (TextView) view.findViewById(R.id.scenario_validation_text);
        doneButton = view.findViewById(R.id.done_button);


        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        doneButton.setSelected(getGame().getMe().isPreGameReady());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer(new PlayerReadySetupScenarioTransition(getGame().getMe().getIdentifier(), !doneButton.isSelected()));
            }
        });

        TextView scenatioTitleText = (TextView) view.findViewById(R.id.scenario_title);
        TextView mapTitleText = (TextView) view.findViewById(R.id.map_title);
        TextView teamTitleText = (TextView) view.findViewById(R.id.team_title);
        TextView overviewTitleText = (TextView) view.findViewById(R.id.overview_title);
        TextView startGameTitleText = (TextView) view.findViewById(R.id.start_game_title);

        scenatioTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        mapTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        teamTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        overviewTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        startGameTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));

        updateUI();

        return view;
    }


    private void validateState(GameState game) {
        validationText.setVisibility(View.INVISIBLE);
        if (isPlayerRolesReady(game)) {
            doneButton.setEnabled(true);

            if (game.getMe().isPreGameReady()) {
                validationText.setVisibility(View.VISIBLE);
                validationText.setText("Waiting for opponent");
            } else {
                validationText.setVisibility(View.VISIBLE);
                validationText.setText("Press ready to confirm your selection");
            }

        } else {
            validationText.setVisibility(View.VISIBLE);
            validationText.setText("Scenario player roles incorrect");
            doneButton.setEnabled(false);
        }
    }

    private GameState getGame() {
        return ((GameSetupActivity) getActivity()).getGame();
    }

    private void sendToServer(Transition transition) {
        ((GameSetupActivity) getActivity()).sendToServer(transition);
    }

    private void onPickScenario(int position) {
        if (getActivity() == null) {
            return;
        }

        Scenario scenario = ScenariosManager.getInstance().getScenarios().get(position);
        int scenarioId = scenario.getId();

        if (LookupHelper.getInstance().getScenarioFor(getGame().getScenario()) == null || scenarioId != LookupHelper.getInstance().getScenarioFor(getGame().getScenario()).getId()) {
            ChangeScenarioTransition transition = new ChangeScenarioTransition(scenarioId);
            sendToServer(transition);
        }
    }


    private void onPickScenarioWithId(int id) {
        if (getActivity() == null) {
            return;
        }


        if (LookupHelper.getInstance().getScenarioFor(getGame().getScenario()) == null || id != LookupHelper.getInstance().getScenarioFor(getGame().getScenario()).getId()) {
            ChangeScenarioTransition transition = new ChangeScenarioTransition(id);
            sendToServer(transition);
        }
    }


    private boolean isAdmin() {
        return ((GameSetupActivity) getActivity()).isAdmin();
    }

    private int getSelectedIndex() {
        GameState game = getGame();

        if (LookupHelper.getInstance().getScenarioFor(game.getScenario()) == null) {
            return -1;
        }

        for (int i = 0; i < ScenariosManager.getInstance().getScenarios().size(); ++i) {
            if (ScenariosManager.getInstance().getScenarios().get(i).getId() == game.getScenario()) {
                return i;

            }
        }
        return -1;
    }

    private void updateUI() {
        GameState game = getGame();

        if (LookupHelper.getInstance().getScenarioFor(game.getScenario()) != null) {
            currentScenario = game.getScenario();

            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ScenarioDetailsFragment fragment = ScenarioDetailsFragment.newInstance(currentScenario);
            fragmentTransaction.replace(R.id.non_admin_fragment_container, fragment);
            fragmentTransaction.commit();
        }

        doneButton.setSelected(getGame().getMe().isPreGameReady());
        validateState(game);

    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();
    }


    public boolean isPlayerRolesReady(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {
            if (player.getScenarioPlayerRole() == -1) {
                return false;
            }
        }
        return gameState.isScenarioPlayerRolesReady();
    }


}
