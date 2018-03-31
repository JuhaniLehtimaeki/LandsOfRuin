package com.landsofruin.companion.cards;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.cards.events.AllCardsModeEvent;
import com.landsofruin.companion.cards.events.BlinkCardEvent;
import com.landsofruin.companion.cards.events.CardViewSelectedEvent;
import com.landsofruin.companion.cards.events.SingleCardModeSelectedEvent;
import com.landsofruin.companion.cards.events.SingleCardNoReorderModeSelectedEvent;
import com.landsofruin.companion.cards.events.UpdateCardPositionToHolderEvent;
import com.landsofruin.companion.characterinfo.CharacterInfoCardFragment;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.fragment.GameSetupTeamSelectFirebaseFragment;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.gameruleobjects.characterdata.CharacterStatModifier;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by juhani on 10/16/13.
 */
public class FriendlyCharacterView extends FrameLayout implements CharacterCard {
    private static final int FLIP_DURATION = 300;
    private static final Interpolator END_INTERPOLATOR = new DecelerateInterpolator();
    private static final Interpolator START_INTERPOLATOR = new AccelerateInterpolator();
    private boolean mShowingBack = false;
    private View cardFront;
    private View cardBack;
    private View rootLayout;
    private View overlay;
    private boolean thisCardSelected = false;
    CharacterStateHero character;
    private TextView characterNameText;
    private TextView suppressionText;
    private ViewGroup effectIconContainer;
    private TextView unresolvedHits;
    private ViewGroup assignedActionsContainer;

    private boolean dragInProgress = false;
    private View inCommandIcon;
    private View inSecondCommandIcon;
    private int defaultFlags;
    private ImageView characterImage;

    private ViewGroup suppressionIconContainer;
    private TextView characterNameBackText;
    private TextView attackStat;
    private TextView defenceStat;
    private View nonShootingInfoContainer;
    private TextView gearValue;
    private ImageView characterRoleImage;
    private boolean isSelectionMode;
    private boolean isRemoveEnabledMode;
    private View inTeamOverlay;
    private View minionsContainer;
    private TextView minionsNumber;
    private TextView minionsTitle;
    private View woundedIndicator1;
    private View woundedIndicator2;
    private View woundedIndicator3;
    private View woundedIndicator4;
    private View woundedIndicator5;
    private View woundedIndicator6;
    private TextView movementValueText;


    private View defenceModifierBG;
    private View attackModifierBG;
    private ViewGroup weaponsContainer;
    private CharacterInfoCardFragment infoFragment;
    private ImageView minionsRoleIcon;
    private View characterDead;
    private View characterUnconsious;
    private View characterPinned;

    public FriendlyCharacterView(Context context) {
        super(context);
        init();
    }

    public FriendlyCharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public void setCharacter(CharacterStateHero character) {
        this.character = character;
        this.infoFragment.setCharacter(this.character);
        updateUI();
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
    }

    public void setRemoveEnabledMode(boolean removeEnabledMode) {
        isRemoveEnabledMode = removeEnabledMode;
    }

