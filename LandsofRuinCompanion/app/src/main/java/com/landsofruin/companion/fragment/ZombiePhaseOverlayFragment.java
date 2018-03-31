package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.cards.events.CardSelectedEvent;
import com.landsofruin.companion.cards.events.CardViewSelectedEvent;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.eventbus.HideCommandPanelManuallyEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.AddRemoveZombiePhaseHitTransition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Fragment showing the game ended state
 */
public class ZombiePhaseOverlayFragment extends CommandPanelOverlayFragment {

    HashMap<Integer, Integer> previousValues = new HashMap<>();
    private BaseGameActivity activity;
    private ViewGroup rootView;
    private TextView titleView;
    private ViewGroup zombieTurnHitButtons;
    private boolean manuallyHidden = true;
    private View changeOverlayView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.zombie_phase_overlay_fragment,
                parent, false);

        titleView = (TextView) rootView.findViewById(R.id.title);


        zombieTurnHitButtons = (ViewGroup) rootView
                .findViewById(R.id.zombie_turn_take_hit_buttons);


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

        if (game.getPhase().getPrimaryPhase() != PrimaryPhase.ZOMBIES) {
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
            hideOverlay();
            return;
        }

        titleView.setText(this.getCharacter().getName());


        this.zombieTurnHitButtons.removeAllViews();

        LinkedList<Integer> handleddIds = new LinkedList<>();

        for (final ThrowableState throwable : activity.getGame().getWorld()
                .getThrowableStates()) {


            if (throwable.getTemplateSize() == 0 || handleddIds.contains(throwable.getWargearId())) {
                continue;
            }

            handleddIds.add(throwable.getWargearId());


            final View onceHitView = getActivity().getLayoutInflater().inflate(
                    R.layout.phase_4_one_hit, zombieTurnHitButtons, false);

            ((TextView) onceHitView.findViewById(R.id.hit_name)).setText("Hit by " + LookupHelper.getInstance().getWargearFor(throwable).getName());
            int count = this.getCharacter().getZombieHitCountForWargear(LookupHelper.getInstance().getWargearFor(throwable).getId());
            ((TextView) onceHitView.findViewById(R.id.hit_count)).setText("" + count);

            onceHitView.findViewById(R.id.add_hit_button).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), throwable.getWargearId(), false));

                }
            });

            onceHitView.findViewById(R.id.remove_hit_button).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), throwable.getWargearId(), true));

                }
            });

            if (count <= 0) {
                onceHitView.findViewById(R.id.remove_hit_button).setEnabled(false);
            }


            ((ImageView) onceHitView.findViewById(R.id.attack_image)).setImageResource(R.drawable.demolisions);
            animateIfNeeded(count, throwable.getWargearId(), ((TextView) onceHitView.findViewById(R.id.hit_count)));


            zombieTurnHitButtons.addView(onceHitView);

        }


        //NORMAL ZOMBIE
        View onceHitView = getActivity().getLayoutInflater().inflate(
                R.layout.phase_4_one_hit, zombieTurnHitButtons, false);

        ((TextView) onceHitView.findViewById(R.id.hit_name)).setText("Attacking Shambler Rotters");
        int count = this.getCharacter().getZombieHitCountForWargear(Zombie.ZOMBIE_WEAPON_ID);
        ((TextView) onceHitView.findViewById(R.id.hit_count)).setText("" + count);

        onceHitView.findViewById(R.id.add_hit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), Zombie.ZOMBIE_WEAPON_ID, false));

            }
        });

        onceHitView.findViewById(R.id.remove_hit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), Zombie.ZOMBIE_WEAPON_ID, true));

            }
        });

        if (count <= 0) {
            onceHitView.findViewById(R.id.remove_hit_button).setEnabled(false);
        }

        ((ImageView) onceHitView.findViewById(R.id.attack_image)).setImageResource(R.drawable.zombie_attack);

        animateIfNeeded(count, Zombie.ZOMBIE_WEAPON_ID, ((TextView) onceHitView.findViewById(R.id.hit_count)));

        zombieTurnHitButtons.addView(onceHitView);

        //FAST ZOMBIE

        onceHitView = getActivity().getLayoutInflater().inflate(
                R.layout.phase_4_one_hit, zombieTurnHitButtons, false);

        ((TextView) onceHitView.findViewById(R.id.hit_name)).setText("Attacking Rager Rotters");
        count = this.getCharacter().getZombieHitCountForWargear(Zombie.ZOMBIE_FAST_WEAPON_ID);
        ((TextView) onceHitView.findViewById(R.id.hit_count)).setText("" + count);

        onceHitView.findViewById(R.id.add_hit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), Zombie.ZOMBIE_FAST_WEAPON_ID, false));

            }
        });

        onceHitView.findViewById(R.id.remove_hit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), Zombie.ZOMBIE_FAST_WEAPON_ID, true));

            }
        });

        if (count <= 0) {
            onceHitView.findViewById(R.id.remove_hit_button).setEnabled(false);
        }

        ((ImageView) onceHitView.findViewById(R.id.attack_image)).setImageResource(R.drawable.zombie_attack_fast);

        animateIfNeeded(count, Zombie.ZOMBIE_FAST_WEAPON_ID, ((TextView) onceHitView.findViewById(R.id.hit_count)));

        zombieTurnHitButtons.addView(onceHitView);


        //FAT ZOMBIE


        onceHitView = getActivity().getLayoutInflater().inflate(
                R.layout.phase_4_one_hit, zombieTurnHitButtons, false);

        ((TextView) onceHitView.findViewById(R.id.hit_name)).setText("Attacking Veteran Rotter");
        count = this.getCharacter().getZombieHitCountForWargear(Zombie.ZOMBIE_FAT_WEAPON_ID);
        ((TextView) onceHitView.findViewById(R.id.hit_count)).setText("" + count);

        onceHitView.findViewById(R.id.add_hit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), Zombie.ZOMBIE_FAT_WEAPON_ID, false));

            }
        });

        onceHitView.findViewById(R.id.remove_hit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.sendToServer(new AddRemoveZombiePhaseHitTransition(getCharacter().getIdentifier(), Zombie.ZOMBIE_FAT_WEAPON_ID, true));

            }
        });

        if (count <= 0) {
            onceHitView.findViewById(R.id.remove_hit_button).setEnabled(false);
        }

        ((ImageView) onceHitView.findViewById(R.id.attack_image)).setImageResource(R.drawable.zombie_attack_fat);

        animateIfNeeded(count, Zombie.ZOMBIE_FAT_WEAPON_ID, ((TextView) onceHitView.findViewById(R.id.hit_count)));

        zombieTurnHitButtons.addView(onceHitView);

    }

    @Override
    View getRootView() {
        return rootView;
    }

    private void animateIfNeeded(int newCount, int wgId, TextView textView) {

        if (previousValues.get(wgId) == null || previousValues.get(wgId) != newCount) {
            textView.setScaleX(2f);
            textView.setScaleY(2f);

            textView.animate().scaleY(1f).scaleX(1f).setInterpolator(new OvershootInterpolator());
        }

        previousValues.put(wgId, newCount);
    }


    @Subscribe
    public void onHideCommandPanelManuallyEvent(HideCommandPanelManuallyEvent event) {
        manuallyHidden = true;
        hideOverlay();
    }


    @Subscribe
    public void onCharacterSelectedEvent(CharacterSelectedEvent event) {
        if (event.getCharacter() != null) {
            manuallyHidden = false;
        }
        hideOverlay(event.getCharacter());
    }

}
