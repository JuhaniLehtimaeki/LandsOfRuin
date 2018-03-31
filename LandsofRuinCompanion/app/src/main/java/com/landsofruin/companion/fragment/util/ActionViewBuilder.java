package com.landsofruin.companion.fragment.util;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameConstants;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.action.ActionContainerForAssignActions;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.AssignActionForCharacterTransition;
import com.landsofruin.companion.state.transition.RemoveActionForCharacterTransition;
import com.landsofruin.companion.utils.ColourUtils;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by juhani on 11/30/13.
 */
public abstract class ActionViewBuilder {


    public static View buildActionView(final ActionContainerForAssignActions actionContainer, Action actionState, CharacterState characterState, LayoutInflater inflater, boolean isActionAssigned, BaseGameActivity activity, ViewGroup parent) {

        if (actionState.getId() == Action.ACTION_ID_AIM_AND_SHOOTING || actionState.getId() == Action.ACTION_ID_SHOOTING || actionState.getId() == Action.ACTION_ID_CC) {
            return buildShootingCCActionView(actionContainer, actionState, characterState, inflater, isActionAssigned, activity, parent);
        } else {
            return buildDefaultActionView(actionContainer, actionState, characterState, inflater, isActionAssigned, activity, parent);
        }


    }


    private static WeakHashMap<String, View> viewCache = new WeakHashMap<>();

