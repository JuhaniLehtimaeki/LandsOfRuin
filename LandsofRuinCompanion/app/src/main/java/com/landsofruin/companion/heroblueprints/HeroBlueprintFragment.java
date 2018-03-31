package com.landsofruin.companion.heroblueprints;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.heroblueprint.HeroBlueprint;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.BR;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.skills.SkillsManager;
import com.landsofruin.gametracker.wargear.WargearManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeroBlueprintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeroBlueprintFragment extends Fragment {
    private static final String ARG_HERO_BLUEPRINT_ID = "param1";

    private String heroBlueprintId;
    private ViewDataBinding binding;
    private ValueEventListener valueListener;
    private HeroBlueprint heroBlueprint;
    private boolean isNew = false;
    private ViewGroup skillsContainer;
    private ViewGroup wargearContainer;
    private ViewGroup weaponsContainer;
    private DatabaseReference firebaseRef;


    public HeroBlueprintFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param heroBlueprintId Parameter 1.
     * @return A new instance of fragment CharacterTypeFragment.
     */
    public static HeroBlueprintFragment newInstance(String heroBlueprintId) {
        HeroBlueprintFragment fragment = new HeroBlueprintFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HERO_BLUEPRINT_ID, heroBlueprintId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            heroBlueprintId = getArguments().getString(ARG_HERO_BLUEPRINT_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hero_blueprint, container, false);
        final View view = binding.getRoot();


        firebaseRef = FirebaseDatabase.getInstance().getReference();

        view.findViewById(R.id.beta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    heroBlueprint.setStatus(HeroBlueprint.STATUS_TYPE_BETA);
                }
            }
        });


        view.findViewById(R.id.alpha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    heroBlueprint.setStatus(HeroBlueprint.STATUS_TYPE_ALPHA);
                }
            }
        });


        view.findViewById(R.id.production).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    heroBlueprint.setStatus(HeroBlueprint.STATUS_TYPE_PRODUCTION_READY);
                }
            }
        });


        if (heroBlueprintId == null) {
            isNew = true;
            heroBlueprint = new HeroBlueprint();
            binding.setVariable(BR.heroBlueprint, heroBlueprint);
        } else {
            isNew = false;
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    heroBlueprint = dataSnapshot.getValue(HeroBlueprint.class);

                    binding.setVariable(BR.heroBlueprint, heroBlueprint);

                }


                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            };

            firebaseRef.child("admin").child("heroblueprint").child(heroBlueprintId).addValueEventListener(valueListener);
        }

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                heroBlueprint.setName(((TextView) view.findViewById(R.id.name)).getText().toString());
                heroBlueprint.setCardImageUrl(((TextView) view.findViewById(R.id.card_pic_url)).getText().toString());
                heroBlueprint.setPortraitImageUrl(((TextView) view.findViewById(R.id.profile_pic_url)).getText().toString());


                if (isNew) {
                    DatabaseReference pushId = firebaseRef.child("admin").child("heroblueprint").push();
                    heroBlueprint.setId(pushId.getKey());
                    pushId.setValue(heroBlueprint);

                } else {
                    firebaseRef.child("admin").child("heroblueprint").child(heroBlueprintId).setValue(heroBlueprint);
                }


                getActivity().finish();

            }
        });

        skillsContainer = ((ViewGroup) view.findViewById(R.id.skills_container));
        wargearContainer = ((ViewGroup) view.findViewById(R.id.wargear_container));
        weaponsContainer = ((ViewGroup) view.findViewById(R.id.weapons_container));


        view.findViewById(R.id.edit_skills_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSkills();
            }
        });


        view.findViewById(R.id.edit_wargear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWargear();
            }
        });

        view.findViewById(R.id.edit_weapons_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWeapons();
            }
        });


        ViewGroup squadsView = (ViewGroup) view.findViewById(R.id.squads_container);
        if (!isNew) {
            fillInSquads(squadsView);
        }

        ViewGroup characterTypeView = (ViewGroup) view.findViewById(R.id.character_type_selection);
        fillInCharacterTypes(characterTypeView);

        return view;
    }

    private void fillInSquads(final ViewGroup squadsView) {
        firebaseRef.child("admin").child("heroblueprint").child(heroBlueprintId).addValueEventListener(valueListener);


        DatabaseReference ref = firebaseRef.child("admin").child("heroblueprint");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                squadsView.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                for (DataSnapshot action : dataSnapshot.getChildren()) {
                    final HeroBlueprint heroBlueprint_ = action.getValue(HeroBlueprint.class);


                    if (LookupHelper.getInstance().getCharacterTypeFor(heroBlueprint_.getCharacterType()).getType() != CharacterType.TYPE_HERO) {
                        View view = inflater.inflate(R.layout.hero_blueprint_one_squad, squadsView, false);
                        CheckBox radio = (CheckBox) view.findViewById(R.id.checkbox);

                        radio.setText(heroBlueprint_.getName());
                        if (heroBlueprint.getCanHaveSquads().contains(heroBlueprint_.getId())) {
                            radio.setChecked(true);
                        } else {
                            radio.setChecked(false);
                        }


                        radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    if (!heroBlueprint.getCanHaveSquads().contains(heroBlueprint_.getId())) {
                                        heroBlueprint.getCanHaveSquads().add(heroBlueprint_.getId());
                                    }
                                } else {
                                    heroBlueprint.getCanHaveSquads().remove(heroBlueprint_.getId());
                                }
                            }
                        });

                        squadsView.addView(view);
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void fillInCharacterTypes(final ViewGroup characterTypeView) {
        DatabaseReference ref = firebaseRef.child("/rule/charactertype");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                characterTypeView.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                for (DataSnapshot action : dataSnapshot.getChildren()) {
                    final CharacterType characterType = action.getValue(CharacterType.class);

                    View view = inflater.inflate(R.layout.hero_blueprint_one_charactertype, characterTypeView, false);

                    RadioButton radio = (RadioButton) view.findViewById(R.id.radio);
                    if (characterType.getId().equals(heroBlueprint.getCharacterType())) {
                        radio.setChecked(true);
                    }

                    radio.setText(characterType.getName());
                    radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                heroBlueprint.setCharacterType(characterType.getId());
                            }
                        }
                    });


                    characterTypeView.addView(view);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void updateWeapons() {

        weaponsContainer.removeAllViews();

        for (final WargearOffensive wargear : WargearManager.getInstance().getOffensiveWargear()) {
            View oneWargear = LayoutInflater.from(getActivity()).inflate(R.layout.hero_blueprint_one_weapon, weaponsContainer, false);

            ((TextView) oneWargear.findViewById(R.id.name)).setText(wargear.getName());
            ((TextView) oneWargear.findViewById(R.id.description)).setText(wargear.getCategory());


            final TextView countTextView = ((TextView) oneWargear.findViewById(R.id.count));

            int count = getWeaponCount(wargear.getWeaponId());
            countTextView.setText("" + count);

            oneWargear.findViewById(R.id.add_skill).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heroBlueprint.addWeapon(wargear.getWeaponId());
                    int count = getWeaponCount(wargear.getWeaponId());
                    countTextView.setText("" + count);


                }
            });


            oneWargear.findViewById(R.id.remove_skill).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heroBlueprint.removeWeapon(wargear.getWeaponId());
                    int count = getWeaponCount(wargear.getWeaponId());
                    countTextView.setText("" + count);


                }
            });


            ViewGroup ammoContainer = ((ViewGroup) oneWargear.findViewById(R.id.ammo_container));


            if (wargear.getBulletsPerAction() > 0) {
                ammoContainer.setVisibility(View.VISIBLE);


                ((TextView) oneWargear.findViewById(R.id.ammo_name)).setText(wargear.getClipName());
                ((TextView) oneWargear.findViewById(R.id.ammo_description)).setText("size: " + wargear.getClipSize());

                final TextView ammoCount = ((TextView) oneWargear.findViewById(R.id.ammo_count));
                ammoCount.setText("" + heroBlueprint.getAmmoForWeapon(wargear.getWeaponId()));


                oneWargear.findViewById(R.id.add_ammo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newAmmoCount = heroBlueprint.getAmmoForWeapon(wargear.getWeaponId()) + 1;
                        heroBlueprint.setAmmoForWeapon(wargear.getWeaponId(), newAmmoCount);
                        ammoCount.setText("" + newAmmoCount);
                    }
                });


                oneWargear.findViewById(R.id.remove_ammo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newAmmoCount = heroBlueprint.getAmmoForWeapon(wargear.getWeaponId()) - 1;
                        if (newAmmoCount < 0) {
                            newAmmoCount = 0;
                        }
                        heroBlueprint.setAmmoForWeapon(wargear.getWeaponId(), newAmmoCount);
                        ammoCount.setText("" + newAmmoCount);
                    }
                });

            }


            weaponsContainer.addView(oneWargear);
        }
    }


    private void updateWargear() {

        wargearContainer.removeAllViews();

        for (final Wargear wargear : WargearManager.getInstance().getNonOffensiveWargear()) {
            View oneWargear = LayoutInflater.from(getActivity()).inflate(R.layout.hero_blueprint_one_wargear, wargearContainer, false);

            ((TextView) oneWargear.findViewById(R.id.name)).setText(wargear.getName());
            ((TextView) oneWargear.findViewById(R.id.description)).setText(wargear.getCategory());


            final TextView countTextView = ((TextView) oneWargear.findViewById(R.id.count));

            int count = getWargearCount(wargear.getId());
            countTextView.setText("" + count);

            oneWargear.findViewById(R.id.add_skill).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heroBlueprint.addWargear(wargear.getId());
                    int count = getWargearCount(wargear.getId());
                    countTextView.setText("" + count);


                }
            });


            oneWargear.findViewById(R.id.remove_skill).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heroBlueprint.removeWargear(wargear.getId());
                    int count = getWargearCount(wargear.getId());
                    countTextView.setText("" + count);


                }
            });


            wargearContainer.addView(oneWargear);
        }
    }


    private void updateSkills() {

        skillsContainer.removeAllViews();

        for (final Skill skill : SkillsManager.getInstance().getSkills()) {
            View oneSkill = LayoutInflater.from(getActivity()).inflate(R.layout.hero_blueprint_one_skill, skillsContainer, false);

            ((TextView) oneSkill.findViewById(R.id.name)).setText(skill.getName());
            ((TextView) oneSkill.findViewById(R.id.description)).setText(skill.getDescription());


            final TextView countTextView = ((TextView) oneSkill.findViewById(R.id.count));

            int count = getSkillCount(skill.getId());
            countTextView.setText("" + count);

            oneSkill.findViewById(R.id.add_skill).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heroBlueprint.addSkill(skill.getId());
                    int count = getSkillCount(skill.getId());
                    countTextView.setText("" + count);


                }
            });


            oneSkill.findViewById(R.id.remove_skill).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heroBlueprint.removeSkill(skill.getId());
                    int count = getSkillCount(skill.getId());
                    countTextView.setText("" + count);


                }
            });


            skillsContainer.addView(oneSkill);
        }


    }


    private int getWeaponCount(int id) {
        int count = 0;
        for (Integer weaponId : this.heroBlueprint.getWeapons()) {
            if (weaponId == id) {
                ++count;
            }
        }

        return count;
    }

    private int getWargearCount(int id) {
        int count = 0;
        for (Integer wargearId : this.heroBlueprint.getWargear()) {
            if (wargearId == id) {
                ++count;
            }
        }

        return count;
    }

    private int getSkillCount(int id) {
        int count = 0;
        for (Integer skillId : this.heroBlueprint.getSkills()) {
            if (skillId == id) {
                ++count;
            }
        }

        return count;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (heroBlueprintId != null) {
            firebaseRef.child("admin").child("heroblueprint").child(heroBlueprintId).removeEventListener(valueListener);
        }
    }
}
