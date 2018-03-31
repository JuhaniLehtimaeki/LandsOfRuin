package com.landsofruin.gametracker.actionsui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.TargetCharactersSelectionChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actions.ActionManager;
import com.landsofruin.gametracker.actionsui.ActionViewUtil.ActionPerformPressedCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ActionsFriendlyUIDialogFragment extends Fragment {
    private static final String ARGUMENT_PC_ID = "ARGUMENT_PC_ID";
    private CharacterState character;
    private Action action;
    private ActionPerformPressedCallback callback;
    private ViewGroup targetsContainer;
    private ViewGroup targetsSelection;
    private HashMap<String, View> selectedViews = new HashMap<String, View>();
    private LayoutInflater inflater;
    private List<CharacterState> selectedTargets = new ArrayList<>();
    private View doneButton;


    public static ActionsFriendlyUIDialogFragment newInstance(Action action,
                                                              CharacterState character) {
        ActionsFriendlyUIDialogFragment f = new ActionsFriendlyUIDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PC_ID, character.getIdentifier());
        args.putInt("action", action.getId());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        action = ActionManager.getInstance().getActionForId(
                getArguments().getInt("action"));

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.character = ((BaseGameActivity) activity).getGame().findCharacterByIdentifier(getArguments()
                .getString(ARGUMENT_PC_ID));
    }


    public void onTargetCharacterTapped(final CharacterState character) {


        selectedTargets.add(character);

        ArrayList<CharacterState> cloned = new ArrayList<>();
        Collections.copy(selectedTargets, cloned);
        BusProvider.getInstance().post(
                new TargetCharactersSelectionChangedEvent(
                        cloned));

        // add view
        final View oneTarget = inflater.inflate(R.layout.one_selected_enemy_target,
                targetsContainer, false);

        final TextView targetNameText = (TextView) oneTarget
                .findViewById(R.id.target_name);
        targetNameText.setText(character.getName());


        String pickUri = character.getProfilePictureUri();
        if (pickUri != null) {
            Picasso.with(this.getActivity())
                    .load(pickUri)
                    .into((ImageView) oneTarget.findViewById(R.id.enemy_portrait));
        }

        oneTarget.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        selectedTargets.remove(character);

                        targetsContainer.removeView(oneTarget);

                        ArrayList<CharacterState> cloned = new ArrayList<>();
                        Collections.copy(selectedTargets, cloned);
                        BusProvider
                                .getInstance()
                                .post(new TargetCharactersSelectionChangedEvent(cloned));


                        refreshShootButtonState();
                        refreshTargetMarkers();
                    }
                });

        targetsContainer.addView(oneTarget);


        selectedViews.put(character.getIdentifier(), oneTarget);


        refreshShootButtonState();
        refreshTargetMarkers();

    }


    private void refreshTargetMarkers() {


        ArrayList<CharacterState> targets = character.getAvailableTargetsForFriendlyAction(action, character, ((BaseGameActivity) getActivity()).getGame());

        for (final CharacterState character : targets) {
            if (character.isHidden() || !character.isOnMap()) {
                continue;
            }

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View v = inflater.inflate(R.layout.action_target_friendly_fragment,
                container, false);

        targetsContainer = (ViewGroup) v
                .findViewById(R.id.targets_container);

        targetsSelection = (ViewGroup) v
                .findViewById(R.id.targets_selection);

        doneButton = v.findViewById(R.id.done_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.actionPerformPressed(selectedTargets.get(0), null, null);

            }
        });

        ArrayList<CharacterState> targets = character
                .getAvailableTargetsForFriendlyAction(action, character, ((BaseGameActivity) getActivity()).getGame());

        for (final CharacterState target : targets) {


            View enemyTarget = inflater.inflate(R.layout.one_enemy_target, targetsSelection,
                    false);

            Picasso.with(this.getActivity())
                    .load(target.getCardPictureUri())
                    .into((ImageView) enemyTarget.findViewById(R.id.enemy_portrait));

            ((TextView) enemyTarget.findViewById(R.id.target_name)).setText(target.getName());
            enemyTarget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTargetCharacterTapped(target);
                }
            });

            targetsSelection.addView(enemyTarget);


        }

        ((TextView) v.findViewById(R.id.title)).setText(character.getName()
                + " " + action.getName() + " - Select target:");


        v.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callback.actionCancelled();
            }
        });

        return v;
    }


    private void refreshShootButtonState() {

        if (selectedTargets.size() == 1) {
            doneButton.setEnabled(true);
        } else {
            doneButton.setEnabled(false);
        }
    }

    public void addCallback(ActionPerformPressedCallback callback) {
        this.callback = callback;
    }
}