    private static View buildShootingCCActionView(final ActionContainerForAssignActions actionContainer, final Action action, final CharacterState character, LayoutInflater inflater, boolean isActionAssigned, final BaseGameActivity activity, ViewGroup parent) {

        boolean isShooting = action.getId() == Action.ACTION_ID_SHOOTING || action.getId() == Action.ACTION_ID_AIM_AND_SHOOTING;
        boolean isAiming = action.getId() == Action.ACTION_ID_AIM_AND_SHOOTING;

        GameState gameState = activity.getGame();


        String key = character.getIdentifier() + action.getId() + gameState.getPhase().getGameTurn();
        View cachedView = viewCache.get(key);


        final View oneActionView = cachedView == null ? inflater
                .inflate(R.layout.one_assign_action_shooting, parent,
                        false) : cachedView;

        final TextView disabledText = (TextView) oneActionView
                .findViewById(R.id.disabled_text);


        oneActionView.setClickable(true);
        oneActionView.setEnabled(true);
        disabledText.setVisibility(View.INVISIBLE);

        if (isActionAssigned) {
            oneActionView.setSelected(true);
            oneActionView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    activity
                            .sendToServer(new RemoveActionForCharacterTransition(
                                    character.getIdentifier(), action.getId()));
                }
            });

        } else {

            oneActionView.setSelected(false);
            if (!actionContainer.isEnabled()) {
                oneActionView.setClickable(false);
                oneActionView.setEnabled(false);
                disabledText.setVisibility(View.VISIBLE);
                disabledText.setText(actionContainer.getDisabledText());

            } else {

                oneActionView.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        activity
                                .sendToServer(new AssignActionForCharacterTransition(
                                        character.getIdentifier(), action.getId()));

                    }
                });
            }
        }


        if (cachedView != null) {
            if (oneActionView.getParent() != null) {
                ((ViewGroup) oneActionView.getParent()).removeView(oneActionView);
            }
            return oneActionView;
        }

        viewCache.put(key, oneActionView);

        final TextView titleText = (TextView) oneActionView
                .findViewById(R.id.title);


        final ViewGroup infoContainer = (ViewGroup) oneActionView.findViewById(R.id.info_container);

        final TextView descriptionText = (TextView) oneActionView
                .findViewById(R.id.description);

        descriptionText.setText(action.getDescription());

        ImageView actionIcon = ((ImageView) oneActionView.findViewById(R.id.action_icon));
        actionIcon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(action.getId())));

        final TextView apsText = (TextView) oneActionView
                .findViewById(R.id.aps);

        titleText.setText(Html.fromHtml(action.getName()));
        apsText.setText("" + action.getActionPoints() + "AP");

        List<Integer> handledGroups = new ArrayList<>();

        List<Integer> wargear = character.getWargear();
        for (final Integer wargear2 : wargear) {

            if (isShooting && (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                    || !"Ranged".equalsIgnoreCase(LookupHelper.getInstance().getWargearFor(wargear2)
                    .getType()))) {
                continue;
            }

            if (!isShooting && (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                    || !"cc".equalsIgnoreCase(LookupHelper.getInstance().getWargearFor(wargear2)
                    .getType()))) {
                continue;
            }


            if (character.isWargearSkillRequirementsMet(
                    LookupHelper.getInstance().getWargearFor(wargear2))) {


                if (!handledGroups.contains(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId())) {
                    handledGroups.add(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId());


                    View infoView = inflater.inflate(R.layout.one_weapon_group_shooting_info_header, infoContainer, false);

                    ((TextView) infoView.findViewById(R.id.ammo)).setText("" + character.getAmmoFor(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId()));


                    ((TextView) infoView.findViewById(R.id.weapon_name)).setText(LookupHelper.getInstance().getWargearFor(wargear2).getName());
                    Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(LookupHelper.getInstance().getWargearFor(wargear2).getCategory());

                    if (resource != null) {
                        ((ImageView) infoView.findViewById(R.id.weapon_image)).setImageResource(resource);
                    } else {
                        infoView.findViewById(R.id.weapon_image).setVisibility(View.INVISIBLE);
                    }


                    if (!isShooting) {
//                        infoView.findViewById(R.id.mid_range).setVisibility(View.GONE);
//                        infoView.findViewById(R.id.long_range).setVisibility(View.GONE);

//                        ((TextView) infoView.findViewById(R.id.short_range)).setText("Hit chance");
                    }

                    infoContainer.addView(infoView);


                }

                boolean enabled = true;

                int bulletsPerAction = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getBulletsPerAction();

                if (character instanceof CharacterStateSquad) {
                    bulletsPerAction = bulletsPerAction * ((CharacterStateSquad) character).getSquadSize();
                }

                int ammo = character.getAmmoFor(LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId());

                if (ammo < bulletsPerAction) {
                    enabled = false;
                }


                View infoView = inflater.inflate(R.layout.one_weapon_shooting_info, infoContainer, false);
                ((TextView) infoView.findViewById(R.id.mode_name)).setText(((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getModeName());
                ((TextView) infoView.findViewById(R.id.ammo_info)).setText("" + bulletsPerAction);

                if (!enabled) {
                    ((TextView) infoView.findViewById(R.id.ammo_info)).setTextColor(0xFFFF0000);
                }


                int targets = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getMaxTargets();

                if (character instanceof CharacterStateSquad) {
                    targets = targets * ((CharacterStateSquad) character).getSquadSize();
                }


                ((TextView) infoView.findViewById(R.id.targets)).setText("" + targets);
                ((TextView) infoView.findViewById(R.id.power)).setText("" + ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getDiceLightInfantry());


                int targetNumber = character.getTargetNumberShort((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2), gameState);
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

                if (isShooting) {

                    targetNumber = character.getTargetNumberMid((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2), gameState);
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

                    targetNumber = character.getTargetNumberLong((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2), gameState);
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

                int noise = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2)).getNoiseLevel(character);


                if (character instanceof CharacterStateSquad) {
                    noise = noise * ((CharacterStateSquad) character).getSquadSize();
                }


                TextView noiseView = ((TextView) infoView.findViewById(R.id.noise));
                noiseView.setText("" + noise);
                ((View) noiseView.getParent()).setBackgroundColor(ColourUtils.getColourForNoise(noise));
                noiseView.setTextColor(ColourUtils.getTextColourForNoise(noise));


                infoContainer.addView(infoView);

            }
        }


        return oneActionView;
    }


    private static View buildDefaultActionView(final ActionContainerForAssignActions actionContainer, final Action action, final CharacterState character, LayoutInflater inflater, boolean isActionAssigned, final BaseGameActivity activity, ViewGroup parent) {


        final View oneActionView = inflater
                .inflate(R.layout.one_assign_action, parent,
                        false);

        final TextView titleText = (TextView) oneActionView
                .findViewById(R.id.title);

        final TextView disabledText = (TextView) oneActionView
                .findViewById(R.id.disabled_text);


        final TextView descriptionText = (TextView) oneActionView
                .findViewById(R.id.description);


        descriptionText.setText(action.getDescription());

        ImageView actionIcon = ((ImageView) oneActionView.findViewById(R.id.action_icon));
        actionIcon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(action.getId())));


        final TextView apsText = (TextView) oneActionView
                .findViewById(R.id.aps);

        titleText.setText(Html.fromHtml(action.getName()));
        apsText.setText("" + action.getActionPoints() + "AP");


        if (isActionAssigned) {

            oneActionView.setSelected(true);
            oneActionView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    activity
                            .sendToServer(new RemoveActionForCharacterTransition(
                                    character.getIdentifier(), action.getId()));
                }
            });

        } else {

            oneActionView.setSelected(false);
            if (!actionContainer.isEnabled()) {
                oneActionView.setClickable(false);
                oneActionView.setEnabled(false);
                disabledText.setVisibility(View.VISIBLE);
                disabledText.setText(actionContainer.getDisabledText());

            } else {

                oneActionView.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        activity
                                .sendToServer(new AssignActionForCharacterTransition(
                                        character.getIdentifier(), action.getId()));

                    }
                });
            }
        }


        return oneActionView;
    }


}
