package com.landsofruin.companion.characterinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.TutorialShowWeaponInfoEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class CharacterInfoCardFragment extends FrameLayout {

    private static final String ARGUMENT_PC_ID = "ARGUMENT_PC_ID";
    private static final String ARGUMENT_IS_IN_SELECTION_MODE = "ARGUMENT_IS_IN_SELECTION_MODE";

    private CharacterState character;
    private ViewGroup actionsContainerView;
    private ViewGroup weaponsContainerView;
    private ViewGroup skillsContainerView;
    private ViewGroup effectsContainerView;
    private ViewGroup wargearContainerView;

    private TextView wargearWeightTextView;


    private TextView suppressionText;
    private boolean weaponsContainerInitialised = false;
    private TextView offensiveModifiersText;
    private TextView defensiveModifiersText;
    private boolean isInSelectionMode;

    public CharacterInfoCardFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CharacterInfoCardFragment(Context context) {
        super(context);
        init();
    }


    public void setInSelectionMode(boolean inSelectionMode) {
        isInSelectionMode = inSelectionMode;
    }

    public void setCharacter(CharacterState character) {
        this.character = character;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.getInstance().register(this);
        refreshCharacterInfo();

    }


    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.getInstance().unregister(this);
    }


    public void init() {


        View v = inflate(getContext(), R.layout.character_info_card_fragment,
                this);


        actionsContainerView = (ViewGroup) v.findViewById(R.id.actions_info);
        weaponsContainerView = (ViewGroup) v.findViewById(R.id.weapons_info);
        skillsContainerView = (ViewGroup) v.findViewById(R.id.skills_info);
        effectsContainerView = (ViewGroup) v.findViewById(R.id.effects_info);
        wargearContainerView = (ViewGroup) v.findViewById(R.id.wargear_info);


        wargearWeightTextView = (TextView) v
                .findViewById(R.id.wargear_total_weight);


        offensiveModifiersText = (TextView) v.findViewById(R.id.offensive_modifier);
        defensiveModifiersText = (TextView) v.findViewById(R.id.defensive_modifier);
        suppressionText = (TextView) v.findViewById(R.id.suppression);


        if (isInSelectionMode) {
            actionsContainerView.setVisibility(View.GONE);
            effectsContainerView.setVisibility(View.GONE);
            v.findViewById(R.id.offensive_container).setVisibility(View.GONE);
            v.findViewById(R.id.suppression_container).setVisibility(View.GONE);
            v.findViewById(R.id.defensive_container).setVisibility(View.GONE);
            v.findViewById(R.id.effects_title).setVisibility(View.GONE);
            v.findViewById(R.id.actions_title).setVisibility(View.GONE);
        }


        refreshCharacterInfo();

    }


    private void showWeaponsInfo() {

        if (this.character == null) {
            return;
        }

        if (weaponsContainerInitialised) {
            return;
        }


        weaponsContainerInitialised = true;
        List<Integer> wg = this.character.getWargear();
        ArrayList<Integer> handledIds = new ArrayList<>();
        weaponsContainerView.removeAllViews();

        for (Integer wargear : wg) {
            if (!(LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearOffensive)) {
                continue;
            }

            if (handledIds.contains(LookupHelper.getInstance().getWargearFor(wargear).getWeaponId())) {
                continue;
            }

            final WargearOffensive offensiveWargear = (WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear);

            View oneWeapon = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.info_one_weapon_group_for_character,
                    weaponsContainerView, false);

            oneWeapon.findViewById(R.id.tutorial_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new TutorialShowWeaponInfoEvent(offensiveWargear.getWeaponId()));
                }
            });


            ((TextView) oneWeapon.findViewById(R.id.weapon_name))
                    .setText(LookupHelper.getInstance().getWargearFor(wargear).getName());

            ((TextView) oneWeapon.findViewById(R.id.total_ammo)).setText(""
                    + character.getAmmoFor(LookupHelper.getInstance().getWargearFor(wargear)
                    .getWeaponId()));

            if (offensiveWargear.getBulletsPerAction() <= 0) {
                oneWeapon.findViewById(R.id.total_ammo)
                        .setVisibility(View.GONE);
                ((TextView) oneWeapon.findViewById(R.id.ammo_remaining_label))
                        .setText("");
            }


            Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(offensiveWargear.getCategory());
            if (resource != null) {
                ((ImageView) oneWeapon.findViewById(R.id.weapon_image)).setImageResource(resource);
            } else {
                oneWeapon.findViewById(R.id.weapon_image).setVisibility(View.GONE);
            }

            ViewGroup modes = (ViewGroup) oneWeapon
                    .findViewById(R.id.firing_modes_container);


            for (Integer wargear2 : wg) {

                if (LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId() == LookupHelper.getInstance().getWargearFor(wargear).getWeaponId()) {
                    if (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)) {
                        continue;
                    }

                    WargearOffensive thisWG = ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2));

                    View oneMode = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                            R.layout.one_weapon_mode_character_info, modes,
                            false);

                    ((TextView) oneMode.findViewById(R.id.mode)).setText(thisWG
                            .getModeName());

                    ((TextView) oneMode.findViewById(R.id.targets)).setText(""
                            + thisWG.getMaxTargets());


                    ((TextView) oneMode.findViewById(R.id.power)).setText(""
                            + ((WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear2))
                            .getDiceLightInfantry());

                    ((TextView) oneMode.findViewById(R.id.bullets_per_action))
                            .setText("" + thisWG.getBulletsPerAction());

                    modes.addView(oneMode);
                }
            }

            weaponsContainerView.addView(oneWeapon);
            handledIds.add(LookupHelper.getInstance().getWargearFor(wargear).getWeaponId());
        }

    }

    private void showActionsInfo() {
        if (this.character == null) {
            return;
        }

        actionsContainerView.removeAllViews();


        List<Integer> actions = this.character.getCurrentActions();
        for (Integer action : actions) {
            View oneAction = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.info_one_asigned_action_for_character,
                    actionsContainerView, false);

            ((TextView) oneAction.findViewById(R.id.action_name))
                    .setText(LookupHelper.getInstance().getActionFor(action).getName());

            ((TextView) oneAction.findViewById(R.id.action_description))
                    .setText(LookupHelper.getInstance().getActionFor(action).getDescription());
            ((ImageView) oneAction.findViewById(R.id.action_icon)).setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(action)));


            actionsContainerView.addView(oneAction);

        }

    }

    private void showSkillsInfo() {


        skillsContainerView.removeAllViews();

        List<Integer> handledSkills = new ArrayList<Integer>();
        List<Integer> skills = this.character.getSkills();
        for (Integer skill : skills) {
            if (handledSkills.contains(skill)) {
                continue;
            }
            handledSkills.add(skill);
            View oneSkill = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.info_one_skill_for_character, skillsContainerView,
                    false);

            int count = 0;
            for (int skillState : skills) {
                if (skillState == skill) {
                    ++count;
                }
            }

            if (count > 1) {
                ((TextView) oneSkill.findViewById(R.id.skill_name)).setText(""
                        + count + " X " + LookupHelper.getInstance().getSkillFor(skill).getName());

            } else {
                ((TextView) oneSkill.findViewById(R.id.skill_name))
                        .setText(LookupHelper.getInstance().getSkillFor(skill).getName());
            }

            ((TextView) oneSkill.findViewById(R.id.skill_description))
                    .setText(LookupHelper.getInstance().getSkillFor(skill).getDescription());

            skillsContainerView.addView(oneSkill);
        }

    }

    public Context getActivity() {
        return getContext();
    }

    private void showEffectsInfo() {

        effectsContainerView.removeAllViews();


        int defModifier = character.getCurrentDefensiveModifier(((BaseGameActivity) getActivity()).getGame());
        int offModifier = character.getCurrentOffensiveModifier(((BaseGameActivity) getActivity()).getGame());


        offensiveModifiersText.setText("" + offModifier);
        defensiveModifiersText.setText("" + defModifier);
        suppressionText.setText("" + character.getCurrentSuppression() + "%");


        List<CharacterEffect> effects = this.character
                .getCharacterEffects();
        ArrayList<Integer> handledEffects = new ArrayList<Integer>();

        for (CharacterEffect characterEffect : effects) {
            if (handledEffects.contains(characterEffect.getId())) {
                continue;
            }
            handledEffects.add(characterEffect.getId());

            int count = 0;
            for (CharacterEffect effect : effects) {
                if (effect.getId() == characterEffect.getId()) {
                    ++count;
                }
            }

            View oneEffect = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.info_one_effect_for_character,
                    effectsContainerView, false);

            if (count > 1) {
                ((TextView) oneEffect.findViewById(R.id.effect_name))
                        .setText("" + count + " X " + characterEffect.getName());
            } else {
                ((TextView) oneEffect.findViewById(R.id.effect_name))
                        .setText(characterEffect.getName());
            }

            ((ImageView) oneEffect.findViewById(R.id.effect_icon))
                    .setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(characterEffect.getIcon()));

            ((TextView) oneEffect.findViewById(R.id.effect_description))
                    .setText(characterEffect.getDescription());

            effectsContainerView.addView(oneEffect);

        }

        if (effects.isEmpty()) {

            View oneEffect = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.info_one_effect_for_character,
                    effectsContainerView, false);


            ((TextView) oneEffect.findViewById(R.id.effect_name))
                    .setText("No active effects");


            effectsContainerView.addView(oneEffect);
        }

    }

    private void showWargearInfo() {

        int movementPenalty = (int) ((1 - (character
                .getMovementModifierFromWeight())) * 100);

        wargearWeightTextView
                .setText("Total gear weight:   "
                        + ((float) Math.round(character.getTotalItemWeight() * 100) / 100)
                        + "kg\nMovement penalty: " + movementPenalty + "%");

        List<Integer> wg = this.character.getWargear();
        wargearContainerView.removeAllViews();
        for (Integer wargear : wg) {
            if (LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearOffensive) {
                continue;
            }

            View oneWeapon = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.info_one_wargear_for_character,
                    wargearContainerView, false);

            ((TextView) oneWeapon.findViewById(R.id.wargear_name))
                    .setText(LookupHelper.getInstance().getWargearFor(wargear).getName());

            ((TextView) oneWeapon.findViewById(R.id.wargear_description))
                    .setText(LookupHelper.getInstance().getWargearFor(wargear).getCategory() + " "
                            + LookupHelper.getInstance().getWargearFor(wargear).getWeight() + "kg");

            wargearContainerView.addView(oneWeapon);

        }

    }


    private void refreshCharacterInfo() {

        if (this.character == null) {
            return;
        }

        if (!isInSelectionMode) {
            showActionsInfo();
            showEffectsInfo();
        }
        showSkillsInfo();
        showWargearInfo();
        showWeaponsInfo();
    }

    public CharacterState getCharacter() {
        return character;
    }

    @Subscribe
    public void onGameStateChangedEvent(GameStateChangedEvent event) {
        refreshCharacterInfo();
    }
}
