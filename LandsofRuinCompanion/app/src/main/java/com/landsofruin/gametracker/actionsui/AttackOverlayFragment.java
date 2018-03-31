package com.landsofruin.gametracker.actionsui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.OpponentIsWaitingForYouEvent;
import com.landsofruin.companion.eventbus.TargetCharacterSelectionStateEndEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameConstants;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.NextAttackState;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.dice.DiceUtils;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.characterdata.UnresolvedHit;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.CancelAttackTransition;
import com.landsofruin.companion.state.transition.ChangeAttackMetadataTransition;
import com.landsofruin.companion.state.transition.PerformShootingTransition;
import com.landsofruin.companion.state.transition.StartAttackTransition;
import com.landsofruin.companion.utils.UIUtils;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AttackOverlayFragment extends Fragment {

    private static final int SELECTED_COLOUR = 0xFFB71C1C;
    private static final int UN_SELECTED_COLOUR = 0x00000000;


    boolean phase3setup = false;
    private TextView shooterName;
    private View shortButton;
    private View mediumButton;
    private View longButton;
    private boolean rangeShort = false;
    private boolean rangeMedium = false;
    private boolean rangeLong = false;

    private boolean selectionStateRangeShort = false;
    private boolean selectionStateRangeMedium = false;
    private boolean selectionStateRangeLong = false;


    private LayoutInflater inflater;
    private CharacterState shooter;
    private ArrayList<OneTarget> targets = new ArrayList<>();
    private ViewGroup targetsContainer;
    private View shootButton;
    private View cancelButton;
    private boolean meAttacking = false;
    private WargearOffensive wargear;
    private GameState gamestate;
    private int actionId;
    private boolean isCC = false;

    private View phase3View;
    private View phase2View;
    private View phase1View;
    private View waitingForEnemyView;
    private TextView infoTextView;
    private int selectedWeapon = -1;

    private boolean isAiming = false;
    private ViewGroup characterCardContainer;
    private ViewGroup weaponsTabs;
    private ViewPager modesPager;
    private ViewGroup explanationsContainer;
    private View phase1DoneButton;
    private View phase2_5_View;
    private View shootButton2_5;
    private View cancelButton2_5;
    private ViewGroup rangeTargetsContainer;
    private ViewGroup rangeShooterContainer;
    private TextView rangeText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View v = inflater.inflate(R.layout.attack_fragment_fragment, container, false);


        waitingForEnemyView = v.findViewById(R.id.waiting_for_enemy);

        // phase 1
        phase1View = v.findViewById(R.id.phase_1);

        v.findViewById(R.id.cancel_shooting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseGameActivity) getActivity()).sendToServer(new CancelAttackTransition());
            }
        });

        weaponsTabs = (ViewGroup) v.findViewById(R.id.weapons_tabs);
        modesPager = (ViewPager) v.findViewById(R.id.modes_pager);
        modesPager.setOffscreenPageLimit(2);

        phase1DoneButton = v.findViewById(R.id.done_phase_1_button);
        phase1DoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StartAttackTransition transition = new StartAttackTransition(shooter.getIdentifier(), actionId, selectedWeapon);
                ((GameActivity) getActivity()).sendToServer(transition);

            }
        });

        // phase 2
        phase2View = v.findViewById(R.id.phase_2);

        // phase 2_5
        phase2_5_View = v.findViewById(R.id.phase_2_5);


        rangeTargetsContainer = (ViewGroup) v.findViewById(R.id.range_targets_container);
        rangeShooterContainer = (ViewGroup) v.findViewById(R.id.range_shooter_container);


        shootButton2_5 = v.findViewById(R.id.shoot_button_2_5);
        cancelButton2_5 = v.findViewById(R.id.cancel_button_2_5);

        shortButton = v.findViewById(R.id.button_target_short_range);
        mediumButton = v.findViewById(R.id.button_target_medium_range);
        longButton = v.findViewById(R.id.button_target_long_range);

        targetsContainer = (ViewGroup) v.findViewById(R.id.targets_container);

        shortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionStateRangeShort = true;
                selectionStateRangeMedium = false;
                selectionStateRangeLong = false;

                refreshPhase2_5State();

            }
        });


        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionStateRangeShort = false;
                selectionStateRangeMedium = true;
                selectionStateRangeLong = false;


                refreshPhase2_5State();

            }
        });


        longButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionStateRangeShort = false;
                selectionStateRangeMedium = false;
                selectionStateRangeLong = true;

                refreshPhase2_5State();

            }
        });


        shootButton2_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rangeShort = selectionStateRangeShort;
                rangeMedium = selectionStateRangeMedium;
                rangeLong = selectionStateRangeLong;

                for (OneTarget target : targets) {
                    target.status = null;
                }

                sendChangesToServer();
            }
        });

        cancelButton2_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionStateRangeShort = false;
                selectionStateRangeMedium = false;
                selectionStateRangeLong = false;


                phase3setup = false;
                StartAttackTransition transition = new StartAttackTransition(shooter.getIdentifier(), actionId, wargear.getId());
                ((GameActivity) getActivity()).sendToServer(transition);
                BusProvider.getInstance().post(
                        new TargetCharacterSelectionStateEndEvent());
            }
        });

        // phase 3

        phase3View = v.findViewById(R.id.phase_3);
        characterCardContainer = (ViewGroup) v.findViewById(R.id.character_card_container);

        infoTextView = (TextView) v.findViewById(R.id.info_text);
        rangeText = (TextView) v.findViewById(R.id.range_text);


        shootButton = v.findViewById(R.id.shoot_button);
        cancelButton = v.findViewById(R.id.cancel_button);


        shootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<UnresolvedHit> hits = new ArrayList<>();
                for (OneTarget target : targets) {


                    int power = wargear.getDiceLightInfantry();

                    if (target.status == OneTarget.HitStatus.CRIT) {
                        ++power;
                    }

                    UnresolvedHit hit;
                    int range = UnresolvedHit.RANGE_CC;
                    if (isCC) {
                        hit = new UnresolvedHit(UnresolvedHit.TYPE_CC, power);
                        range = UnresolvedHit.RANGE_CC;
                    } else {
                        hit = new UnresolvedHit(UnresolvedHit.TYPE_SHOOTING, power);


                        if (rangeLong) {
                            range = UnresolvedHit.RANGE_LONG;
                        } else if (rangeMedium) {
                            range = UnresolvedHit.RANGE_MID;
                        } else if (rangeShort) {
                            range = UnresolvedHit.RANGE_SHORT;
                        }
                    }

                    hit.setRange(range);
                    hit.setTargetCharacterId(target.character.getIdentifier());
                    hit.setSourceCharacterId(shooter.getIdentifier());
                    hit.setSourceWargearId(wargear.getId());
                    hit.setHardCover(target.heavyCover);
                    hit.setSoftCover(target.lightCover);
                    hit.setAttackOfOpportunity(target.attackOfOpportunity);


                    if (target.status == OneTarget.HitStatus.MISS) {
                        hit.setIsMiss();
                    }

                    hits.add(hit);
                }


                ((BaseGameActivity) getActivity()).sendToServer(new PerformShootingTransition(actionId, shooter.getIdentifier(), hits, wargear.getId(), gamestate.getMe().getIdentifier()));


            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isCC) {
                    phase3setup = false;
                    StartAttackTransition transition = new StartAttackTransition(shooter.getIdentifier(), actionId, wargear.getId());
                    ((GameActivity) getActivity()).sendToServer(transition);
                    BusProvider.getInstance().post(
                            new TargetCharacterSelectionStateEndEvent());
                } else {
                    selectionStateRangeShort = false;
                    selectionStateRangeMedium = false;
                    selectionStateRangeLong = false;

                    phase3setup = false;
                    rangeShort = false;
                    rangeMedium = false;
                    rangeLong = false;

                    for (OneTarget target : targets) {
                        target.status = null;
                    }

                    sendChangesToServer();
                }


            }
        });


        return v;
    }

    private void refreshPhase1State() {
        phase1DoneButton.setEnabled(this.selectedWeapon != -1);
    }


    private void refreshPhase2_5State() {

        if (selectionStateRangeShort) {
            shortButton.setSelected(true);
            mediumButton.setSelected(false);
            longButton.setSelected(false);

        } else if (selectionStateRangeMedium) {
            shortButton.setSelected(false);
            mediumButton.setSelected(true);
            longButton.setSelected(false);

        } else if (selectionStateRangeLong) {
            shortButton.setSelected(false);
            mediumButton.setSelected(false);
            longButton.setSelected(true);
        } else {
            shortButton.setSelected(false);
            mediumButton.setSelected(false);
            longButton.setSelected(false);
        }


        shootButton2_5.setEnabled(selectionStateRangeShort || selectionStateRangeMedium || selectionStateRangeLong);


        rangeShooterContainer.removeAllViews();

        View shooterView;
        if (gamestate.findPlayerByCharacterIdentifier(shooter.getIdentifier()).isMe()) {
            shooterView = inflater.inflate(R.layout.attack_friendly_attacker, characterCardContainer, false);

        } else {
            shooterView = inflater.inflate(R.layout.attack_enemy_attacker, characterCardContainer, false);

        }

        ((TextView) shooterView.findViewById(R.id.target_name)).setText(shooter.getName());

        explanationsContainer = ((ViewGroup) shooterView.findViewById(R.id.explanations));


        Picasso.with(this.getActivity())
                .load(shooter.getCardPictureUri())
                .into((ImageView) shooterView.findViewById(R.id.portrait));


        ((TextView) shooterView.findViewById(R.id.weapon_name)).setText(LookupHelper.getInstance().getWargearFor(wargear.getId()).getName());
        Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(LookupHelper.getInstance().getWargearFor(wargear.getId()).getCategory());

        if (resource != null) {
            ((ImageView) shooterView.findViewById(R.id.weapon_image)).setImageResource(resource);
        } else {
            shooterView.findViewById(R.id.weapon_image).setVisibility(View.INVISIBLE);
        }


        rangeShooterContainer.addView(shooterView);


        rangeTargetsContainer.removeAllViews();

        NextAttackState attackState = gamestate.getPhase().getNextAttackState();
        for (String target : attackState.getTargets()) {

            CharacterState character = gamestate.findCharacterByIdentifier(target.substring(0, target.lastIndexOf(":")));


            if (character == null) {
                if (target.startsWith("" +
                        "" +
                        "")) {

                    int zombieType = Integer.parseInt(target.substring("ZOMBIE_".length(), "ZOMBIE_".length() + 1));
                    character = new Zombie(zombieType);

                } else {
                    //fallback
                    character = new Zombie();
                }
            }


            final View oneTarget = inflater.inflate(R.layout.one_selected_enemy_target_large,
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

            rangeTargetsContainer.addView(oneTarget);
        }


    }

    private void refreshPhase3State() {


        boolean isReady = true;

        if (isCC) {
            rangeText.setVisibility(View.GONE);
        } else {
            rangeText.setVisibility(View.VISIBLE);
            if (rangeShort) {
                rangeText.setText("Shooting at Short Range (0'' - 10'')");
            } else if (rangeMedium) {
                rangeText.setText("Shooting at Medium Range (10'' - 20'')");
            } else if (rangeLong) {
                rangeText.setText("Shooting at Long Range (20'' +)");
            }
        }


        infoTextView.setText("Attack!");

        for (OneTarget target : targets) {
            if (isReady && !target.isReady()) {
                isReady = false;
            }

            if (target.status == OneTarget.HitStatus.MISS) {
                target.misButton.setSelected(true);
                target.hitButton.setSelected(false);
                target.critButton.setSelected(false);
            } else if (target.status == OneTarget.HitStatus.HIT) {
                target.misButton.setSelected(false);
                target.hitButton.setSelected(true);
                target.critButton.setSelected(false);
            } else if (target.status == OneTarget.HitStatus.CRIT) {
                target.misButton.setSelected(false);
                target.hitButton.setSelected(false);
                target.critButton.setSelected(true);
            } else if (target.status == null) {
                target.misButton.setSelected(false);
                target.hitButton.setSelected(false);
                target.critButton.setSelected(false);
            }

            int defence;
            ArrayList<String> explanations;
            if (isCC) {
                defence = target.character.getCurrentDefensiveModifierCC(gamestate, target.lightCover, target.heavyCover);

                explanations = target.character.getDefensiveModifiersExplanationsCC(gamestate);

            } else {
                defence = target.character.getCurrentDefensiveModifierShooting(gamestate, target.lightCover, target.heavyCover);
                explanations = target.character.getDefensiveModifiersExplanationsShooting(gamestate);
            }

            if (target.lightCover) {
                target.lightcoverContainer.setSelected(true);
                target.heavyCoverContainer.setSelected(false);
                target.coverText.setText("light\ncover");
            } else if (target.heavyCover) {
                target.lightcoverContainer.setSelected(true);
                target.heavyCoverContainer.setSelected(true);
                target.coverText.setText("heavy\ncover");
            } else {
                target.lightcoverContainer.setSelected(false);
                target.heavyCoverContainer.setSelected(false);
                target.coverText.setText("no\ncover");
            }


            target.misButton.setEnabled(true);
            target.hitButton.setEnabled(true);
            target.critButton.setEnabled(true);


            target.explanations.removeAllViews();
            for (String explanation : explanations) {
                TextView tv = (TextView) inflater.inflate(R.layout.character_one_attack_explanation, target.explanations, false);
                tv.setText(explanation);
                target.explanations.addView(tv);

            }


            TextView tv = (TextView) inflater.inflate(R.layout.character_one_attack_explanation, target.explanations, false);
            tv.setText("Total defence: " + defence);
            target.explanations.addView(tv);

            int targetNumber = shooter.getTargetNumber(wargear, gamestate, rangeShort, rangeMedium, rangeLong, this.isAiming, defence);


            if (targetNumber >= 20) {

                target.misButton.setText("miss\n1-" + (targetNumber - 1));
                target.hitButton.setText("hit\n20");

                target.critButton.setEnabled(false);
                target.critButton.setText("crit\n-");

            } else {

                if (!gamestate.isGameModeAdvanced()) {
                    if (targetNumber == 2) {
                        target.misButton.setText("miss\n1");
                    } else {
                        target.misButton.setText("miss\n1-" + (targetNumber - 1));
                    }

                    if (targetNumber == 20) {
                        target.hitButton.setText("hit\n" + "20");
                    } else {
                        target.hitButton.setText("hit\n" + targetNumber + "-20");
                    }


                    target.critButton.setEnabled(false);
                    target.critButton.setText("crit\n-");
                } else {

                    if (targetNumber == 2) {
                        target.misButton.setText("miss\n1");
                    } else {
                        target.misButton.setText("miss\n1-" + (targetNumber - 1));
                    }


                    if (targetNumber == 19) {
                        target.hitButton.setText("hit\n19");
                    } else {
                        target.hitButton.setText("hit\n" + targetNumber + "-19");
                    }


                    target.critButton.setText("crit\n20");
                    target.critButton.setEnabled(true);
                }


            }

            if (!isReady) {
                infoTextView.setText("Roll the dice!");
            }


        }

        shootButton.setEnabled(isReady);


        NextAttackState attackState = gamestate.getPhase().getNextAttackState();
        if (attackState != null && attackState.isPlayerReady(gamestate.getMe().getIdentifier())) {
            shootButton.setSelected(true);

            infoTextView.setText("Waiting for other players to press ready");

        } else {
            shootButton.setSelected(false);
        }
        refreshShooterExplanations();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gamestate = ((BaseGameActivity) getActivity()).getGame();
        NextAttackState attackState = gamestate.getPhase().getNextAttackState();
        if (attackState != null) {
            this.actionId = attackState.getActionId();
            meAttacking = attackState.isMeAttacking(gamestate);
            shooter = gamestate.findCharacterByIdentifier(attackState.getCharacterAttackingId());

            loadStateFromMetadata(attackState);
        }
    }


    private boolean phase2FragmentCreated = false;

    private void setupPhase2() {

        if (!phase2FragmentCreated) {
            NextAttackState attackState = gamestate.getPhase().getNextAttackState();

            ActionsShootingCCSelectTargetFragment fragment = ActionsShootingCCSelectTargetFragment.newInstance(attackState.getWargearId(), shooter.getIdentifier(), attackState.getActionId());
            getChildFragmentManager().beginTransaction().replace(R.id.target_selection_container, fragment).commit();

            phase2FragmentCreated = true;
        }
    }

    private void setupPhase1() {

        selectedWeapon = -1;
        phase2FragmentCreated = false;
        NextAttackState attackState = gamestate.getPhase().getNextAttackState();


        boolean isRangedAttack = attackState.getActionId() == Action.ACTION_ID_SHOOTING || attackState.getActionId() == Action.ACTION_ID_AIM_AND_SHOOTING;
        boolean isCCAttack = attackState.getActionId() == Action.ACTION_ID_CC;

        Integer lastWg = null;
        weaponsTabs.removeAllViews();
        List<Integer> wargear = shooter.getWargear();
        LinkedList<Integer> weaponsForAdapter = new LinkedList<>();
        int index = 0;
        final LinkedList<View> tabs = new LinkedList<>();
        for (final Integer wargear2 : wargear) {

            if (isRangedAttack && (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                    || !LookupHelper.getInstance().getWargearFor(wargear2).isTypeRanged())) {
                continue;
            }

            if (isCCAttack && (!(LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearOffensive)
                    || !LookupHelper.getInstance().getWargearFor(wargear2).isTypeCC())) {
                continue;
            }


            if (shooter.isWargearSkillRequirementsMet(
                    LookupHelper.getInstance().getWargearFor(wargear2))) {


                if (lastWg == null || LookupHelper.getInstance().getWargearFor(lastWg).getWeaponId() != LookupHelper.getInstance().getWargearFor(wargear2).getWeaponId()) {

                    lastWg = wargear2;

                    weaponsForAdapter.add(LookupHelper.getInstance().getWargearFor(lastWg).getWeaponId());


                    final View weaponTab = getActivity().getLayoutInflater().inflate(
                            R.layout.shooting_ui_one_weapon_tab, weaponsTabs, false);


                    ((TextView) weaponTab.findViewById(R.id.weapon_name)).setText(LookupHelper.getInstance().getWargearFor(wargear2).getName());
                    Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(LookupHelper.getInstance().getWargearFor(wargear2).getCategory());

                    if (resource != null) {
                        ((ImageView) weaponTab.findViewById(R.id.weapon_image)).setImageResource(resource);
                    } else {
                        weaponTab.findViewById(R.id.weapon_image).setVisibility(View.INVISIBLE);
                    }
                    tabs.add(weaponTab);

                    final int pageToSelect = index;
                    weaponTab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            modesPager.setCurrentItem(pageToSelect);
                        }
                    });

                    weaponsTabs.addView(weaponTab);
                    ++index;
                }
            }

        }

        modesPager.setAdapter(new ScreenSlidePagerAdapter(getChildFragmentManager(), weaponsForAdapter));
        modesPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                if (position + 1 < tabs.size()) {
                    float scale = 1 + (0.2f * positionOffset);
                    View selectedTab = tabs.get(position + 1);

                    selectedTab.setScaleX(scale);
                    selectedTab.setScaleY(scale);

                    selectedTab.setAlpha(0.5f + (0.5f * (positionOffset)));
                }

                if (position >= 0) {
                    float scale = 1 + (0.2f * (1 - positionOffset));
                    View previousTab = tabs.get(position);

                    previousTab.setScaleX(scale);
                    previousTab.setScaleY(scale);

                    previousTab.setAlpha(0.5f + (0.5f * (1 - positionOffset)));
                }

            }

            @Override
            public void onPageSelected(int position) {


                if (position + 1 < tabs.size()) {
                    float scale = 1 + (0.2f * 1);
                    View selectedTab = tabs.get(position + 1);

                    selectedTab.setScaleX(scale);
                    selectedTab.setScaleY(scale);

                    selectedTab.setAlpha(0.5f + (0.5f * (1)));
                }

                if (position >= 0) {
                    float scale = 1 + (0.2f * (1 - 1));
                    View previousTab = tabs.get(position);

                    previousTab.setScaleX(scale);
                    previousTab.setScaleY(scale);

                    previousTab.setAlpha(0.5f + (0.5f * (1 - 1)));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        modesPager.setCurrentItem(0);
        refreshPhase1State();
    }

    private void setupPhase3() {

        if (phase3setup) {
            return;
        }

        if (!meAttacking) {
            cancelButton.setEnabled(false);
            cancelButton.setVisibility(View.INVISIBLE);
            shootButton.setBackgroundResource(R.drawable.solid_blue_button_background);
        }

        waitingForEnemyView.setVisibility(View.GONE);

        targetsContainer.removeAllViews();
        this.targets.clear();

        phase3setup = true;


        NextAttackState attackState = gamestate.getPhase().getNextAttackState();

        View view;
        if (gamestate.findPlayerByCharacterIdentifier(shooter.getIdentifier()).isMe()) {
            view = inflater.inflate(R.layout.attack_friendly_attacker, characterCardContainer, false);

        } else {
            view = inflater.inflate(R.layout.attack_enemy_attacker, characterCardContainer, false);

        }

        ((TextView) view.findViewById(R.id.target_name)).setText(shooter.getName());

        explanationsContainer = ((ViewGroup) view.findViewById(R.id.explanations));


        Picasso.with(this.getActivity())
                .load(shooter.getCardPictureUri())
                .into((ImageView) view.findViewById(R.id.portrait));


        ((TextView) view.findViewById(R.id.weapon_name)).setText(LookupHelper.getInstance().getWargearFor(wargear.getId()).getName());
        Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(LookupHelper.getInstance().getWargearFor(wargear.getId()).getCategory());

        if (resource != null) {
            ((ImageView) view.findViewById(R.id.weapon_image)).setImageResource(resource);
        } else {
            view.findViewById(R.id.weapon_image).setVisibility(View.INVISIBLE);
        }

        characterCardContainer.addView(view);
        refreshShooterExplanations();

        HashMap<String, OneTarget> mainTargetContainers = new HashMap<>();
        int index = 0;
        for (String target : attackState.getTargets()) {
            final OneTarget oneTarget = new OneTarget();

            boolean isMyCharacter = false;

            final CharacterState character = gamestate.findCharacterByIdentifier(target.substring(0, target.lastIndexOf(":")));
            oneTarget.character = character;

            ViewGroup addToThis = targetsContainer;

            OneTarget mainTarget = null;
            if (character != null && character.getIdentifier() != null) {
                mainTarget = mainTargetContainers.get(character.getIdentifier());
                if (mainTarget != null) {
                    addToThis = mainTarget.furtherHitsContainer;
                }
            }


            oneTarget.idWithIndex = target;
            if (oneTarget.character == null) {
                if (target.startsWith("" +
                        "" +
                        "")) {

                    int zombieType = Integer.parseInt(target.substring("ZOMBIE_".length(), "ZOMBIE_".length() + 1));
                    oneTarget.character = new Zombie(zombieType);
                    oneTarget.character.setIdentifier(target);
                } else {
                    //fallback
                    oneTarget.character = new Zombie();
                    oneTarget.character.setIdentifier(target);
                }


            } else {
                isMyCharacter = gamestate.findPlayerByCharacterIdentifier(oneTarget.character.getIdentifier()).isMe();
            }


            final View oneHitView;

            if (isMyCharacter) {
                oneHitView = getActivity().getLayoutInflater().inflate(
                        R.layout.attack_one_target_friendly, addToThis, false);
            } else {
                oneHitView = getActivity().getLayoutInflater().inflate(
                        R.layout.attack_one_target, addToThis, false);
            }


            oneTarget.explanations = (ViewGroup) oneHitView.findViewById(R.id.explanations);


            oneTarget.misButton = (TextView) oneHitView.findViewById(R.id.miss_button);
            oneTarget.hitButton = (TextView) oneHitView.findViewById(R.id.hit_button);
            oneTarget.critButton = (TextView) oneHitView.findViewById(R.id.crit_button);

            oneTarget.furtherHitsContainer = (ViewGroup) oneHitView.findViewById(R.id.further_hits_container);


            Log.e("TMP", "oneTarget.furtherHitsContainer set " + oneTarget.furtherHitsContainer);

            if (character != null && character.getIdentifier() != null) {
                if (mainTarget == null) {
                    mainTargetContainers.put(character.getIdentifier(), oneTarget);

                    Log.e("TMP", "setting main");
                }
            }
//            oneTarget.attackOfOpportunityButton = (Button) oneHitView.findViewById(R.id.attack_of_opportunity_button);
//            oneTarget.lightCoverButton = (Button) oneHitView.findViewById(R.id.light_cover_button);
//            oneTarget.heavyCoverButton = (Button) oneHitView.findViewById(R.id.heavy_cover_button);

            oneTarget.lightcoverContainer = oneHitView.findViewById(R.id.light_cover_indicator);
            oneTarget.heavyCoverContainer = oneHitView.findViewById(R.id.heavy_cover_indicator);
            oneTarget.coverText = (TextView) oneHitView.findViewById(R.id.cover_text);
            oneTarget.coverContainer = oneHitView.findViewById(R.id.cover_container);


            oneTarget.misButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oneTarget.status = OneTarget.HitStatus.MISS;
                    refreshPhase3State();
                    sendChangesToServer();
                }
            });


            oneTarget.hitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oneTarget.status = OneTarget.HitStatus.HIT;
                    refreshPhase3State();
                    sendChangesToServer();
                }
            });

            oneTarget.critButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oneTarget.status = OneTarget.HitStatus.CRIT;
                    refreshPhase3State();
                    sendChangesToServer();
                }
            });


