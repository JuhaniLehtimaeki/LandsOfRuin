package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.CancelEndGameConfirmationTransition;
import com.landsofruin.companion.state.transition.EndGameConfirmationSetPlayerReadyTransition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

/**
 *
 */
public class EndGameConfirmationOverlayFragment extends Fragment {

    private BaseGameActivity activity;
    private ViewGroup rootView;
    private View readyButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.confirm_end_game_overlay_fragment,
                parent, false);


        rootView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity) getActivity()).sendToServer(new CancelEndGameConfirmationTransition());
            }
        });

        readyButton = rootView.findViewById(R.id.ready_button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlayerState player = ((GameActivity) getActivity()).getGame().getMe();

                ((GameActivity) getActivity()).sendToServer(new EndGameConfirmationSetPlayerReadyTransition(player.getIdentifier(), !player.getConfirmEndGameState().isPlayerReady()));

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);


    }


    private void updateUI() {
        GameState game = activity.getGame();


        if (shouldBeVisible(game)) {
            showOverlay();
        } else {
            hideOverlay();
            return;
        }

        readyButton.setSelected(game.getMe().getConfirmEndGameState().isPlayerReady());
    }


    private void hideOverlay() {
        if (rootView.getVisibility() == View.GONE) {
            return;
        }

        rootView.setVisibility(View.GONE);

    }


    private void showOverlay() {
        if (rootView.getVisibility() == View.VISIBLE) {
            return;
        }

        rootView.setVisibility(View.VISIBLE);


    }


    private boolean shouldBeVisible(GameState game) {

        for (PlayerState player : game.getPlayers()) {
            if (player.getConfirmEndGameState() != null) {
                return true;
            }
        }


        return false;
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


}
