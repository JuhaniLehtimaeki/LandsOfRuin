package com.landsofruin.gametracker.actionsui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.TargetCharacterSelectionStateEndEvent;
import com.landsofruin.companion.eventbus.TutorialShowWeaponInfoEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameConstants;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.NextAttackState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.utils.ColourUtils;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttackOverlayWeaponFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttackOverlayWeaponFragment extends Fragment {

    private static final String ARG_PARAM_WEAPON_ID = "param1";

    private HashMap<Integer, View> weaponToViewMap = new HashMap<>();

    private int weaponId;


    public AttackOverlayWeaponFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param weaponId Parameter 1.
     * @return A new instance of fragment AttackOverlayWeaponFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttackOverlayWeaponFragment newInstance(int weaponId) {
        AttackOverlayWeaponFragment fragment = new AttackOverlayWeaponFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_WEAPON_ID, weaponId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            weaponId = getArguments().getInt(ARG_PARAM_WEAPON_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_attack_overlay_weapon, container, false);
        GameState gamestate = ((GameActivity) getActivity()).getGame();
        if (gamestate == null) {
            getActivity().finish();
            return view;
        }

        NextAttackState attackState = gamestate.getPhase().getNextAttackState();

        if (attackState == null) {
            return view;
        }

        final CharacterState shooter = gamestate.findCharacterByIdentifier(attackState.getCharacterAttackingId());
        boolean isAiming = attackState.getActionId() == Action.ACTION_ID_AIM_AND_SHOOTING;
        final int actionId = attackState.getActionId();

        boolean isRangedAttack = attackState.getActionId() == Action.ACTION_ID_SHOOTING || attackState.getActionId() == Action.ACTION_ID_AIM_AND_SHOOTING;
        boolean isCCAttack = attackState.getActionId() == Action.ACTION_ID_CC;


        ViewGroup modesContainer = (ViewGroup) view.findViewById(R.id.modes_container);
        inflater.inflate(R.layout.shooting_ui_one_weapon_shooting_header, modesContainer, true);

        //modes
        List<Integer> wargear = shooter.getWargear();
        for (final Integer wargear2 : wargear) {
            if (weaponId != LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId()) {
                continue;
            }

            if (isRangedAttack && (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                    || !LookupHelper.getInstance().getWargearFor(wargear2).isTypeRanged())) {
                continue;
            }

            if (isCCAttack && (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                    || !LookupHelper.getInstance().getWargearFor(wargear2).isTypeCC())) {
                continue;
            }


            view.findViewById(R.id.info_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new TutorialShowWeaponInfoEvent(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId()));
                }
            });


            ((TextView) view.findViewById(R.id.ammo)).setText("" + shooter.getAmmoFor(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId()));


            if (shooter.isWargearSkillRequirementsMet(
                    LookupHelper.getInstance().getWargearFor(wargear2))) {


                boolean enabled = true;

                int bulletsPerAction = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getBulletsPerAction();

                int ammo = shooter.getAmmoFor(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId());

                if (ammo < bulletsPerAction) {
                    enabled = false;
                }


                String text = LookupHelper.getInstance().getWargearFor(wargear2).getName();

                if (((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2))
                        .getModeName() != null) {
                    text = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2))
                            .getModeName();
                }


                View infoView = inflater.inflate(R.layout.shooting_ui_one_weapon_shooting_info, modesContainer, false);

                TextView nameView = ((TextView) infoView.findViewById(R.id.mode_name));
                nameView.setText(text);


                ((TextView) infoView.findViewById(R.id.ammo_info)).setText("" + ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getBulletsPerAction());
                if (!enabled) {
                    ((TextView) infoView.findViewById(R.id.ammo_info)).setTextColor(0xFFFF0000);
                }


                int targetNumber = shooter.getTargetNumberShort((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2), gamestate);
                if (isAiming) {
                    targetNumber -= GameConstants.AIM_MODIFIER;
                }
                if (targetNumber <= 1) {
                    targetNumber = 2;
                }
                TextView shortView = ((TextView) infoView.findViewById(R.id.short_range));
                shortView.setText("" + targetNumber);
                ((View) shortView.getParent()).setBackgroundColor(ColourUtils.getColourForTargetNumber(targetNumber));
                shortView.setTextColor(ColourUtils.getTextColourForTargetNumber(targetNumber));

                if (isRangedAttack) {

                    targetNumber = shooter.getTargetNumberMid((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2), gamestate);
                    if (isAiming) {
                        targetNumber -= GameConstants.AIM_MODIFIER;
                    }
                    if (targetNumber <= 1) {
                        targetNumber = 2;
                    }
                    TextView midView = ((TextView) infoView.findViewById(R.id.mid_range));
                    midView.setText("" + targetNumber);
                    ((View) midView.getParent()).setBackgroundColor(ColourUtils.getColourForTargetNumber(targetNumber));
                    midView.setTextColor(ColourUtils.getTextColourForTargetNumber(targetNumber));


                    targetNumber = shooter.getTargetNumberLong((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2), gamestate);
                    if (isAiming) {
                        targetNumber -= GameConstants.AIM_MODIFIER;
                    }
                    if (targetNumber <= 1) {
                        targetNumber = 2;
                    }
                    TextView longView = ((TextView) infoView.findViewById(R.id.long_range));
                    longView.setText("" + targetNumber);
                    ((View) longView.getParent()).setBackgroundColor(ColourUtils.getColourForTargetNumber(targetNumber));
                    longView.setTextColor(ColourUtils.getTextColourForTargetNumber(targetNumber));
                } else {
                    infoView.findViewById(R.id.mid_range).setVisibility(View.GONE);
                    infoView.findViewById(R.id.long_range).setVisibility(View.GONE);
                }

                int noise = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getNoiseLevel(shooter);
                TextView noiseView = ((TextView) infoView.findViewById(R.id.noise));
                noiseView.setText("" + noise);
                ((View) noiseView.getParent()).setBackgroundColor(ColourUtils.getColourForNoise(noise));
                noiseView.setTextColor(ColourUtils.getTextColourForNoise(noise));


                ((TextView) infoView.findViewById(R.id.targets)).setText("" + ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getMaxTargets());
                ((TextView) infoView.findViewById(R.id.power)).setText("" + ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getDiceLightInfantry());


                View nameContainerView = infoView.findViewById(R.id.mode_name_container);

                weaponToViewMap.put(wargear2, nameContainerView);

                if (enabled) {
                    nameContainerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            BusProvider.getInstance().post(new WeaponModeSelectedEvent(wargear2));
                            BusProvider.getInstance().post(new TargetCharacterSelectionStateEndEvent());
                        }
                    });


                } else {
                    infoView.findViewById(R.id.mode_name_container).setEnabled(false);
                }

                modesContainer.addView(infoView);
            }
        }

        return view;
    }

    @Subscribe
    public void onWeaponModeSelectedEvent(AttackOverlayWeaponFragment.WeaponModeSelectedEvent event) {

        for (int id : this.weaponToViewMap.keySet()) {
            if (id == event.getWargearId()) {
                this.weaponToViewMap.get(id).setSelected(true);
            } else {
                this.weaponToViewMap.get(id).setSelected(false);
            }
        }

    }


    public static class WeaponModeSelectedEvent {
        private int wargearId;

        public WeaponModeSelectedEvent(int wargearId) {
            this.wargearId = wargearId;
        }

        public int getWargearId() {
            return wargearId;
        }
    }

}
