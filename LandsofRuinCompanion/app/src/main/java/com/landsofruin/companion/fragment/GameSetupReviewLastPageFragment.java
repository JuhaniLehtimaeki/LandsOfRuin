package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.map.MapView;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.PlayerReadyTransition;
import com.landsofruin.companion.state.transition.StartGameTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.scenarios.ScenariosManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class GameSetupReviewLastPageFragment extends Fragment {

    private MapView mapView;
    private View readyButton;

    private LinearLayout characterListLayout;
    private TextView scenarioView;
    private TextView titleView;
    private TextView gameModeView;
    private TextView scenarioValidationText;
    private ViewGroup playersContainer;
    private View adminStartView;

    public static GameSetupReviewLastPageFragment newInstance() {
        GameSetupReviewLastPageFragment fragment = new GameSetupReviewLastPageFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
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
        View view = inflater.inflate(R.layout.fragment_game_setup_review_last_page, parent, false);

        scenarioValidationText = (TextView) view.findViewById(R.id.scenario_validation_text);

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        playersContainer = (ViewGroup) view.findViewById(R.id.players);
        readyButton = view.findViewById(R.id.ready_button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerReady(!readyButton.isSelected());
            }
        });


        final View startButton = view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                onStartGame();
            }
        });

        mapView = (MapView) view.findViewById(R.id.map_preview);
        gameModeView = ((TextView) view.findViewById(R.id.game_mode));


        adminStartView = view.findViewById(R.id.admin_start_overlay);


        if (isAdmin()) {

            view.findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeTitle();
                }
            });
        } else {
            view.findViewById(R.id.title_subtext).setVisibility(View.INVISIBLE);
        }


        characterListLayout = (LinearLayout) view.findViewById(R.id.character_list);
        scenarioView = (TextView) view.findViewById(R.id.scenario_title);
        titleView = (TextView) view.findViewById(R.id.title);
        updateUI();


        TextView scenatioTitleText = (TextView) view.findViewById(R.id.scenario_tab_title);
        TextView mapTitleText = (TextView) view.findViewById(R.id.map_title);
        TextView teamTitleText = (TextView) view.findViewById(R.id.team_title);
        TextView overviewTitleText = (TextView) view.findViewById(R.id.overview_title);
        TextView startGameTitleText = (TextView) view.findViewById(R.id.start_game_title);

        scenatioTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        mapTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        teamTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        overviewTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        startGameTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));

        return view;
    }


    private void onChangeTitle() {
        Bundle arguments = new Bundle();
        arguments.putString(EditGameTitleDialogFragment.ARGUMENT_TITLE, getGame().getTitle());

        EditGameTitleDialogFragment fragment = new EditGameTitleDialogFragment();
        fragment.setArguments(arguments);

        fragment.show(getChildFragmentManager(), "dialog_game_title");
    }

    private GameState getGame() {
        return ((GameSetupActivity) getActivity()).getGame();
    }


    private void sendToServer(Transition transition) {
        ((GameSetupActivity) getActivity()).sendToServer(transition);
    }


    private void onStartGame() {
        StartGameTransition transition = new StartGameTransition();
        sendToServer(transition);

    }


    public void playerReady(boolean isReady) {
        PlayerReadyTransition transition = new PlayerReadyTransition(PlayerAccount.getUniqueIdentifier(), isReady);
        sendToServer(transition);
    }

    private boolean isAdmin() {
        return ((GameSetupActivity) getActivity()).isAdmin();
    }

    private void updateUI() {
        GameState game = getGame();

        Scenario scenario = LookupHelper.getInstance().getScenarioFor(game.getScenario());
        if (scenario == null) {
            scenarioView.setText("No scenario selected.");
        } else {
            scenarioView.setText(scenario.getName());
        }


        TeamState team = game.getMe() != null ? game.getMe().getTeam() : null;


        titleView.setText(game.getTitle());

        readyButton.setSelected(game.getMe().isReady());

        mapView.setGame(game);
        mapView.setMap(game.getMap());

        if (game.getMap() != null && game.getMap().getBackgroundImageURL() != null) {
            Picasso.with(getActivity()).load(game.getMap().getBackgroundImageURL()).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.setBackground(new BitmapDrawable(getResources(), bitmap));
                    } else {
                        mapView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (team != null) {


            characterListLayout.removeAllViews();

            for (CharacterState character : team.listAllTypesCharacters()) {
                View view = inflater.inflate(R.layout.item_player_character, characterListLayout, false);

                ImageView profileView = (ImageView) view.findViewById(R.id.profile_picture);

                Picasso.with(this.getActivity())
                        .load(character.getProfilePictureUri())
                        .into(profileView);
                characterListLayout.addView(view);
            }
        }

        if (getGame().getGameMode() == GameState.GAME_MODE_BASIC) {
            gameModeView.setText("Basic game rule set");
        } else if (getGame().getGameMode() == GameState.GAME_MODE_ADVANCED) {
            gameModeView.setText("Advanced rule set");
        } else if (getGame().getGameMode() == GameState.GAME_MODE_ONLINE) {
            gameModeView.setText("Online game with advanced rule set");
        }


        playersContainer.removeAllViews();
        for (PlayerState player : game.getPlayers()) {
            View onePLayerView = inflater.inflate(R.layout.item_player_list_item, playersContainer, false);


            TextView playerView = (TextView) onePLayerView.findViewById(R.id.player_name);
            TextView playerRoleView = (TextView) onePLayerView.findViewById(R.id.player_role_name);
            TextView startingPosition = (TextView) onePLayerView.findViewById(R.id.starting_position_name);


            if (player.getStartingPosition() == null) {
                startingPosition.setText("map position not set");
            } else if (player.getStartingPosition() == PlayerState.StartingPosition.EAST) {
                startingPosition.setText("Map position: East");
            } else if (player.getStartingPosition() == PlayerState.StartingPosition.WEST) {
                startingPosition.setText("Map position: West");
            } else if (player.getStartingPosition() == PlayerState.StartingPosition.NORTH) {
                startingPosition.setText("Map position: North");
            } else if (player.getStartingPosition() == PlayerState.StartingPosition.SOUTH) {
                startingPosition.setText("Map position: South");
            }

            if (player.getScenarioPlayerRole() == -1 || ScenariosManager.getInstance().getPlayerRoleByID(player.getScenarioPlayerRole()) == null) {
                playerRoleView.setText("player role not selected");
            } else {
                playerRoleView.setText("Player Role: " + ScenariosManager.getInstance().getPlayerRoleByID(player.getScenarioPlayerRole()).getName());

            }

            playerView.setText(player.getName() + (player.isMe() ? "\n(you)" : ""));

            TextView status = (TextView) onePLayerView.findViewById(R.id.status);

            status.setTextColor(player.isConnected() ? 0xFF4CAF50 : 0xFFF44336);
            status.setText(player.isConnected() ? "Connected" : "Not connected");

            playersContainer.addView(onePLayerView);
        }


        boolean everyoneReady = true;

        for (PlayerState player : game.getPlayers()) {
            if (!player.isReady()) {
                everyoneReady = false;
                break;
            }
        }

        if (everyoneReady && isAdmin()) {
            adminStartView.setVisibility(View.VISIBLE);
        } else {
            adminStartView.setVisibility(View.GONE);
        }
    }


    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();


    }


}
