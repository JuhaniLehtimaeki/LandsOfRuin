package com.landsofruin.companion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.transition.ChangeMapTransition;
import com.landsofruin.companion.state.transition.ReadySetupMapSelectionTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

public class GameSetupMapSelectFragment extends Fragment {

    private View doneButton;
    private TextView validationText;
    private MapState lastSelectedMap;

    public static GameSetupMapSelectFragment newInstance() {
        GameSetupMapSelectFragment fragment = new GameSetupMapSelectFragment();
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
        View view = inflater.inflate(R.layout.fragment_game_setup_map_select, parent, false);


        doneButton = view.findViewById(R.id.done_button);
        validationText = (TextView) view.findViewById(R.id.scenario_validation_text);

        if (isAdmin()) {
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToServer(new ReadySetupMapSelectionTransition());
                }
            });
            validationText.setText("Press ready to confirm your selection");
        } else {
            view.findViewById(R.id.non_admin_info).setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.fragment_container).setVisibility(View.GONE);
            validationText.setText("Waiting for the admin to select the map");
        }
        doneButton.setEnabled(false);


        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        TextView scenatioTitleText = (TextView) view.findViewById(R.id.scenario_title);
        TextView mapTitleText = (TextView) view.findViewById(R.id.map_title);
        TextView teamTitleText = (TextView) view.findViewById(R.id.team_title);
        TextView overviewTitleText = (TextView) view.findViewById(R.id.overview_title);
        TextView startGameTitleText = (TextView) view.findViewById(R.id.start_game_title);

        scenatioTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        mapTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        teamTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        overviewTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        startGameTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));

        updateUI();
        return view;
    }


    private void validateState(GameState game) {

        if (game.getMap() != null) {

            if (isAdmin()) {
                doneButton.setEnabled(true);
                validationText.setText("Press ready to confirm your selection");
            } else {
                validationText.setText("Waiting for the admin to select the map");
            }
        } else {
            validationText.setVisibility(View.VISIBLE);
            if (isAdmin()) {
                validationText.setText("Please select a map");
            } else {
                validationText.setText("Waiting for the admin to select the map");
            }

            doneButton.setEnabled(false);
        }
    }

    private GameState getGame() {
        return ((GameSetupActivity) getActivity()).getGame();
    }

    private void sendToServer(Transition transition) {
        ((GameSetupActivity) getActivity()).sendToServer(transition);
    }

    private boolean isAdmin() {
        return ((GameSetupActivity) getActivity()).isAdmin();
    }

    private void updateUI() {
        final GameState game = getGame();
        validateState(game);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();
    }


    @Subscribe
    public void onMapChangedEvent(GameSetupTableSelectFragment.MapChangedEvent event) {
        this.lastSelectedMap = event.getMap();
        ChangeMapTransition transition = new ChangeMapTransition(lastSelectedMap);
        sendToServer(transition);

        validateState(getGame());

    }

}
