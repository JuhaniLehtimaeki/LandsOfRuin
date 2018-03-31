package com.landsofruin.companion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.TutorialShowEvent;
import com.landsofruin.companion.eventbus.TutorialShowMoraleInfoEvent;
import com.landsofruin.companion.eventbus.TutorialShowWeaponInfoEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.tutorial.TutorialUtils;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

/**
 *
 */
public class TutorialOverlayFragment extends Fragment {

    private boolean isFirstOpen = true;

    private ViewGroup rootView;

    private LayoutInflater inflater;
    private ViewGroup content;
    private View okButton;
    private TextView titleTextView;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.tutorial_overlay_fragment,
                parent, false);

        content = (ViewGroup) rootView.findViewById(R.id.content_view);

        titleTextView = ((TextView) rootView.findViewById(R.id.tutorial_title));
        okButton = rootView.findViewById(R.id.ok_tutorial_button);
        this.inflater = inflater;

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);


    }


    @Override
    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }


    @Subscribe
    public void onTutorialShowWeaponInfoEvent(TutorialShowWeaponInfoEvent event) {
        showWeaponInfo(event.getWeaponId());
    }


    @Subscribe
    public void onTutorialShowWeaponInfoEvent(TutorialShowMoraleInfoEvent event) {
        showMoraleInfo();
    }

    @Subscribe
    public void onTutorialShowEvent(TutorialShowEvent event) {
        showCurrentTutorial(event.getX(), event.getY(), event.getWidth(), event.getHeight());
    }


    private void showWeaponInfo(int weaponId) {


        rootView.setVisibility(View.VISIBLE);
        content.removeAllViews();

        final View tutorialView = inflater.inflate(R.layout.tutorial_weapon_view, content, false);


        titleTextView.setText("Weapon info");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.GONE);
            }
        });
        ViewGroup weaponsContainerView = (ViewGroup) tutorialView.findViewById(R.id.weapon_info_container);


        LinkedList<Wargear> wargearList = WargearManager.getInstance().getWargearByWeaponID(weaponId);
        Wargear wargearInstanceOne = wargearList.getFirst();
        if (wargearInstanceOne instanceof WargearOffensive) {


            WargearOffensive offensiveWargear = (WargearOffensive) wargearInstanceOne;

            View oneWeapon = inflater.inflate(
                    R.layout.tutorial_one_weapon_group,
                    weaponsContainerView, false);

            ((TextView) oneWeapon.findViewById(R.id.weapon_name))
                    .setText(wargearInstanceOne.getName());


            ((TextView) oneWeapon.findViewById(R.id.clip_gear_value))
                    .setText("Gear value per " + ((WargearOffensive) wargearInstanceOne).getClipName() + " " + ((WargearOffensive) wargearInstanceOne).getClipGearValue());
            ((TextView) oneWeapon.findViewById(R.id.clip_size))
                    .setText("Ammo per " + ((WargearOffensive) wargearInstanceOne).getClipName() + " " + ((WargearOffensive) wargearInstanceOne).getClipSize());

            ((TextView) oneWeapon.findViewById(R.id.camo_rating))
                    .setText("Camouflage modifier " + wargearInstanceOne.getCamoModifier());

            ((TextView) oneWeapon.findViewById(R.id.gear_value))
                    .setText("Gear value " + wargearInstanceOne.getGearValue());

            if (offensiveWargear.getImageUri() != null && !offensiveWargear.getImageUri().isEmpty()) {
                Picasso.with(getActivity()).load(offensiveWargear.getImageUri()).into(((ImageView) oneWeapon.findViewById(R.id.weapon_image)));
            } else {

                Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(offensiveWargear.getCategory());
                if (resource != null) {
                    ((ImageView) oneWeapon.findViewById(R.id.weapon_image)).setImageResource(resource);
                } else {
                    oneWeapon.findViewById(R.id.weapon_image).setVisibility(View.GONE);
                }
            }
            ViewGroup modes = (ViewGroup) oneWeapon
                    .findViewById(R.id.firing_modes_container);


            for (Wargear wargear2 : wargearList) {

                WargearOffensive thisWG = ((WargearOffensive) wargear2);

                View oneMode = inflater.inflate(
                        R.layout.tutorial_one_weapon_mode, modes,
                        false);

                ((TextView) oneMode.findViewById(R.id.mode)).setText(thisWG
                        .getModeName());

                ((TextView) oneMode.findViewById(R.id.targets)).setText(""
                        + thisWG.getMaxTargets());


                ((TextView) oneMode.findViewById(R.id.power)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getDiceLightInfantry());

                ((TextView) oneMode.findViewById(R.id.power_hi)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getDiceHeavyInfantry());


                ((TextView) oneMode.findViewById(R.id.power_la)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getDiceLightArmored());


                ((TextView) oneMode.findViewById(R.id.power_ha)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getDiceHeavyArmored());


                ((TextView) oneMode.findViewById(R.id.bullets_per_action))
                        .setText("" + thisWG.getBulletsPerAction());


                ((TextView) oneMode.findViewById(R.id.mod_short)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getModifierShort());

                ((TextView) oneMode.findViewById(R.id.mod_mid)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getModifierMid());

                ((TextView) oneMode.findViewById(R.id.mod_long)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getModifierLong());

                if ("CC".equals((offensiveWargear.getType()))) {
                    oneMode.findViewById(R.id.mod_mid).setBackgroundColor(0xFF000000);
                    oneMode.findViewById(R.id.mod_long).setBackgroundColor(0xFF000000);
                }

                ((TextView) oneMode.findViewById(R.id.noise)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getNoiseLevel(null));


                ((TextView) oneMode.findViewById(R.id.suppression)).setText(""
                        + ((WargearOffensive) wargear2)
                        .getSuppresionWithoutHititng() + "+" + ((WargearOffensive) wargear2)
                        .getSuppression());


                modes.addView(oneMode);
            }

            weaponsContainerView.addView(oneWeapon);
        }

        content.addView(tutorialView);
    }


    private void showMoraleInfo() {

        GameState gamestate = ((GameActivity) getActivity()).getGame();


        content.removeAllViews();

        titleTextView.setText("Morale");

        final View tutorialView = inflater.inflate(R.layout.tutorial_morale_view, content, false);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.GONE);
            }
        });


        TextView teamMorale = (TextView) tutorialView.findViewById(R.id.team_morale);
        ProgressBar moraleLevelProgressBar = (ProgressBar) tutorialView.findViewById(R.id.morale_progress_bar);
        ImageView tribeIcon = (ImageView) tutorialView.findViewById(R.id.tribe_icon);

        //update morale
        TeamState team = gamestate.getMe().getTeam();
        if (team.getTeamStatus(gamestate) == TeamState.TEAM_STATUS_NORMAL) {
            teamMorale.setText("Morale OK");
        } else if (team.getTeamStatus(gamestate) == TeamState.TEAM_STATUS_CONFUSION) {
            teamMorale.setText("Team Confused!");
        } else if (team.getTeamStatus(gamestate) == TeamState.TEAM_STATUS_PANIC) {
            teamMorale.setText("Team Panicked!");
        }

        int progressValue = team.getPsychologyPool(gamestate);
        if (progressValue < 1) {
            progressValue = 1;
        }
        moraleLevelProgressBar
                .setProgress(progressValue);

        if (team.getLargeTribeLogoUri() != null) {
            Picasso.with(this.getActivity())
                    .load(team.getLargeTribeLogoUri())
                    .into(tribeIcon);
        }

        ((TextView) tutorialView.findViewById(R.id.morale_pool)).setText("" + team.getPsychologyPool(gamestate));

        int effect = team.getCurrentPositivePsychologyEffect()
                - team.getCurrentNegativePsychologyEffect();


        int effectLastTurn = team.getLastTurnPositivePsychologyEffect()
                - team.getLastTurnNegativePsychologyEffect();
        ((TextView) tutorialView.findViewById(R.id.morale_positive)).setText("" + team.getCurrentPositivePsychologyEffect() + " (last turn: " + team.getLastTurnPositivePsychologyEffect() + ")");
        ((TextView) tutorialView.findViewById(R.id.morale_negative)).setText("" + team.getCurrentNegativePsychologyEffect() + " (last turn: " + team.getLastTurnNegativePsychologyEffect() + ")");
        ((TextView) tutorialView.findViewById(R.id.morale_total)).setText("" + effect + " (last turn: " + effectLastTurn + ")");


        int highestLD = 0;
        CharacterState leader = null;
        for (CharacterState character : team.listAllTypesCharacters()) {
            if (character.isDead() || character.isUnconsious()) {
                continue;
            }
            int ld = character.getLeadership();
            if (ld > highestLD) {
                highestLD = ld;
                leader = character;
            }
        }

        if (leader != null) {
            ((TextView) tutorialView.findViewById(R.id.leader_name)).setText(leader.getName());

            Picasso.with(this.getActivity())
                    .load(leader.getProfilePictureUri())
                    .into((ImageView) tutorialView.findViewById(R.id.leader_portrait));
        } else {
            ((TextView) tutorialView.findViewById(R.id.leader_name)).setText("No Leader!");
        }
        ((TextView) tutorialView.findViewById(R.id.morale_leadership)).setText("" + highestLD);


        content.addView(tutorialView);


        rootView.setVisibility(View.VISIBLE);

    }

    private synchronized void showCurrentTutorial(float x, float y, int width, int height) {

        if (rootView == null) {
            return;
        }

        GameState gamestate = ((GameActivity) getActivity()).getGame();

        final String nextTutorial = TutorialUtils.getInstance(getActivity()).getTutorialIdFor(gamestate.getPhase().getPrimaryPhase(), gamestate.getPhase().isMine(),
                gamestate.getPhase().getGameTurn());


        if (nextTutorial == null) {
            rootView.setVisibility(View.GONE);
            return;
        } else {

            if (!isFirstOpen) {
                rootView.setPivotY(0);
                rootView.setPivotX(0);
                rootView.setTranslationX(x);
                rootView.setTranslationY(y);


                float scaleX = (float) width / rootView.getWidth();
                float scaleY = (float) height / rootView.getHeight();

                rootView.setScaleX(scaleX);
                rootView.setScaleY(scaleY);
            }

            content.removeAllViews();


            final View tutorialView = TutorialUtils.getInstance(getActivity()).getViewForTutorial(nextTutorial, inflater, content);

            String title = TutorialUtils.getInstance(getActivity()).getTitleFor(nextTutorial);

            titleTextView.setText(title);

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    rootView.setVisibility(View.GONE);

                    //TODO: set tutorial seen in user account

                }
            });

            content.addView(tutorialView);


            rootView.setVisibility(View.VISIBLE);
            if (!isFirstOpen) {
                rootView.animate().scaleY(1).scaleX(1).translationX(0).translationY(0);
            }
            isFirstOpen = false;
        }


    }


}
