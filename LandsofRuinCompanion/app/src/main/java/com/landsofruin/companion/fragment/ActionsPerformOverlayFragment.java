package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.cards.events.CardSelectedEvent;
import com.landsofruin.companion.cards.events.CardViewSelectedEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.eventbus.HideActionSelectorEvent;
import com.landsofruin.companion.eventbus.HideCommandPanelManuallyEvent;
import com.landsofruin.companion.mapcompanionfragment.SelectActionFragment;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actionsui.ActionViewContainer;
import com.landsofruin.gametracker.actionsui.ActionViewUtil;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Fragment showing the game ended state
 */
public class ActionsPerformOverlayFragment extends CommandPanelOverlayFragment {

    private BaseGameActivity activity;
    private ViewGroup rootView;

    private TextView titleView;
    private ViewGroup actionsContainer;
    private ViewGroup oneActionOverlayContainer;
    private TextView noActionsView;
    private View changeOverlayView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.actions_perform_overlay_fragment,
                parent, false);

        actionsContainer = (ViewGroup) rootView.findViewById(R.id.assigned_actions);
        oneActionOverlayContainer = (ViewGroup) rootView.findViewById(R.id.one_action_overlay_container_container);
        noActionsView = (TextView) rootView.findViewById(R.id.no_actions_view);

        titleView = (TextView) rootView.findViewById(R.id.title);

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


    View getChangeOverlayView() {
        return changeOverlayView;
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
        if (game.getPhase().getPrimaryPhase() != PrimaryPhase.ACTION) {
            return false;
        }
        return this.getCharacter() != null;


    }



    void updateUI() {
        GameState game = activity.getGame();


        if (shouldBeVisible(game)) {
            showOverlay();
        } else {
            hideOverlay(null);
            return;
        }


        oneActionOverlayContainer.setVisibility(View.GONE);

        titleView.setText(this.getCharacter().getName());


        List<Integer> actions = this.getCharacter().getCurrentActions();
        actionsContainer.removeAllViews();
//        oneActionOverlay.removeAllViews();


        if (actions.isEmpty()) {
            noActionsView.setVisibility(View.VISIBLE);

        } else {
            noActionsView.setVisibility(View.GONE);
        }

        boolean first = true;
        for (final Integer action : actions) {


            if (this.getCharacter().hasActionPerformed(action)) {
                continue;
            }


            List<ActionViewContainer> actionViews = ActionViewUtil
                    .createActionView(
                            LookupHelper.getInstance().getActionFor(action),
                            this.getCharacter(),
                            actionsContainer,
                            (BaseGameActivity) getActivity(),
                            new ActionViewUtil.ActionPerformCloseActionUICallback() {

                                @Override
                                public void actionPerformed() {
                                    oneActionOverlayContainer.setVisibility(View.GONE);
                                    updateUI();
                                }

                                @Override
                                public void actionSkippedUI() {


                                }

                                @Override
                                public void resetActions() {
                                    oneActionOverlayContainer.setVisibility(View.GONE);
                                    updateUI();
                                }
                            });

            SelectActionFragment fragment = new SelectActionFragment();
            fragment.setActions(actionViews, LookupHelper.getInstance().getActionFor(action).getName());
            fragment.setCallback(new SelectActionFragment.ActionSelectedCallback() {

                @Override
                public void actionSelected(ActionViewContainer action) {

                    if (action.getPressedCallback() != null) {
                        action.getPressedCallback().actionPerformPressed(null, null, null);
                    } else {
                        oneActionOverlayContainer.setVisibility(View.VISIBLE);
                        FragmentTransaction transaction = getChildFragmentManager()
                                .beginTransaction();
                        transaction.replace(R.id.one_action_overlay_container,
                                action.getPerformActionFragment());
                        transaction.commit();
                    }


                }

                @Override
                public void actionSkipped() {
                    oneActionOverlayContainer.setVisibility(View.GONE);

                }

                @Override
                public void actionCancelled() {
                    oneActionOverlayContainer.setVisibility(View.GONE);
                }
            });

            FragmentTransaction transaction = getChildFragmentManager()
                    .beginTransaction();


            if (first) {
                first = false;
                transaction.replace(R.id.assigned_actions, fragment);
            } else {
                transaction.add(R.id.assigned_actions, fragment);
            }

            transaction.commit();


        }

    }

    @Override
    View getRootView() {
        return this.rootView;
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
    public void onCharacterSelectedEvent(CharacterSelectedEvent event) {
        if (event.getCharacter() != null) {
            manuallyHidden = false;
        }
        hideOverlay(event.getCharacter());
    }

    @Subscribe
    public void onHideActionSelectorEvent(HideActionSelectorEvent event) {
        manuallyHidden = false;
        hideOverlay();
    }


    @Subscribe
    public void onHideCommandPanelManuallyEvent(HideCommandPanelManuallyEvent event) {
        manuallyHidden = true;
        hideOverlay();
    }

}