    private void init() {
        View view = inflate(getContext(), R.layout.character_card, this);

        view.findViewById(R.id.flip_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });
        view.findViewById(R.id.flip_front_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

        characterImage = (ImageView) view.findViewById(R.id.character_image);

        weaponsContainer = (ViewGroup) view.findViewById(R.id.weapons_container);


        defenceModifierBG = view.findViewById(R.id.card_defence_bg);
        attackModifierBG = view.findViewById(R.id.card_attack_bg);


        inCommandIcon = view.findViewById(R.id.character_in_command_icon);
        inSecondCommandIcon = view.findViewById(R.id.character_second_in_command_icon);
        characterRoleImage = (ImageView) view.findViewById(R.id.character_role_icon);

        characterDead = view.findViewById(R.id.character_dead);
        characterUnconsious = view.findViewById(R.id.character_unconsious);
        characterPinned = view.findViewById(R.id.character_pinned);


        rootLayout = view.findViewById(R.id.topLevelLayout);


        inTeamOverlay = view.findViewById(R.id.in_team_overlay);

        minionsContainer = view.findViewById(R.id.minions_container);
        minionsNumber = (TextView) view.findViewById(R.id.minions_number);
        minionsTitle = (TextView) view.findViewById(R.id.minions_title);
        minionsRoleIcon = (ImageView) view.findViewById(R.id.minion_role_icon);


        movementValueText = (TextView) view.findViewById(R.id.movement);


        gearValue = (TextView) view.findViewById(R.id.gear_value);

        attackStat = (TextView) view.findViewById(R.id.attack);
        defenceStat = (TextView) view.findViewById(R.id.defence);


        suppressionText = (TextView) view.findViewById(R.id.suppression);
        cardFront = view.findViewById(R.id.card_front);
        cardBack = view.findViewById(R.id.card_back);

        suppressionIconContainer = ((ViewGroup) view.findViewById(R.id.suppression_icon_container));

        nonShootingInfoContainer = view.findViewById(R.id.non_shooting_info_container);


        characterNameText = ((TextView) view.findViewById(R.id.character_name));
        characterNameBackText = ((TextView) view.findViewById(R.id.character_name_back));


        unresolvedHits = ((TextView) view.findViewById(R.id.unresolved_hits));

        effectIconContainer = (ViewGroup) view
                .findViewById(R.id.effect_icon_container);
        assignedActionsContainer = (ViewGroup) view
                .findViewById(R.id.assigned_actions);


        woundedIndicator1 = view.findViewById(R.id.wounded_indicator_1);
        woundedIndicator2 = view.findViewById(R.id.wounded_indicator_2);
        woundedIndicator3 = view.findViewById(R.id.wounded_indicator_3);
        woundedIndicator4 = view.findViewById(R.id.wounded_indicator_4);
        woundedIndicator5 = view.findViewById(R.id.wounded_indicator_5);
        woundedIndicator6 = view.findViewById(R.id.wounded_indicator_6);


        overlay = view.findViewById(R.id.click_stealer);

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectionMode) {
                    BusProvider.getInstance().post(new GameSetupTeamSelectFirebaseFragment.SelectDeselectEvent(character.getIdentifier()));
                } else {

                    if (!dragInProgress) {
                        BusProvider.getInstance().post(new CardViewSelectedEvent(FriendlyCharacterView.this));
                    }
                }
            }
        });


        defaultFlags = characterNameText.getPaintFlags();
        if (isRemoveEnabledMode) {


            infoFragment = new CharacterInfoCardFragment(getContext());
            ((ViewGroup) view.findViewById(R.id.character_info_fragment)).addView(infoFragment);


            ((ViewGroup) nonShootingInfoContainer).removeAllViews();
            this.overlay.setVisibility(View.GONE);


            View editButtonsView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.game_setup_character_remove_button, ((ViewGroup) nonShootingInfoContainer), false);

            editButtonsView.findViewById(R.id.remove_character_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new GameSetupTeamSelectFirebaseFragment.SelectDeselectEvent(character.getIdentifier()));
                }
            });


            ((ViewGroup) nonShootingInfoContainer).addView(editButtonsView);

        } else {

            if (isSelectionMode) {
                view.findViewById(R.id.flip_front_button).setVisibility(View.GONE);
                ((ViewGroup) nonShootingInfoContainer).removeAllViews();


            } else {
                infoFragment = new CharacterInfoCardFragment(getContext());
                ((ViewGroup) view.findViewById(R.id.character_info_fragment)).addView(infoFragment);
            }
        }

        updateUI();


    }


    void flipCard() {
        if (!mShowingBack) {
            mShowingBack = true;


            final ViewPropertyAnimator animator = rootLayout.animate();
            animator.rotationY(90f).alpha(0.9f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    cardBack.setVisibility(View.VISIBLE);
                    cardFront.setVisibility(View.GONE);
                    rootLayout.animate().rotationY(180f).alpha(1f).setDuration(FLIP_DURATION / 2).setInterpolator(END_INTERPOLATOR);

                    animator.setListener(null);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animator.setListener(null);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).setInterpolator(START_INTERPOLATOR).setDuration(FLIP_DURATION / 2).start();

        } else {
            mShowingBack = false;


            final ViewPropertyAnimator animator = rootLayout.animate();
            animator.rotationY(90f).alpha(0.9f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    cardBack.setVisibility(View.GONE);
                    cardFront.setVisibility(View.VISIBLE);
                    rootLayout.animate().rotationY(0f).alpha(1f).setDuration(FLIP_DURATION / 2).setInterpolator(END_INTERPOLATOR);

                    animator.setListener(null);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animator.setListener(null);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).setInterpolator(START_INTERPOLATOR).setDuration(FLIP_DURATION / 2).start();
        }

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.getInstance().register(this);
        updateUI();

    }


    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.getInstance().unregister(this);
    }


    @Subscribe
    public void onCardSelectedEvent(CardViewSelectedEvent event) {
        if (event.getSelectedCharacterCardFragment() != this) {
            thisCardSelected = false;
            overlay.setVisibility(View.VISIBLE);

            if (mShowingBack) {
                flipCard();
            }
        } else {
            thisCardSelected = true;
            overlay.setVisibility(View.GONE);

        }
        highlightIfNeeded();
    }

    @Subscribe
    public void onSingleCardModeSelectedEvent(SingleCardModeSelectedEvent event) {

        if (!thisCardSelected) {
            overlay.setVisibility(View.VISIBLE);
            if (mShowingBack) {
                flipCard();
            }
        }
    }

    @Subscribe
    public void onAllCardsModeEvent(AllCardsModeEvent event) {

        overlay.setVisibility(View.GONE);

    }


    void blinkThisCard() {

        rootLayout.clearAnimation();

        final ViewPropertyAnimator animator = rootLayout.animate();
        animator.scaleY(1.2f).scaleX(1.2f).setDuration(200).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rootLayout.animate().scaleY(1f).scaleX(1f).setDuration(200).setInterpolator(new BounceInterpolator());
                animator.setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animator.setListener(null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    @Subscribe
    public void onBlinkCardEvent(BlinkCardEvent event) {

        if (this.character.getIdentifier().equals(event.getTargetCharacterId())) {
            blinkThisCard();
        }

    }

    @Subscribe
    public void onUpdateCardPositionToHolderEvent(UpdateCardPositionToHolderEvent event) {
        updatePosition();
    }


    @Subscribe
    public void onSingleCardNoReorderModeSelectedEvent(SingleCardNoReorderModeSelectedEvent event) {

        if (!thisCardSelected) {
            if (overlay.getVisibility() == View.GONE) {

                overlay.setVisibility(View.VISIBLE);
                overlay.setAlpha(1f);

            }
            if (mShowingBack) {
                flipCard();
            }
        }

    }

    private void updateUI() {
        if (getActivity() == null || this.character == null) {
            return;
        }

        Picasso.with(this.getContext())
                .load(character.getCardPictureUri())
                .into(characterImage);

        GameState game = ((BaseGameActivity) getContext()).getGame();


        if (game == null) {
            return;
        }
        TeamState team = game.findTeamByCharacterIdentifier(this.character.getIdentifier());

        if (getContext() instanceof GameSetupActivity) {
            team = game.getMe().getTeam();
        }

        if (team == null) {
            return;
        }

        gearValue.setText("" + this.character.getCurrentGearValue());
        characterNameText.setText(this.character.getName());


        if (this.character.getName().length() > 11) {
            characterNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }


        characterNameBackText.setText(this.character.getName());

        Picasso.with(getActivity()).load(this.character.getRoleIconURL()).into(characterRoleImage);

        if (character.isDead()) {
            characterDead.setVisibility(VISIBLE);
            characterPinned.setVisibility(GONE);
            characterUnconsious.setVisibility(GONE);

        } else if (character.isUnconsious()) {
            characterDead.setVisibility(GONE);
            characterPinned.setVisibility(GONE);
            characterUnconsious.setVisibility(VISIBLE);

        } else if (character.isPinned()) {
            characterDead.setVisibility(GONE);
            characterPinned.setVisibility(VISIBLE);
            characterUnconsious.setVisibility(GONE);

        } else {
            characterDead.setVisibility(GONE);
            characterPinned.setVisibility(GONE);
            characterUnconsious.setVisibility(GONE);
        }


        int suppressionValue = this.character.getCurrentSuppression();

        suppressionIconContainer.removeAllViews();
        if (suppressionValue > 0) {
            suppressionText.setText("" + (this.character.getCurrentSuppression() < 100 ? " " : "") + (this.character.getCurrentSuppression() < 10 ? "0" : "") + this.character.getCurrentSuppression());


            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.suppression_1);
            suppressionIconContainer.addView(iv);


            if (suppressionValue >= 25) {
                iv = new ImageView(getActivity());
                iv.setImageResource(R.drawable.suppression_2);
                suppressionIconContainer.addView(iv);
            }

            if (suppressionValue >= 50) {
                iv = new ImageView(getActivity());
                iv.setImageResource(R.drawable.suppression_3);
                suppressionIconContainer.addView(iv);
            }

            if (suppressionValue >= 75) {
                iv = new ImageView(getActivity());
                iv.setImageResource(R.drawable.suppression_4);
                suppressionIconContainer.addView(iv);
            }

            if (suppressionValue >= 100) {
                iv = new ImageView(getActivity());
                iv.setImageResource(R.drawable.suppression_5);
                suppressionIconContainer.addView(iv);
            }


            if (suppressionValue >= 100) {

                suppressionText.setBackgroundResource(R.drawable.card_suppression_red);
                suppressionText.setTextColor(0xffff0000);
            } else {

                suppressionText.setBackgroundResource(R.drawable.card_suppression_orange);
                suppressionText.setTextColor(0xffff7e00);
            }


        } else {
            suppressionText.setText(" 00");
            suppressionText.setBackgroundResource(R.drawable.card_suppression_blue);
            suppressionText.setTextColor(0xff0f91a4);
        }


        int hits = this.character.getUnresolvedHits().size();

        if (hits == 0) {
            unresolvedHits.setBackgroundResource(R.drawable.card_hit_blue);
            unresolvedHits.setTextColor(0xff0f91a4);

        } else if (hits < 5) {
            unresolvedHits.setBackgroundResource(R.drawable.card_hit_orange);
            unresolvedHits.setTextColor(0xffff7e00);
        } else {
            unresolvedHits.setBackgroundResource(R.drawable.card_hit_red);
            unresolvedHits.setTextColor(0xffff0000);
        }
        unresolvedHits.setText("" + (hits < 10 ? "0" : "") + hits);


        effectIconContainer.removeAllViews();
        for (final CharacterEffect characterEffect : this.character.getCharacterEffects()) {

            View oneIconView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.one_effect, effectIconContainer, false);

            final ImageView icon = (ImageView) oneIconView
                    .findViewById(R.id.effect_icon);
            icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(characterEffect.getIcon()));

            effectIconContainer.addView(oneIconView);


        }


        assignedActionsContainer.removeAllViews();


        int movemementValue;

        if (game.getPhase().isMine() && (game.getPhase().getPrimaryPhase() == PrimaryPhase.ACTION || game.getPhase().getPrimaryPhase() == PrimaryPhase.MOVE)) {
            movemementValue = this.character.getMovementAllowance(game);
        } else {
            movemementValue = character.getMovementAllowanceForNextTurn(game);
        }

        movementValueText.setText("" + (movemementValue < 10 ? "0" : "") + movemementValue + "''");

        for (final Integer action : this.character.getCurrentActions()) {

            View oneIconView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.one_action_in_card, assignedActionsContainer, false);

            final ImageView icon = (ImageView) oneIconView
                    .findViewById(R.id.action_icon);


            if (this.character.getActionsPerformed().contains(action)) {
                icon.setAlpha(0.5f);
            }

            icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(action)));
            assignedActionsContainer.addView(oneIconView);
        }
        highlightIfNeeded();


        inCommandIcon.setVisibility(View.GONE);
        inSecondCommandIcon.setVisibility(View.GONE);


        if (this.character.getIdentifier().equals(team.getCharacterInCommandId())) {
            inCommandIcon.setVisibility(View.VISIBLE);
        } else if (this.character.getIdentifier().equals(team.getCharacterSecondInCommandId())) {
            inSecondCommandIcon.setVisibility(View.VISIBLE);
        }


        weaponsContainer.removeAllViews();


        List<Integer> wg = this.character.getWargear();
        ArrayList<Integer> handledIds = new ArrayList<>();

        for (Integer wargear : wg) {
            if (!(LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearOffensive) && !(LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearConsumable)) {
                continue;
            }

            if (handledIds.contains(LookupHelper.getInstance().getWargearFor(wargear).getWeaponId())) {
                continue;
            }


            View view =
                    ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                            R.layout.weapon_info, assignedActionsContainer, false);

            ImageView oneWeaponView = (ImageView) view.findViewById(R.id.highlight_icon);

            TextView skill = (TextView) view.findViewById(R.id.skill_value);


            Integer resource;
            String skillText;
            int bulletsLeft;
            int mostBulletsNeeded;

            if ((LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearConsumable)) {
                WargearConsumable wargearConsumable = (WargearConsumable) LookupHelper.getInstance().getWargearFor(wargear);
                if (!"rpg".equalsIgnoreCase(wargearConsumable.getCategory())) {
                    continue;
                }

                skillText = "";
                bulletsLeft = 1;
                mostBulletsNeeded = 1;
                resource = WargearCategoryToIconMapper.getIconResourceForCategory(wargearConsumable.getCategory());
            } else {
                final WargearOffensive offensiveWargear = (WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear);
                handledIds.add(LookupHelper.getInstance().getWargearFor(wargear).getWeaponId());
                resource = WargearCategoryToIconMapper.getIconResourceForCategory(offensiveWargear.getCategory());
                skillText = "" + this.character.getSkillModifiersForWeapon(offensiveWargear, game);

                mostBulletsNeeded = Integer.MAX_VALUE;

                LinkedList<Wargear> allModes = WargearManager.getInstance().getWargearByWeaponID(offensiveWargear.getWeaponId());
                for (Wargear wgForModes : allModes) {
                    mostBulletsNeeded = Math.min(mostBulletsNeeded, ((WargearOffensive) wgForModes).getBulletsPerAction());
                }

                bulletsLeft = this.character.getAmmoFor(offensiveWargear.getWeaponId());
            }


            if (resource != null) {
                oneWeaponView.setImageResource(resource);

                skill.setText(skillText);

                weaponsContainer.addView(view);
            }
            ImageView outOfBulletsWarning = (ImageView) view.findViewById(R.id.actions_left);


            if (mostBulletsNeeded > 0) {


                if (bulletsLeft / mostBulletsNeeded == 0) {
                    outOfBulletsWarning.setImageResource(R.drawable.card_no_ammo_warning);
                    view.setBackgroundResource(R.drawable.card_weapon_ammo_out);
                    outOfBulletsWarning.setVisibility(View.VISIBLE);
                } else if (bulletsLeft / mostBulletsNeeded <= 2) {
                    outOfBulletsWarning.setImageResource(R.drawable.card_low_ammo_warning);
                    view.setBackgroundResource(R.drawable.card_weapon_ammo_warning);
                    outOfBulletsWarning.setVisibility(View.VISIBLE);
                } else {
                    outOfBulletsWarning.setImageResource(R.drawable.card_ammo_normal);
                    view.setBackgroundResource(R.drawable.card_weapon);
                    outOfBulletsWarning.setVisibility(View.VISIBLE);
                }

            } else {
                outOfBulletsWarning.setVisibility(View.GONE);
            }

        }

        int attackMod = character.getCurrentOffensiveModifier(game);

        if (attackMod <= -10) {
            attackStat.setText("" + -attackMod);
        } else {
            attackStat.setText("0" + -attackMod);
        }


        if (attackMod < -5) {
            attackModifierBG.setBackgroundResource(R.drawable.card_attack_red);
            attackStat.setTextColor(0xffff0000);
        } else if (attackMod < 0) {
            attackModifierBG.setBackgroundResource(R.drawable.card_attack_orange);
            attackStat.setTextColor(0xffff7e00);
        } else {
            attackModifierBG.setBackgroundResource(R.drawable.card_attack_blue);
            attackStat.setTextColor(0xff03778f);
        }


        int defenceMod = character.getCurrentDefensiveModifier(game);

        if (defenceMod <= -10) {
            defenceStat.setText("" + -defenceMod);
        } else {
            defenceStat.setText("0" + -defenceMod);
        }


        if (defenceMod < -5) {
            defenceModifierBG.setBackgroundResource(R.drawable.card_defence_red);
            defenceStat.setTextColor(0xffff0000);

        } else if (defenceMod < 0) {
            defenceModifierBG.setBackgroundResource(R.drawable.card_defence_orange);
            defenceStat.setTextColor(0xffff7e00);
        } else {
            defenceModifierBG.setBackgroundResource(R.drawable.card_defence_blue);
            defenceStat.setTextColor(0xff03778f);
        }


        //update minions
        int minions = character.getMinionCount(team);
        if (minions > 0) {
            minionsContainer.setVisibility(View.VISIBLE);
            minionsNumber.setText("" + minions);
            minionsTitle.setText(character.getMinionName(team));
            Picasso.with(getActivity()).load(this.character.getFirstCharacterStateSquad(team).getRoleIconURL()).into(minionsRoleIcon);
        } else {
            minionsContainer.setVisibility(View.GONE);
        }

        woundedIndicator1.setVisibility(View.GONE);
        woundedIndicator2.setVisibility(View.GONE);
        woundedIndicator3.setVisibility(View.GONE);
        woundedIndicator4.setVisibility(View.GONE);
        woundedIndicator5.setVisibility(View.GONE);
        woundedIndicator6.setVisibility(View.GONE);

        List<CharacterStatModifier> modifiers = this.character.getModifiers();
        for (CharacterStatModifier mod : modifiers) {
            if (mod.getLocations() != null) {


                for (Integer location : mod.getLocations()) {

                    switch (location) {
                        case 1:
                            woundedIndicator1.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            woundedIndicator2.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            woundedIndicator3.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            woundedIndicator4.setVisibility(View.VISIBLE);
                            break;
                        case 5:
                            woundedIndicator5.setVisibility(View.VISIBLE);
                            break;
                        case 6:
                            woundedIndicator6.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        }

    }


    void highlightIfNeeded() {

        //FIXME: redo the highlight
//        if (thisCardSelected) {
//            rootLayout.setBackgroundColor(0xFFFFFFFF);
//        } else {
//
//            if (isHighlighed()) {
//                rootLayout.setBackgroundColor(0xFFFF6666);
//            } else {
//                rootLayout.setBackgroundColor(0xFF111111);
//            }
//        }
    }


    boolean isHighlighed() {


        GameState game = ((BaseGameActivity) getActivity()).getGame();

        if (game.getPhase().getPrimaryPhase() == PrimaryPhase.GAME_END) {

            return false;
        } else if (game.getPhase().getPrimaryPhase() == PrimaryPhase.PRE_GAME) {


            return false;
        }


        if (game.getPhase().isMine()) {


            if (game.getPhase().getPrimaryPhase() == PrimaryPhase.ZOMBIES) {
                return false;

            } else if (game.getPhase().getPrimaryPhase() == PrimaryPhase.ACTION) {
                return this.character.hasActionsToPerform();
            } else if (game.getPhase().getPrimaryPhase() == PrimaryPhase.MOVE) {
                return !this.character.isMovedOnMapThisTurn();
            } else if (game.getPhase().getPrimaryPhase() == PrimaryPhase.ASSIGN_ACTIONS) {
                return this.character.getRemainingActionPoints(game) > 0;
            } else {
                return false;
            }

        } else {

            if (game.getPhase().getPrimaryPhase() == PrimaryPhase.ZOMBIES) {


                return false;
            } else {

                return false;
            }
        }


    }


    @Subscribe
    public void onGameStateChangedEvent(GameStateChangedEvent event) {
        updateUI();
    }

    @Subscribe
    public void onGameStateChangedEvent(GameSetupTeamSelectFirebaseFragment.SelectedTeamChangedEvent event) {
        if (isSelectionMode) {
            if (event.getSelectedIds().contains(this.character.getIdentifier())) {
                inTeamOverlay.setVisibility(View.VISIBLE);
            } else {
                inTeamOverlay.setVisibility(View.GONE);
            }
        }
    }


    public CharacterState getCharacter() {
        return this.character;
    }

    @Override
    public View getView() {
        return this;
    }


    void updatePosition() {

        int[] coordinates = new int[2];
        rootLayout.getLocationOnScreen(coordinates);
        CardLocationsHolder.setLocationFor(this.character.getIdentifier(), coordinates);
    }


    private Context getActivity() {
        return getContext();
    }
}
