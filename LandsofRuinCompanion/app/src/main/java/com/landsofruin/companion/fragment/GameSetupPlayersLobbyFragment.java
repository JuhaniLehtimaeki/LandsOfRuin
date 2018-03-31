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
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.ReadySetupPlayerLobbyTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.utils.NetworkUtils;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

import java.io.File;

public class GameSetupPlayersLobbyFragment extends Fragment {

    private View doneButton;
    private ViewGroup playersContainer;

    public static GameSetupPlayersLobbyFragment newInstance() {
        GameSetupPlayersLobbyFragment fragment = new GameSetupPlayersLobbyFragment();
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
        View view = inflater.inflate(R.layout.fragment_game_setup_player_lobby, parent, false);

        doneButton = view.findViewById(R.id.done_button);

        if (isAdmin()) {

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToServer(new ReadySetupPlayerLobbyTransition());
                }
            });

            view.findViewById(R.id.non_admin_text).setVisibility(View.GONE);

        } else {
            doneButton.setEnabled(false);
            doneButton.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.admin_text).setVisibility(View.GONE);
        }


        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        playersContainer = (ViewGroup) view.findViewById(R.id.players_container);

        ((TextView) view.findViewById(R.id.ip_address)).setText("Your IP address: " +
                NetworkUtils.getIPAddress(true) + "\nPort: " + NetworkUtils.lastPort);

        updateUI();

// check if we're on Bluestack. This is a bit if a hack though
        File test = new File("/data/Bluestacks.prop");
        if (isAdmin() && (test.exists() || isx86Port())) {
            view.findViewById(R.id.bluestacks_warning).setVisibility(View.VISIBLE);
        }


        if(!isAdmin()){
            view.findViewById(R.id.ip_hint).setVisibility(View.GONE);
            view.findViewById(R.id.ip_address).setVisibility(View.GONE);
        }

        return view;
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
        GameState game = getGame();


        LayoutInflater inflater = LayoutInflater.from(getActivity());
        playersContainer.removeAllViews();
        for (PlayerState player : game.getPlayers()) {
            View onePLayerView = inflater.inflate(R.layout.item_player_list_item_lobby, playersContainer, false);

            TextView playerView = (TextView) onePLayerView.findViewById(R.id.player_name);
            TextView status = (TextView) onePLayerView.findViewById(R.id.status);


            playerView.setText(player.getName() + (player.isMe() ? "\n(you)" : ""));


            status.setTextColor(player.isConnected() ? 0xFF4CAF50 : 0xFFF44336);
            status.setText(player.isConnected() ? "Connected" : "Not connected");

            playersContainer.addView(onePLayerView);
        }


    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();
    }

    public static boolean isx86Port() {
        String kernelVersion = System.getProperty("os.version");
        if (kernelVersion != null && kernelVersion.contains("x86")) // for BlueStacks returns "2.6.38-android-x86+"
            return true;
        return false;
    }

}
