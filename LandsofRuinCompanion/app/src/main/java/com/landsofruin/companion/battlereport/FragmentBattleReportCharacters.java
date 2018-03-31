package com.landsofruin.companion.battlereport;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.state.AttackLogItem;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.battlereport.BattleReport;
import com.landsofruin.companion.state.battlereport.BattleReportPlayer;
import com.landsofruin.companion.state.gameruleobjects.characterdata.UnresolvedHit;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.damage.DamageLine;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.tribemanagement.TribeCharacter;
import com.landsofruin.companion.tribemanagement.UserAccount;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.damage.data.DamageDataManager;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.picasso.Picasso;


public class FragmentBattleReportCharacters extends Fragment {
    private static final String ARG_PARAM1 = "battleReportId";

    private String battleReportId;
    private BattleReport battlereport;
    private ValueEventListener battleReportValueListener;
    private DatabaseReference firebaseRef;
    private ViewGroup playersContainer;

    public FragmentBattleReportCharacters() {
    }

    public static FragmentBattleReportCharacters newInstance(String batteReportId) {
        FragmentBattleReportCharacters fragment = new FragmentBattleReportCharacters();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batteReportId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            battleReportId = getArguments().getString(ARG_PARAM1);
        }


        firebaseRef = FirebaseDatabase.getInstance().getReference();

        battleReportValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                battlereport = dataSnapshot.getValue(BattleReport.class);

                for (final BattleReportPlayer player : battlereport.getPlayers()) {
                    firebaseRef.child("user").child(player.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                            inflateGameInfo(userAccount, player);
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });
                }
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        firebaseRef.child("battlereports").child(battleReportId).addListenerForSingleValueEvent(battleReportValueListener);
    }


    private void inflateGameInfo(UserAccount userAccount, BattleReportPlayer player) {

        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_battle_report_characters_one_player, playersContainer, false);

        for (final TribeCharacter character : userAccount.getTribe().getCharacters().values()) {
            if (player.getCharacters().contains(character.getId())) {
                View characterView = inflater.inflate(R.layout.fragment_battle_report_characters_one_player_one_character, view, false);

                Picasso.with(getActivity()).load(character.getCardImageUrl()).into((ImageView) characterView.findViewById(R.id.character_image));

                ((TextView) characterView.findViewById(R.id.name)).setText(character.getName());


                final ViewGroup eventsContent = (ViewGroup) characterView.findViewById(R.id.event_content);


                ValueEventListener characterEventsValueListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }

                        LayoutInflater inflater = LayoutInflater.from(getActivity());

                        for (DataSnapshot action : dataSnapshot.getChildren()) {
                            AttackLogItem item = action.getValue(AttackLogItem.class);

                            View view = inflater.inflate(R.layout.end_game_one_attack_log_item, eventsContent, false);

                            Picasso.with(getActivity())
                                    .load(item.getTargetCharacterPortraitUrl())
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
                                                .createCharacterEffect(effectId, 0);

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


                            eventsContent.addView(view);
                        }


                    }


                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                };

                firebaseRef.child("battlereports").child(battleReportId).child("characterActions").child(character.getId()).addListenerForSingleValueEvent(characterEventsValueListener);


                view.addView(characterView);
            }
        }


        playersContainer.addView(view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle_report_characters, container, false);
        playersContainer = (ViewGroup) view.findViewById(R.id.players_container);

        return view;
    }

}