//            oneTarget.attackOfOpportunityButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    oneTarget.attackOfOpportunity = !oneTarget.attackOfOpportunity;
//                    refreshPhase3State();
//                    sendChangesToServer();
//                }
//            });


            oneTarget.coverContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (character == null || character.getIdentifier() == null) {

                        if (oneTarget.lightCover == true) {
                            oneTarget.heavyCover = true;
                            oneTarget.lightCover = false;
                        } else if (oneTarget.heavyCover == true) {
                            oneTarget.heavyCover = false;
                            oneTarget.lightCover = false;
                        } else {
                            oneTarget.heavyCover = false;
                            oneTarget.lightCover = true;
                        }

                    } else {
                        for (OneTarget target : targets) {
                            if (character.getIdentifier().equals(target.character.getIdentifier())) {

                                if (target.lightCover == true) {
                                    target.heavyCover = true;
                                    target.lightCover = false;
                                } else if (target.heavyCover == true) {
                                    target.heavyCover = false;
                                    target.lightCover = false;
                                } else {
                                    target.heavyCover = false;
                                    target.lightCover = true;
                                }
                            }


                        }


                    }


                    refreshPhase3State();
                    sendChangesToServer();
                }
            });


            ((TextView) oneHitView.findViewById(R.id.target_name)).setText(oneTarget.character.getName());

            if (index >= DiceUtils.getDiceColors().size()) {
                index = 0;
            }

            oneHitView.findViewById(R.id.dice_color).setBackgroundColor(DiceUtils.getDiceColors().get(index));


            Picasso.with(this.getActivity())
                    .load(oneTarget.character.getCardPictureUri())
                    .into((ImageView) oneHitView.findViewById(R.id.target_portrait));

            ++index;


            if (mainTarget != null) {
                oneHitView.findViewById(R.id.top_part).setVisibility(View.GONE);
            }


            addToThis.addView(oneHitView);

            this.targets.add(oneTarget);
            if (mainTarget == null) {
                getActivity().getLayoutInflater().inflate(
                        R.layout.target_spacer, addToThis, true);
            }
        }


        if (isCC) {
            rangeShort = true;
        }

        refreshPhase3State();

    }


    private void refreshShooterExplanations() {
        NextAttackState attackState = gamestate.getPhase().getNextAttackState();
        explanationsContainer.removeAllViews();

        ArrayList<String> explanations = shooter.getTargetNumberExplanations((WargearOffensive) WargearManager.getInstance().getWargearById(attackState.getWargearId()), gamestate);

        boolean isAiming = attackState.getActionId() == Action.ACTION_ID_AIM_AND_SHOOTING;
        if (isAiming) {
            explanations.add("Aim +" + GameConstants.AIM_MODIFIER);
        }

        for (String string : explanations) {
            TextView tv = (TextView) inflater.inflate(R.layout.character_one_attack_explanation, explanationsContainer, false);
            tv.setText(string);
            explanationsContainer.addView(tv);
        }
        TextView tv = (TextView) inflater.inflate(R.layout.character_one_attack_explanation, explanationsContainer, false);
        if (rangeShort) {
            tv.setText("Weapon modifier short range " + (wargear.getModifierShort() > 0 ? "+" : "") + wargear.getModifierShort());
        } else if (rangeMedium) {
            tv.setText("Weapon modifier medium range " + (wargear.getModifierMid() > 0 ? "+" : "") + wargear.getModifierMid());
        } else if (rangeLong) {
            tv.setText("Weapon modifier long range " + (wargear.getModifierLong() > 0 ? "+" : "") + wargear.getModifierLong());
        }
        explanationsContainer.addView(tv);


    }


    private void sendChangesToServer() {
        HashMap<String, Boolean> metadata = new HashMap<>();
        if (rangeShort) {
            metadata.put(NextAttackState.META_KEY_SHORT_RANGE, true);
        }

        if (rangeMedium) {
            metadata.put(NextAttackState.META_KEY_MID_RANGE, true);
        }

        if (rangeLong) {
            metadata.put(NextAttackState.META_KEY_LONG_RANGE, true);
        }

        HashMap<String, HashMap<String, Boolean>> characterMetadata = new HashMap<String, HashMap<String, Boolean>>();
        for (OneTarget oneTarget : this.targets) {
            HashMap<String, Boolean> oneCharacterMetadata = new HashMap<String, Boolean>();
            if (oneTarget.lightCover) {
                oneCharacterMetadata.put(NextAttackState.META_KEY_SOFT_COVER, true);
            }
            if (oneTarget.heavyCover) {
                oneCharacterMetadata.put(NextAttackState.META_KEY_HARD_COVER, true);
            }
            if (oneTarget.attackOfOpportunity) {
                oneCharacterMetadata.put(NextAttackState.META_KEY_ATTACK_OF_OPPORTUNITY, true);
            }

            if (oneTarget.status == OneTarget.HitStatus.MISS) {
                oneCharacterMetadata.put(NextAttackState.META_KEY_MISS, true);
            } else if (oneTarget.status == OneTarget.HitStatus.HIT) {
                oneCharacterMetadata.put(NextAttackState.META_KEY_HIT, true);
            } else if (oneTarget.status == OneTarget.HitStatus.CRIT) {
                oneCharacterMetadata.put(NextAttackState.META_KEY_CRIT, true);
            }


            characterMetadata.put(oneTarget.idWithIndex, oneCharacterMetadata);
        }

        ((BaseGameActivity) getActivity()).sendToServer(new ChangeAttackMetadataTransition(characterMetadata, metadata, gamestate.getMe().getIdentifier()));

    }


    private void loadStateFromMetadata(NextAttackState attackState) {

        wargear = (WargearOffensive) WargearManager.getInstance().getWargearById(attackState.getWargearId());
        if (wargear != null) {
            isCC = "CC".equals(wargear.getType());
        }


        HashMap<String, Boolean> metadata = attackState.getMetadata();
        Boolean shortRange = metadata.get(NextAttackState.META_KEY_SHORT_RANGE);
        Boolean midRange = metadata.get(NextAttackState.META_KEY_MID_RANGE);
        Boolean longRange = metadata.get(NextAttackState.META_KEY_LONG_RANGE);


        if (shortRange != null && shortRange) {
            rangeShort = true;
            rangeMedium = false;
            rangeLong = false;

        } else if (midRange != null && midRange) {
            rangeShort = false;
            rangeMedium = true;
            rangeLong = false;

        } else if (longRange != null && longRange) {
            rangeShort = false;
            rangeMedium = false;
            rangeLong = true;

        } else {
            rangeShort = false;
            rangeMedium = false;
            rangeLong = false;
        }


        this.isAiming = attackState.getActionId() == Action.ACTION_ID_AIM_AND_SHOOTING;

        if (attackState.getWargearId() == -1) {

            if (!meAttacking) {
                waitingForEnemyView.setVisibility(View.VISIBLE);

                phase1View.setVisibility(View.GONE);
                phase2View.setVisibility(View.GONE);
                phase2_5_View.setVisibility(View.GONE);
                phase3View.setVisibility(View.GONE);
            } else {
                waitingForEnemyView.setVisibility(View.GONE);
                phase1View.setVisibility(View.VISIBLE);
                phase2View.setVisibility(View.GONE);
                phase2_5_View.setVisibility(View.GONE);
                phase3View.setVisibility(View.GONE);
                setupPhase1();
            }


        } else if (attackState.getTargets().isEmpty()) {


            phase3setup = false;

            if (!meAttacking) {
                waitingForEnemyView.setVisibility(View.VISIBLE);
                phase1View.setVisibility(View.GONE);
                phase2View.setVisibility(View.GONE);
                phase2_5_View.setVisibility(View.GONE);
                phase3View.setVisibility(View.GONE);
            } else {
                setupPhase2();
                phase1View.setVisibility(View.GONE);
                phase2View.setVisibility(View.VISIBLE);
                phase2_5_View.setVisibility(View.GONE);
                phase3View.setVisibility(View.GONE);
                waitingForEnemyView.setVisibility(View.GONE);
            }


        } else if (isCC || rangeShort || rangeMedium || rangeLong) {

            phase1View.setVisibility(View.GONE);
            phase2View.setVisibility(View.GONE);
            phase2_5_View.setVisibility(View.GONE);
            phase3View.setVisibility(View.VISIBLE);

            waitingForEnemyView.setVisibility(View.GONE);

            setupPhase3();


            HashMap<String, HashMap<String, Boolean>> characterMetadata = attackState.getCharacterMetadata();


            for (String characterIdWithIndex : attackState.getTargets()) {

                OneTarget target = null;
                for (OneTarget oneTarget : this.targets) {
                    if (characterIdWithIndex.equals(oneTarget.idWithIndex)) {
                        target = oneTarget;
                        break;
                    }
                }

                if (target != null) {
                    HashMap<String, Boolean> oneCharacterMetadata = characterMetadata.get(characterIdWithIndex);

                    if (oneCharacterMetadata == null) {
                        Log.e("Attack UI", "failed to load character metadata for id: " + characterIdWithIndex);
                        continue;
                    }

                    Boolean softCover = oneCharacterMetadata.get(NextAttackState.META_KEY_SOFT_COVER);
                    Boolean hardCover = oneCharacterMetadata.get(NextAttackState.META_KEY_HARD_COVER);

                    Boolean aoo = oneCharacterMetadata.get(NextAttackState.META_KEY_ATTACK_OF_OPPORTUNITY);


                    target.lightCover = softCover != null && softCover;

                    target.heavyCover = hardCover != null && hardCover;


                    target.attackOfOpportunity = aoo != null && aoo;


                    Boolean miss = oneCharacterMetadata.get(NextAttackState.META_KEY_MISS);
                    Boolean hit = oneCharacterMetadata.get(NextAttackState.META_KEY_HIT);
                    Boolean crit = oneCharacterMetadata.get(NextAttackState.META_KEY_CRIT);


                    if (miss != null && miss) {
                        target.status = OneTarget.HitStatus.MISS;
                    } else if (hit != null && hit) {
                        target.status = OneTarget.HitStatus.HIT;
                    } else if (crit != null && crit) {
                        target.status = OneTarget.HitStatus.CRIT;
                    }

                }
            }


            refreshPhase3State();

        } else {

            //phase 2_5

            if (!meAttacking) {
                waitingForEnemyView.setVisibility(View.VISIBLE);
                phase1View.setVisibility(View.GONE);
                phase2View.setVisibility(View.GONE);
                phase2_5_View.setVisibility(View.GONE);
                phase3View.setVisibility(View.GONE);
            } else {
                setupPhase2();
                phase1View.setVisibility(View.GONE);
                phase2View.setVisibility(View.GONE);
                phase2_5_View.setVisibility(View.VISIBLE);
                phase3View.setVisibility(View.GONE);
                waitingForEnemyView.setVisibility(View.GONE);
            }
            refreshPhase2_5State();
        }


    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {

        NextAttackState attackState = gamestate.getPhase().getNextAttackState();
        if (attackState != null) {
            loadStateFromMetadata(attackState);
        }
    }

    private static class OneTarget {

        public ViewGroup explanations;
        public View lightcoverContainer;
        public View heavyCoverContainer;
        public View coverContainer;
        public TextView coverText;


        public ViewGroup furtherHitsContainer;


        String idWithIndex;
        CharacterState character;
        TextView misButton;
        TextView hitButton;
        TextView critButton;
        HitStatus status;
        boolean lightCover;
        boolean heavyCover;
        boolean attackOfOpportunity;

        boolean isReady() {
            return status != null;
        }

        enum HitStatus {MISS, HIT, CRIT}

    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private LinkedList<Integer> weapons = new LinkedList<>();


        public ScreenSlidePagerAdapter(FragmentManager manager, LinkedList<Integer> weapons) {
            super(manager);
            this.weapons = weapons;

        }

        @Override
        public Fragment getItem(int position) {

            return AttackOverlayWeaponFragment.newInstance(weapons.get(position));
        }

        @Override
        public int getCount() {
            return weapons.size();
        }
    }

    @Subscribe
    public void onOpponentIsWaitingForYouEvent(OpponentIsWaitingForYouEvent event) {
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText("Waiting for you...");

        final ViewPropertyAnimator animator = shootButton.animate();
        animator.translationX(UIUtils.convertDpToPixel(-50, getActivity())).setInterpolator(new CycleInterpolator(5)).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                shootButton.animate().translationX(0);

                animator.setListener(null);
            }
        });
    }


    @Subscribe
    public void onWeaponModeSelectedEvent(AttackOverlayWeaponFragment.WeaponModeSelectedEvent event) {
        selectedWeapon = event.getWargearId();
        refreshPhase1State();

    }


}
