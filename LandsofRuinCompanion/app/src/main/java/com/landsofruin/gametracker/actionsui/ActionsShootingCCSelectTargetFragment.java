package com.landsofruin.gametracker.actionsui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.TargetCharacterSelectionStateEndEvent;
import com.landsofruin.companion.eventbus.TargetCharacterSelectionStateStartEvent;
import com.landsofruin.companion.eventbus.TargetCharactersSelectionChangedEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.NextAttackState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.AttackStateSetShootersTransition;
import com.landsofruin.companion.state.transition.AttackStateSetTargetsTransition;
import com.landsofruin.companion.state.transition.StartAttackTransition;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ActionsShootingCCSelectTargetFragment extends Fragment {

    private CharacterState character;
    private WargearOffensive wargear;

    private LinkedList<CharacterState> selectedTargets = new LinkedList<>();
    private LayoutInflater inflater;
    private TextView targetsText;
    private int actionId;
    private boolean isCloseCombat = false;
    private ViewGroup targetsContainer;
    private HashMap<String, View> selectedViews = new HashMap<>();
    private View shootButton;
    private TextView targetsText2;
    private int numberOfShooters = 1;
    private View squadContainer;
    private SeekBar shooterCountSeekBar;
    private TextView shooterCountTextView;


    public static ActionsShootingCCSelectTargetFragment newInstance(
            WargearOffensive wargear, CharacterState character, Action action) {
        ActionsShootingCCSelectTargetFragment f = new ActionsShootingCCSelectTargetFragment();
        Bundle args = new Bundle();
        args.putString("character", character.getIdentifier());
        args.putInt("wargear", wargear.getId());
        args.putInt("action", action.getId());
        f.setArguments(args);

        return f;
    }


    public static ActionsShootingCCSelectTargetFragment newInstance(
            int wargear, String character, int action) {
        ActionsShootingCCSelectTargetFragment f = new ActionsShootingCCSelectTargetFragment();
        Bundle args = new Bundle();
        args.putString("character", character);
        args.putInt("wargear", wargear);
        args.putInt("action", action);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((BaseGameActivity) getActivity()).getGame() == null) {
            getActivity().finish();
            return;
        }
        character = ((BaseGameActivity) getActivity()).getGame()
                .findCharacterByIdentifier(
                        getArguments().getString("character"));

        int wargearId = getArguments().getInt("wargear");
        wargear = (WargearOffensive) WargearManager.getInstance()
                .getWargearById(wargearId);

        this.actionId = getArguments().getInt("action");

        if ("CC".equalsIgnoreCase(wargear.getType())) {
            this.isCloseCombat = true;
        }
    }

    private void refreshTargetsSelected() {

        int maxTargets = wargear.getMaxTargets() * numberOfShooters;

        targetsText.setText("Select up to " + maxTargets + " target(s)");

        int remainingTargetsToSelect = maxTargets - selectedTargets.size();
        targetsText2.setText("" + selectedTargets.size());

        if (remainingTargetsToSelect >= 0 && selectedTargets.size() > 0) {
            targetsText2.setTextColor(0xFFFFFFFF);
        } else {
            targetsText2.setTextColor(0xFFB71C1C);
        }
    }


    public void refreshState() {

        if (this.character instanceof CharacterStateSquad) {
            squadContainer.setVisibility(View.VISIBLE);
            shooterCountSeekBar.setMax(((CharacterStateSquad) this.character).getSquadSize() - 1);
            this.numberOfShooters = ((GameActivity) getActivity()).getGame().getPhase().getNextAttackState().getNumberOfShooters() + 1;
            shooterCountSeekBar.setProgress(this.numberOfShooters - 1);
            shooterCountTextView.setText("" + numberOfShooters);


        } else {
            squadContainer.setVisibility(View.GONE);
        }


        refreshShootButtonState();
    }

    public void onTargetCharacterTapped(final CharacterState character, final GameState game) {


        selectedTargets.add(character);

        BusProvider.getInstance().post(
                new TargetCharactersSelectionChangedEvent(
                        (List<CharacterState>) selectedTargets.clone()));

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

                        BusProvider
                                .getInstance()
                                .post(new TargetCharactersSelectionChangedEvent(
                                        (List<CharacterState>) selectedTargets
                                                .clone()));
                        refreshShootButtonState();
                        refreshTargetsSelected();
                    }
                });

        targetsContainer.addView(oneTarget);


        selectedViews.put(character.getIdentifier(), oneTarget);


        refreshShootButtonState();
        refreshTargetsSelected();
    }


    private void refreshShootButtonState() {

        if (selectedTargets.size() == 0
                || selectedTargets.size() > (wargear.getMaxTargets() * numberOfShooters)) {
            shootButton.setEnabled(false);
        } else {
            shootButton.setEnabled(true);
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        this.inflater = inflater;
        this.selectedTargets.clear();

        BusProvider.getInstance().post(
                new TargetCharacterSelectionStateStartEvent());

        View v = inflater.inflate(R.layout.shoot_action_fragment, container, false);

        if (this.wargear == null) {
            return v;
        }

        v.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAttackTransition transition = new StartAttackTransition(character.getIdentifier(), actionId, -1);
                ((GameActivity) getActivity()).sendToServer(transition);
                BusProvider.getInstance().post(
                        new TargetCharacterSelectionStateEndEvent());
            }
        });


        ((TextView) v.findViewById(R.id.weapon_name)).setText(wargear.getName());
        ((TextView) v.findViewById(R.id.mode_name)).setText(wargear.getModeName());


        Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(LookupHelper.getInstance().getWargearFor(wargear.getId()).getCategory());

        if (resource != null) {
            ((ImageView) v.findViewById(R.id.weapon_image)).setImageResource(resource);
        } else {
            v.findViewById(R.id.weapon_image).setVisibility(View.INVISIBLE);
        }

        squadContainer = v.findViewById(R.id.squad_container);

        shootButton = v.findViewById(R.id.shoot_button);
        shootButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                LinkedList<String> targets = new LinkedList<>();
                int index = 0;
                for (CharacterState character : selectedTargets) {
                    targets.add(character.getIdentifier() + ":" + index);
                    ++index;
                }

                AttackStateSetTargetsTransition transition = new AttackStateSetTargetsTransition(targets);
                ((GameActivity) getActivity()).sendToServer(transition);
                BusProvider.getInstance().post(
                        new TargetCharacterSelectionStateEndEvent());

            }
        });


        targetsText = (TextView) v.findViewById(R.id.targets_header);
        targetsText2 = (TextView) v.findViewById(R.id.targets_header_2);

        shooterCountSeekBar = (SeekBar) v.findViewById(R.id.shooter_count);
        shooterCountTextView = (TextView) v.findViewById(R.id.shooter_count_text);


        shooterCountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                AttackStateSetShootersTransition transition = new AttackStateSetShootersTransition(progress);
                ((GameActivity) getActivity()).sendToServer(transition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        refreshTargetsSelected();

        targetsContainer = (ViewGroup) v.findViewById(R.id.targets_container);


        ViewGroup targetsSelection = (ViewGroup) v.findViewById(R.id.targets_selection);
        targetsSelection.removeAllViews();

        final GameState game = ((GameActivity) getActivity()).getGame();

        for (final CharacterState character : game.getEnemyCharacters()) {
            if (character.isHidden() || !character.isOnMap()) {
                continue;
            }
            View enemyTarget = inflater.inflate(R.layout.one_enemy_target, targetsSelection,
                    false);

            Picasso.with(this.getActivity())
                    .load(character.getCardPictureUri())
                    .into((ImageView) enemyTarget.findViewById(R.id.enemy_portrait));

            ((TextView) enemyTarget.findViewById(R.id.target_name)).setText(character.getName());
            enemyTarget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTargetCharacterTapped(character, game);
                }
            });

            targetsSelection.addView(enemyTarget);
        }


        // add zombie button
        Zombie zombie = new Zombie();

        View enemyTarget = inflater.inflate(R.layout.one_enemy_target, targetsSelection,
                false);


        Picasso.with(this.getActivity())
                .load(zombie.getCardPictureUri())
                .into((ImageView) enemyTarget.findViewById(R.id.enemy_portrait));

        ((TextView) enemyTarget.findViewById(R.id.target_name)).setText(zombie.getName());
        enemyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTargetCharacterTapped(new Zombie(), game);

            }
        });
        targetsSelection.addView(enemyTarget);


        zombie = new Zombie(Zombie.ZOMBIE_ID_FAST);

        enemyTarget = inflater.inflate(R.layout.one_enemy_target, targetsSelection,
                false);


        Picasso.with(this.getActivity())
                .load(zombie.getCardPictureUri())
                .into((ImageView) enemyTarget.findViewById(R.id.enemy_portrait));

        ((TextView) enemyTarget.findViewById(R.id.target_name)).setText(zombie.getName());
        enemyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTargetCharacterTapped(new Zombie(Zombie.ZOMBIE_ID_FAST), game);

            }
        });
        targetsSelection.addView(enemyTarget);


        zombie = new Zombie(Zombie.ZOMBIE_ID_FAT);

        enemyTarget = inflater.inflate(R.layout.one_enemy_target, targetsSelection,
                false);


        Picasso.with(this.getActivity())
                .load(zombie.getCardPictureUri())
                .into((ImageView) enemyTarget.findViewById(R.id.enemy_portrait));

        ((TextView) enemyTarget.findViewById(R.id.target_name)).setText(zombie.getName());
        enemyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTargetCharacterTapped(new Zombie(Zombie.ZOMBIE_ID_FAT), game);

            }
        });
        targetsSelection.addView(enemyTarget);


        refreshState();

        return v;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {

        NextAttackState attackState = ((GameActivity) getActivity()).getGame().getPhase().getNextAttackState();
        if (attackState != null) {
            loadStateFromMetadata(attackState);
        }
        refreshTargetsSelected();
        refreshShootButtonState();
    }

    private void loadStateFromMetadata(NextAttackState attackState) {
        refreshState();
    }
}

