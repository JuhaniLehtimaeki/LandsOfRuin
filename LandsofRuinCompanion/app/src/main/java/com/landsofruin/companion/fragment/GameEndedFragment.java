package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.cards.FriendlyCharacterView;
import com.landsofruin.companion.cards.FriendlySquadView;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.map.MapView;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.AttackLogItem;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.Zombie;
import com.landsofruin.companion.state.gameruleobjects.characterdata.UnresolvedHit;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.damage.DamageLine;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.BuildConfig;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.damage.data.DamageDataManager;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Fragment showing the game ended state
 */
public class GameEndedFragment extends Fragment {

    private BaseGameActivity activity;
    private ViewGroup myTeamContainer;
    private View charaterDetailView;
    private MapView mapview;
    private ViewGroup actionLog;
    private LayoutInflater inflater;
    private TextView characterCcAttacks;
    private TextView characterShootingAttacks;
    private TextView characterTotalAttacks;
    private TextView teamCcAttacks;
    private TextView teamShootingAttacks;
    private TextView teamTotalAttacks;
    private Scenario scenario;
    private TextView characterTitle;
    private ViewGroup characterContainer;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.end_game_fragment,
                parent, false);

        ((TextView) view.findViewById(R.id.version_info)).setText(PlayerAccount
                .getAppVersionTitle() + (BuildConfig.DEBUG ? " (DEBUG)" : ""));


        ((TextView) view.findViewById(R.id.more_info)).setText("Zombies on table at the game end: " + this.activity.getGame().getWorld().getCurrentAmountOfZombies());

        characterContainer = (ViewGroup) view.findViewById(R.id.character_fragment_container);


        view.findViewById(R.id.bg_image).animate().scaleX(1.f).scaleY(1.f).setDuration(25000);
        myTeamContainer = (ViewGroup) view.findViewById(R.id.my_team);


        charaterDetailView = view.findViewById(R.id.character_detail_view);
        view.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                charaterDetailView.setVisibility(View.GONE);
            }
        });

        mapview = ((MapView) view.findViewById(R.id.map_game_end));
        actionLog = (ViewGroup) view.findViewById(R.id.action_log);


        MapState map = ((GameActivity) getActivity()).getGame().getMap();
        mapview.setGame(((GameActivity) getActivity()).getGame());
        mapview.setMap(map);
        if (map.getBackgroundImageURL() != null) {
            Picasso.with(getActivity()).load(map.getBackgroundImageURL()).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mapview.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        characterCcAttacks = ((TextView) view.findViewById(R.id.character_cc_attacks));
        characterShootingAttacks = ((TextView) view.findViewById(R.id.character_shooting_attacks));
        characterTotalAttacks = ((TextView) view.findViewById(R.id.character_total_attacks));
        characterTitle = ((TextView) view.findViewById(R.id.character_title));

        teamCcAttacks = ((TextView) view.findViewById(R.id.cc_attacks));
        teamShootingAttacks = ((TextView) view.findViewById(R.id.shooting_attacks));
        teamTotalAttacks = ((TextView) view.findViewById(R.id.total_attacks));


        scenario = LookupHelper.getInstance().getScenarioFor(this.activity.getGame().getScenario());
        if (scenario.getScenarioImageUri() != null) {
            Picasso.with(getActivity())
                    .load(scenario.getScenarioImageUri())
                    .into(((ImageView) view.findViewById(R.id.bg_image)));
        }

        return view;
    }


    private int getCharacterShotsFired(CharacterState characterState) {
        int ret = 0;
        for (AttackLogItem item : characterState.getBattleLogState().getAttackLogItems()) {
            Wargear warger = WargearManager.getInstance().getWargearById(item.getWgId());
            if (!"CC".equalsIgnoreCase(warger
                    .getType())) {
                ++ret;
            }

        }
        return ret;
    }


    private int getCharacterCCAttacks(CharacterState characterState) {
        int ret = 0;
        for (AttackLogItem item : characterState.getBattleLogState().getAttackLogItems()) {
            Wargear warger = WargearManager.getInstance().getWargearById(item.getWgId());
            if ("CC".equalsIgnoreCase(warger
                    .getType())) {
                ++ret;
            }

        }
        return ret;
    }


    private int getCharacterShotsHit(CharacterState characterState) {
        int ret = 0;
        for (AttackLogItem item : characterState.getBattleLogState().getAttackLogItems()) {
            Wargear warger = WargearManager.getInstance().getWargearById(item.getWgId());
            if (!"CC".equalsIgnoreCase(warger
                    .getType())) {
                if (item.isHit()) {
                    ++ret;
                }
            }


        }
        return ret;
    }

    private int getCharacterCCHit(CharacterState characterState) {
        int ret = 0;
        for (AttackLogItem item : characterState.getBattleLogState().getAttackLogItems()) {
            Wargear warger = WargearManager.getInstance().getWargearById(item.getWgId());
            if ("CC".equalsIgnoreCase(warger
                    .getType())) {
                if (item.isHit()) {
                    ++ret;
                }
            }


        }
        return ret;
    }

    private void charterDetailsSelected(CharacterState characterState) {

        GameState gameState = ((GameActivity) getActivity()).getGame();

        View characterView = null;
        if (characterState instanceof CharacterStateSquad) {
            characterView = new FriendlySquadView(getContext());
            ((FriendlySquadView) characterView).setCharacter((CharacterStateSquad) characterState);
        } else if (characterState instanceof CharacterStateHero) {
            characterView = new FriendlyCharacterView(getContext());
            ((FriendlyCharacterView) characterView).setCharacter((CharacterStateHero) characterState);
        }

        characterContainer.removeAllViews();
        if (characterView != null) {
            characterContainer.addView(characterView);
        }


        LinkedList<String> turnSequence = new LinkedList<>();
        turnSequence.addAll(characterState.getBattleLogState().getMovementHistory().keySet());

        Collections.sort(turnSequence);
        LinkedList<PointState> movementHistory = new LinkedList<>();
        for (String turn : turnSequence) {
            movementHistory.add(characterState.getBattleLogState().getMovementHistory().get(turn));
        }
        mapview.setMovementHistoryPath(movementHistory);
        mapview.invalidate();

        characterTitle.setText(characterState.getName() + " battle overview");

        int ccAttacks = getCharacterCCAttacks(characterState);
        int ccHits = getCharacterCCHit(characterState);
        int shootAttacks = getCharacterShotsFired(characterState);
        int shootHits = getCharacterShotsHit(characterState);


        String ccPercentage = MessageFormat.format("{0,number,#.##%}", (float) ccHits / (float) ccAttacks);
        characterCcAttacks.setText("CC: " + ccHits + "/" + ccAttacks + " - " + ccPercentage);

        String shootPercentage = MessageFormat.format("{0,number,#.##%}", (float) shootHits / (float) shootAttacks);
        characterShootingAttacks.setText("Shooting: " + shootHits + "/" + shootAttacks + " - " + shootPercentage);


        String totalPercentage = MessageFormat.format("{0,number,#.##%}", (float) (shootHits + ccHits) / (float) (shootAttacks + ccAttacks));
        characterTotalAttacks.setText("Total: " + (shootHits + ccHits) + "/" + (shootAttacks + ccAttacks) + " - " + totalPercentage);

        actionLog.removeAllViews();

        for (AttackLogItem item : characterState.getBattleLogState().getAttackLogItems()) {
            View view = inflater.inflate(R.layout.end_game_one_attack_log_item, actionLog, false);

            CharacterState target = gameState.findCharacterByIdentifier(item.getTargetCharacter());
            if (target == null) {
                target = new Zombie();
            }

            Picasso.with(this.getActivity())
                    .load(target.getProfilePictureUri())
                    .into((ImageView) view.findViewById(R.id.target_portrait));

            Wargear wargear = WargearManager.getInstance().getWargearById(item.getWgId());

            if (wargear instanceof WargearOffensive) {

                WargearOffensive offensiveWargear = (WargearOffensive) wargear;
                if (offensiveWargear.getImageUri() != null && !offensiveWargear.getImageUri().isEmpty()) {
                    Picasso.with(getActivity()).load(offensiveWargear.getImageUri()).into(((ImageView) view.findViewById(R.id.weapon_image)));
                } else {

                    Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(offensiveWargear.getCategory());
                    if (resource != null) {
                        ((ImageView) view.findViewById(R.id.weapon_image)).setImageResource(resource);
                    } else {
                        view.findViewById(R.id.weapon_image).setVisibility(View.GONE);
                    }
                }
            } else {
                Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(wargear.getCategory());

                if (resource != null) {
                    ((ImageView) view.findViewById(R.id.weapon_image)).setImageResource(resource);
                }
            }


            if (item.isHit()) {
                DamageLine damage;
                if (!"CC".equalsIgnoreCase(wargear
                        .getType())) {
                    damage = DamageDataManager.getInstance().getCCDamageLine(item.getDamage());
                } else {
                    damage = DamageDataManager.getInstance().getShootingDamageLine(item.getDamage());
                }
                ((TextView) view.findViewById(R.id.result)).setText("Hit " + item.getDamage() + " - " + (damage == null ? "" : damage.getPrivateEffectText()));

                StringBuffer sb = new StringBuffer();
                if (item.getRange() == UnresolvedHit.RANGE_CC) {
                    sb.append("Close Combat");
                } else if (item.getRange() == UnresolvedHit.RANGE_SHORT) {
                    sb.append("Short Range");
                } else if (item.getRange() == UnresolvedHit.RANGE_MID) {
                    sb.append("Mid Range");
                } else if (item.getRange() == UnresolvedHit.RANGE_LONG) {
                    sb.append("Long Range");
                }

                if (item.isHardCover()) {
                    sb.append(", Heavy Cover");
                }
                if (item.isSoftCover()) {
                    sb.append(", Light Cover");
                }

                if (item.isAttackOfOpportunity()) {
                    sb.append(", Attack of Opportunity");
                }

                ((TextView) view.findViewById(R.id.conditions)).setText(sb.toString());

                ViewGroup effectIconContainer = (ViewGroup) view
                        .findViewById(R.id.effect_icon_container);
                if (damage != null) {
                    for (final int effectId : damage.getAddsEffects()) {


                        CharacterEffect newEffect = CharacterEffectFactory
                                .createCharacterEffect(effectId, gameState
                                        .getPhase().getGameTurn());

                        View oneIconView = getActivity().getLayoutInflater().inflate(
                                R.layout.one_effect, effectIconContainer, false);

                        final ImageView icon = (ImageView) oneIconView
                                .findViewById(R.id.effect_icon);
                        icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(newEffect.getIcon()));
                        effectIconContainer.addView(oneIconView);
                    }
                }
            } else {
                ((TextView) view.findViewById(R.id.result)).setText("Miss");
            }


            actionLog.addView(view);
        }


        charaterDetailView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
        if (activity.getGame().getPhase().getPrimaryPhase() == PrimaryPhase.GAME_END) {
            updateUI();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        GameState game = activity.getGame();
        if (game.getPhase().getPrimaryPhase() == PrimaryPhase.GAME_END) {
            updateUI();
        }

    }

    private void updateUI() {
        GameState game = activity.getGame();


        myTeamContainer.removeAllViews();

        int ccAttacks = 0;
        int ccHits = 0;
        int shootAttacks = 0;
        int shootHits = 0;
        for (final CharacterState characterState : game.getMe().getTeam().listAllTypesCharacters()) {
            final View oneCharacterView = this.inflater.inflate(
                    R.layout.end_game_one_character, myTeamContainer, false);

            ((TextView) oneCharacterView.findViewById(R.id.target_name)).setText(characterState.getName());
            Picasso.with(this.getActivity())
                    .load(characterState.getProfilePictureUri())
                    .into((ImageView) oneCharacterView.findViewById(R.id.target_portrait));


            ViewGroup effectIconContainer = (ViewGroup) oneCharacterView
                    .findViewById(R.id.effect_icon_container);

            for (final CharacterEffect characterEffect : characterState.getCharacterEffects()) {

                View oneIconView = getActivity().getLayoutInflater().inflate(
                        R.layout.one_effect, effectIconContainer, false);

                final ImageView icon = (ImageView) oneIconView
                        .findViewById(R.id.effect_icon);
                icon.setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(characterEffect.getIcon()));
                effectIconContainer.addView(oneIconView);
            }


            oneCharacterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    charterDetailsSelected(characterState);
                }
            });

            myTeamContainer.addView(oneCharacterView);

            ccAttacks += getCharacterCCAttacks(characterState);
            ccHits += getCharacterCCHit(characterState);
            shootAttacks += getCharacterShotsFired(characterState);
            shootHits += getCharacterShotsHit(characterState);
        }


        String ccPercentage = MessageFormat.format("{0,number,#.##%}", (float) ccHits / (float) ccAttacks);
        teamCcAttacks.setText("CC: " + ccHits + "/" + ccAttacks + " - " + ccPercentage);

        String shootPercentage = MessageFormat.format("{0,number,#.##%}", (float) shootHits / (float) shootAttacks);
        teamShootingAttacks.setText("Shooting: " + shootHits + "/" + shootAttacks + " - " + shootPercentage);


        String totalPercentage = MessageFormat.format("{0,number,#.##%}", (float) (shootHits + ccHits) / (float) (shootAttacks + ccAttacks));
        teamTotalAttacks.setText("Total: " + (shootHits + ccHits) + "/" + (shootAttacks + ccAttacks) + " - " + totalPercentage);

    }


}

