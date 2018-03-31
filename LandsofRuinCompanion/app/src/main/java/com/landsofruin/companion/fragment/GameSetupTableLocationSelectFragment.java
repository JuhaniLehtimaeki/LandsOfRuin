package com.landsofruin.companion.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.map.MapView;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.ChangeStartingPositionTransition;
import com.landsofruin.companion.state.transition.PlayerReadySetupMapTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class GameSetupTableLocationSelectFragment extends Fragment {

    private MapView mapView;

    private ViewGroup playersSouthContainer;
    private ViewGroup playersNorthContainer;
    private ViewGroup playersNorthContainerContainer;
    private ViewGroup playersSouthContainerContainer;
    private TextView mapName;
    private TextView providerName;
    private TextView providerUrl;
    private View doneButton;
    private ImageView providerIcon;
    private TextView validationText;
    private MapState lastSelectedMap;

    public static GameSetupTableLocationSelectFragment newInstance() {
        GameSetupTableLocationSelectFragment fragment = new GameSetupTableLocationSelectFragment();
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
        View view = inflater.inflate(R.layout.fragment_game_setup_select_table_location, parent, false);

        mapName = (TextView) view.findViewById(R.id.table_name);
        providerName = (TextView) view.findViewById(R.id.provider_name);
        providerUrl = (TextView) view.findViewById(R.id.provider_url);
        providerIcon = (ImageView) view.findViewById(R.id.provider_icon);


        mapView = (MapView) view.findViewById(R.id.map);

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        playersSouthContainer = (ViewGroup) view.findViewById(R.id.players_south_container);
        playersNorthContainer = (ViewGroup) view.findViewById(R.id.players_north_container);

        playersNorthContainerContainer = (ViewGroup) view.findViewById(R.id.players_north_container_container);
        playersSouthContainerContainer = (ViewGroup) view.findViewById(R.id.players_south_container_container);


        playersSouthContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeStartingPositionTransition transition = new ChangeStartingPositionTransition(getGame().getMe().getIdentifier(), PlayerState.StartingPosition.SOUTH);
                sendToServer(transition);
            }
        });


        playersNorthContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeStartingPositionTransition transition = new ChangeStartingPositionTransition(getGame().getMe().getIdentifier(), PlayerState.StartingPosition.NORTH);
                sendToServer(transition);
            }
        });

        validationText = (TextView) view.findViewById(R.id.scenario_validation_text);
        doneButton = view.findViewById(R.id.done_button);
        doneButton.setSelected(getGame().getMe().isPreGameReady());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer(new PlayerReadySetupMapTransition(getGame().getMe().getIdentifier(), !doneButton.isSelected()));
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

        if (isPlayerPositionReady(game)) {
            doneButton.setEnabled(true);

            if (game.getMe().isPreGameReady()) {
                validationText.setText("Waiting for opponent");
            } else {
                validationText.setText("Press ready to confirm your selection");
            }

        } else {


            if (game.getMe().getStartingPosition() == null) {
                validationText.setText("Choose your table side");
            } else {
                validationText.setText("Waiting for others to choose table sire");
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


        final MapState map = game.getMap();
        if (mapView != null && map != null) {
            mapView.setGame(game);
            mapView.setMap(game.getMap());

            if (map.getBackgroundImageURL() != null) {

                Picasso.with(getActivity()).load(map.getBackgroundImageURL()).into(new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (getActivity() == null) {
                            return;
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.setBackground(new BitmapDrawable(getResources(), bitmap));
                        } else {
                            mapView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                        }

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                        mapView.setBackgroundResource(R.drawable.map_background);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            } else {
                mapView.setBackgroundResource(R.drawable.map_background);
            }
        }


        ArrayList<PlayerState> players = new ArrayList<>();
        players.addAll(game.getPlayers());


        playersSouthContainerContainer.removeAllViews();
        playersNorthContainerContainer.removeAllViews();

        boolean meAddedNorth = false;
        boolean meAddedSouth = false;

        for (final PlayerState player : players) {


            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_player_list_item_for_position, playersNorthContainerContainer, false);


            TextView playerView = (TextView) view.findViewById(R.id.player_name);
            playerView.setText(player.getName());


            if (player.getStartingPosition() == PlayerState.StartingPosition.NORTH) {
                playersNorthContainerContainer.addView(view);
                if (player.isMe()) {
                    meAddedNorth = true;
                }
            } else if (player.getStartingPosition() == PlayerState.StartingPosition.SOUTH) {
                playersSouthContainerContainer.addView(view);
                if (player.isMe()) {
                    meAddedSouth = true;
                }
            }
        }

        if (!meAddedNorth) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.map_position_add_me_here, playersNorthContainerContainer, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangeStartingPositionTransition transition = new ChangeStartingPositionTransition(getGame().getMe().getIdentifier(), PlayerState.StartingPosition.NORTH);
                    sendToServer(transition);
                }
            });
            playersNorthContainerContainer.addView(view);
        }

        if (!meAddedSouth) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.map_position_add_me_here, playersSouthContainerContainer, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangeStartingPositionTransition transition = new ChangeStartingPositionTransition(getGame().getMe().getIdentifier(), PlayerState.StartingPosition.SOUTH);
                    sendToServer(transition);
                }
            });
            playersSouthContainerContainer.addView(view);
        }

        if (map != null) {


            mapName.setText(map.getName());


            if (map.getProviderName() != null) {
                providerName.setText("by " + map.getProviderName());
                providerName.setVisibility(View.VISIBLE);
            } else {
                providerName.setVisibility(View.GONE);
            }


            if (map.getProviderURL() != null) {
                providerUrl.setText("Buy online");
                providerUrl.setPaintFlags(providerUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                providerUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(map.getProviderURL()));
                        startActivity(intent);
                    }
                });
                providerUrl.setVisibility(View.VISIBLE);
            } else {
                providerUrl.setVisibility(View.GONE);
            }

            if (map.getProviderIconURL() != null) {
                providerIcon.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(map.getProviderIconURL()).into(providerIcon);

            } else {
                providerIcon.setVisibility(View.GONE);
            }
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


    public boolean isPlayerPositionReady(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {
            if (player.getStartingPosition() == null) {
                return false;
            }
        }
        return true;
    }

    @Subscribe
    public void onMapChangedEvent(GameSetupTableSelectFragment.MapChangedEvent event) {
        this.lastSelectedMap = event.getMap();

    }

}
