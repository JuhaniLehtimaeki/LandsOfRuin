package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.cards.CardLocationsHolder;
import com.landsofruin.companion.cards.events.UpdateCardPositionToHolderEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.PrepareEndGameState;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.scenario.ScenarioObjective;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.transition.EndGamePreparationObjectiveSetTransition;
import com.landsofruin.companion.state.transition.EndGamePreparationSetPlayerReadyTransition;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.scenarios.ScenariosManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class EndGamePreparationOverlayFragment extends Fragment {

    private BaseGameActivity activity;
    private ViewGroup rootView;
    private LayoutInflater inflater;
    private ViewGroup myObjectivesContainer;
    private ViewGroup opponentObjectivesContainer;
    private View readyButton;
    private ViewGroup myTeamContainer;
    private ViewGroup opponentTeamContainer;


    private HashMap<String, View> characterToViewMap = new HashMap<>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.prepare_end_game_overlay_fragment,
                parent, false);

        this.inflater = inflater;


        myObjectivesContainer = (ViewGroup) rootView.findViewById(R.id.my_objectives);
        myTeamContainer = (ViewGroup) rootView.findViewById(R.id.my_team);
        opponentObjectivesContainer = (ViewGroup) rootView.findViewById(R.id.opponent_objectives);
        opponentTeamContainer = (ViewGroup) rootView.findViewById(R.id.opponent_team);

        readyButton = rootView.findViewById(R.id.ready_button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlayerState player = ((GameActivity) getActivity()).getGame().getMe();

                ((GameActivity) getActivity()).sendToServer(new EndGamePreparationSetPlayerReadyTransition(player.getIdentifier(), !player.getPrepareEndGameState().isPlayerReady()));

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
        updateUI();

    }


    private void updateTeams(GameState game) {
        myTeamContainer.removeAllViews();


        for (CharacterState characterState : game.getMe().getTeam().listAllTypesCharacters()) {
            final View oneCharacterView = getActivity().getLayoutInflater().inflate(
                    R.layout.prepare_end_game_one_character, myTeamContainer, false);


            characterToViewMap.put(characterState.getIdentifier(), oneCharacterView);

            TextView characterNameText = ((TextView) oneCharacterView.findViewById(R.id.target_name));
            characterNameText.setText(characterState.getName());


            if (characterState.isDown()) {
                characterNameText.setPaintFlags(characterNameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            Picasso.with(this.getActivity())
                    .load(characterState.getProfilePictureUri())
                    .into((ImageView) oneCharacterView.findViewById(R.id.target_portrait));

            ViewGroup effectIconContainer = (ViewGroup) oneCharacterView
                    .findViewById(R.id.effect_icon_container);
            effectIconContainer.removeAllViews();
            for (final CharacterEffect characterEffect : characterState.getCharacterEffects()) {

                View oneIconView = getActivity().getLayoutInflater().inflate(
                        R.layout.one_effect, effectIconContainer, false);

                final ImageView icon = (ImageView) oneIconView
                        .findViewById(R.id.effect_icon);
                icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(characterEffect.getIcon()));
                effectIconContainer.addView(oneIconView);
            }


            myTeamContainer.addView(oneCharacterView);
        }

        opponentTeamContainer.removeAllViews();
        for (PlayerState playerState : game.getPlayers()) {
            if (playerState.isMe()) {
                continue;
            }


            for (CharacterState characterState : playerState.getTeam().listAllTypesCharacters()) {
                final View oneCharacterView = getActivity().getLayoutInflater().inflate(
                        R.layout.prepare_end_game_one_character, opponentTeamContainer, false);

                characterToViewMap.put(characterState.getIdentifier(), oneCharacterView);


                TextView characterNameText = ((TextView) oneCharacterView.findViewById(R.id.target_name));
                characterNameText.setText(characterState.getName());
                if (characterState.isDown()) {
                    characterNameText.setPaintFlags(characterNameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                Picasso.with(this.getActivity())
                        .load(characterState.getProfilePictureUri())
                        .into((ImageView) oneCharacterView.findViewById(R.id.target_portrait));

                ViewGroup effectIconContainer = (ViewGroup) oneCharacterView
                        .findViewById(R.id.effect_icon_container);
                effectIconContainer.removeAllViews();
                for (final CharacterEffect characterEffect : characterState.getCharacterEffects()) {

                    View oneIconView = getActivity().getLayoutInflater().inflate(
                            R.layout.one_effect, effectIconContainer, false);

                    final ImageView icon = (ImageView) oneIconView
                            .findViewById(R.id.effect_icon);
                    icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(characterEffect.getIcon()));
                    effectIconContainer.addView(oneIconView);
                }


                opponentTeamContainer.addView(oneCharacterView);
            }
        }

    }


    private void updateOpponentObjectives(GameState game) {
        opponentObjectivesContainer.removeAllViews();

//TODO if empty
        for (PlayerState player : game.getPlayers()) {
            if (player.isMe()) {
                continue;
            }

            List<Integer> objectives = ScenariosManager.getInstance().getPlayerRoleByID(player.getScenarioPlayerRole()).getScenarioObjectives();
            final PrepareEndGameState engamestate = player.getPrepareEndGameState();

            for (int i = 0; i < objectives.size(); ++i) {
                ScenarioObjective objective = ScenariosManager.getInstance().getScenarioObjectiveByID(objectives.get(i));

                View view = inflater.inflate(R.layout.one_scenario_objective, opponentObjectivesContainer, false);

                CheckBox checkbox = (CheckBox) view.findViewById(R.id.objective_checkbox);
                checkbox.setEnabled(false);
                checkbox.setText(objective.getName());
                ((TextView) view.findViewById(R.id.objective_description)).setText(objective.getDescription());

                checkbox.setChecked(engamestate.isScenarioGoalSet(objective.getId()));
                opponentObjectivesContainer.addView(view);
            }
        }

    }

    private void updateMyObjectives(final GameState game) {
        myObjectivesContainer.removeAllViews();


        List<Integer> objectives = ScenariosManager.getInstance().getPlayerRoleByID(game.getMe().getScenarioPlayerRole()).getScenarioObjectives();

        final PrepareEndGameState engamestate = game.getMe().getPrepareEndGameState();

        //TODO if empty

        for (int i = 0; i < objectives.size(); ++i) {
            final ScenarioObjective objective = ScenariosManager.getInstance().getScenarioObjectiveByID(objectives.get(i));

            View view = inflater.inflate(R.layout.one_scenario_objective, myObjectivesContainer, false);

            final CheckBox checkbox = (CheckBox) view.findViewById(R.id.objective_checkbox);
            checkbox.setText(objective.getName());
            checkbox.setChecked(engamestate.isScenarioGoalSet(objective.getId()));
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    ((GameActivity) getActivity()).sendToServer(new EndGamePreparationObjectiveSetTransition(game.getMe().getIdentifier(), objective.getId(), isChecked));

                }
            });


            ((TextView) view.findViewById(R.id.objective_description)).setText(objective.getDescription());

            myObjectivesContainer.addView(view);
        }

    }

    private void updateUI() {
        GameState game = activity.getGame();


        if (shouldBeVisible(game)) {
            showOverlay();
        } else {
            hideOverlay();
            return;
        }

        readyButton.setSelected(game.getMe().getPrepareEndGameState().isPlayerReady());


        updateMyObjectives(game);
        updateOpponentObjectives(game);

        updateTeams(game);
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
            if (player.getPrepareEndGameState() != null) {
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


    @Subscribe
    public void onUpdateCardPositionToHolderEvent(UpdateCardPositionToHolderEvent event) {
        updateCharacterPositions();
    }

    private void updateCharacterPositions() {

        if (!isEndGamePrepareVisible()) {
            return;
        }


        for (String charaxcterId : characterToViewMap.keySet()) {
            int[] coordinates = new int[2];
            characterToViewMap.get(charaxcterId).getLocationOnScreen(coordinates);
            CardLocationsHolder.setLocationFor(charaxcterId, coordinates);
        }
    }


    protected boolean isEndGamePrepareVisible() {

        if (getActivity() == null) {
            return true;
        }

        GameState game = ((GameActivity) getActivity()).getGame();

        for (PlayerState player : game.getPlayers()) {
            if (player.getPrepareEndGameState() != null) {
                return true;
            }
        }


        return false;
    }
}
