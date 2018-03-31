package com.landsofruin.companion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.cards.events.CardViewSelectedEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.eventbus.HideCommandPanelManuallyEvent;
import com.landsofruin.companion.fragment.util.ActionViewBuilder;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.gameruleobjects.action.ActionContainerForAssignActions;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.RemoveActionForCharacterTransition;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.charactertypes.CharacterTypeManager;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Fragment showing the game ended state
 */
public class ActionsPlanningOverlayFragment extends CommandPanelOverlayFragment {

    private BaseGameActivity activity;
    private ViewGroup rootView;
    private TextView titleView;
    private ViewGroup assignedActionsContainer;
    private ViewGroup availableActionsContainer;
    private boolean manuallyHidden = true;
    private TextView apsLeftText;
    private ScrollView scrollView;
    private View changeOverlayView;
    private View assignedActionsContainerWrapper;
    private View apsLeftWrapper;
    private TextView squadHintText;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.actions_planning_overlay_fragment,
                parent, false);


        titleView = (TextView) rootView.findViewById(R.id.title);

        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        apsLeftText = (TextView) rootView.findViewById(R.id.aps_left);
        squadHintText = (TextView) rootView.findViewById(R.id.squad_hint_text);


        assignedActionsContainerWrapper = rootView.findViewById(R.id.assigned_actions_container);
        apsLeftWrapper = rootView.findViewById(R.id.aps_left_container);


        assignedActionsContainer = (ViewGroup) rootView.findViewById(R.id.assigned_actions);
        availableActionsContainer = (ViewGroup) rootView.findViewById(R.id.available_actions);

        rootView.findViewById(R.id.hide_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new CharacterSelectedEvent(null));
                BusProvider.getInstance().post(new CardViewSelectedEvent(null));
                manuallyHidden = true;
                updateUI();
            }
        });

        changeOverlayView = rootView.findViewById(R.id.change_overlay);

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

        if (manuallyHidden) {
            return false;
        }
        if (!game.getPhase().isMine()) {
            return false;
        }


        if (game.getPhase().getPrimaryPhase() != PrimaryPhase.ASSIGN_ACTIONS) {
            return false;
        }
        return this.getCharacter() != null;


    }

    @Override
    View getChangeOverlayView() {
        return changeOverlayView;
    }

    void updateUI() {
        GameState game = activity.getGame();


        if (shouldBeVisible(game)) {
            showOverlay();
        } else {
            hideOverlay(null);
            return;
        }

        titleView.setText(this.getCharacter().getName());


        CharacterType characterType = CharacterTypeManager.getInstance().getCharacterTypeByID(this.getCharacter().getCharacterType());
        switch (characterType.getType()) {
            case CharacterType.TYPE_SQUAD_CLOSE_SUPPORT:
                // check if the hero is down.
                CharacterState hero = ((CharacterStateSquad) this.getCharacter()).getHeroCharacterState(game);
                if (hero.isDown()) {
                    assignedActionsContainerWrapper.setVisibility(View.VISIBLE);
                    apsLeftWrapper.setVisibility(View.VISIBLE);
                    availableActionsContainer.setVisibility(View.VISIBLE);
                    squadHintText.setVisibility(View.GONE);
                } else {
                    assignedActionsContainerWrapper.setVisibility(View.INVISIBLE);
                    apsLeftWrapper.setVisibility(View.INVISIBLE);
                    squadHintText.setVisibility(View.VISIBLE);
                    availableActionsContainer.setVisibility(View.GONE);
                    squadHintText.setText("Use this squad's hero to assign actions to the squad. The hero's actions are automatically assigned to the squad as well.");
                }

                break;
            case CharacterType.TYPE_SQUAD_SLAVE:
                assignedActionsContainerWrapper.setVisibility(View.INVISIBLE);
                apsLeftWrapper.setVisibility(View.INVISIBLE);
                squadHintText.setVisibility(View.VISIBLE);
                availableActionsContainer.setVisibility(View.GONE);
                squadHintText.setText("This squad cannot perform actions.");
                break;
            default:
                assignedActionsContainerWrapper.setVisibility(View.VISIBLE);
                apsLeftWrapper.setVisibility(View.VISIBLE);
                availableActionsContainer.setVisibility(View.VISIBLE);
                squadHintText.setVisibility(View.GONE);
                break;
        }


        assignedActionsContainer.removeAllViews();


        int movemementValue = 0;

        if (game.getPhase().isMine() && (game.getPhase().getPrimaryPhase() == PrimaryPhase.ACTION || game.getPhase().getPrimaryPhase() == PrimaryPhase.MOVE)) {
            movemementValue = this.getCharacter().getMovementAllowance(game);
        } else {
            movemementValue = this.getCharacter().getMovementAllowanceForNextTurn(game);
        }

        View movementView = getActivity().getLayoutInflater().inflate(
                R.layout.one_action_in_card_movement, assignedActionsContainer, false);

        final TextView movement = (TextView) movementView
                .findViewById(R.id.text_view);
        movement.setText("" + movemementValue + "''");
        assignedActionsContainer.addView(movementView);


        for (final Integer action : this.getCharacter().getCurrentActions()) {

            View oneIconView = getActivity().getLayoutInflater().inflate(
                    R.layout.one_action_in_card, assignedActionsContainer, false);

            final ImageView icon = (ImageView) oneIconView
                    .findViewById(R.id.action_icon);


            icon.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (action == null || getActivity() == null || getCharacter() == null) {
                        return;
                    }

                    ((BaseGameActivity) getActivity())
                            .sendToServer(new RemoveActionForCharacterTransition(
                                    getCharacter().getIdentifier(),
                                    LookupHelper.getInstance().getActionFor(
                                            action).getId()));

                }
            });

            icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(action)));
            assignedActionsContainer.addView(oneIconView);
        }


        List<ActionContainerForAssignActions> actions = this.getCharacter()
                .getAvailableActionsForAssignActions(((BaseGameActivity) getActivity())
                        .getGame());


        availableActionsContainer.removeAllViews();
        for (final ActionContainerForAssignActions action : actions) {
            View actionView = ActionViewBuilder.buildActionView(action, action.getAction(), this.getCharacter(), getActivity().getLayoutInflater(), this.getCharacter().hasActionAssigend(action.getAction()), (GameActivity) getActivity(), availableActionsContainer);

            availableActionsContainer.addView(actionView);
        }

        int remainingAPs = this.getCharacter().getRemainingActionPoints(game);
        apsLeftText.setText("APs remaining: " + remainingAPs);
    }

    @Override
    View getRootView() {
        return rootView;
    }


    @Subscribe
    public void onCharacterSelectedEvent(CharacterSelectedEvent event) {
        if (event.getCharacter() != null) {
            manuallyHidden = false;
        }
        hideOverlay(event.getCharacter());
        scrollView.smoothScrollTo(0, 0);
    }

    @Subscribe
    public void onHideCommandPanelManuallyEvent(HideCommandPanelManuallyEvent event) {
        manuallyHidden = true;
        hideOverlay();
    }


}
