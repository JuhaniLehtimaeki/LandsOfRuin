package com.landsofruin.companion.ruleobjectsui;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.companion.state.CharacterEffectFactory;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.gametracker.BR;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actions.ActionManager;
import com.landsofruin.gametracker.skills.SkillsManager;
import com.landsofruin.gametracker.wargear.WargearManager;

import java.util.LinkedList;

public class SkillFragment extends Fragment {
    private static final String ARG_SKILL_ID = "param1";

    private int skillId;
    private ViewDataBinding binding;
    private Skill skill;
    private boolean isNew = false;
    private DatabaseReference firebaseRef;


    public SkillFragment() {
    }


    public static SkillFragment newInstance(int skill) {
        SkillFragment fragment = new SkillFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SKILL_ID, skill);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            skillId = getArguments().getInt(ARG_SKILL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_skill_edit, container, false);
        final View view = binding.getRoot();


        firebaseRef = FirebaseDatabase.getInstance().getReference();


        if (skillId == 0) {
            isNew = true;
            skill = new Skill();

        } else {
            isNew = false;
            skill = SkillsManager.getInstance().getSkillByID(skillId);
        }

        binding.setVariable(BR.skill, skill);


        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                skill.setName(((TextView) view.findViewById(R.id.name)).getText().toString());
                skill.setDescription(((TextView) view.findViewById(R.id.description)).getText().toString());
                skill.setOffensiveModifier(Integer.parseInt(
                        ((TextView) view.findViewById(R.id.offensive_modifier)).getText().toString()));
                skill.setDefensiveModifier(Integer.parseInt(
                        ((TextView) view.findViewById(R.id.defensive_modifier)).getText().toString()));
                skill.setLeadershipModifier(Integer.parseInt(
                        ((TextView) view.findViewById(R.id.leadership_modifier)).getText().toString()));

                if (isNew) {
                    //FIXME
//                    Firebase pushId = firebaseRef.child("rule").child("action").push();
//                    action.setId(pushId.getKey());
//                    pushId.setValue(action);

                } else {
                    firebaseRef.child("rule").child("skill").child("" + skillId).setValue(skill);
                }


                getActivity().finish();

            }
        });

        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.enables_actions);
        for (final Action action_ : ActionManager.getInstance().getActions()) {

            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, viewGroup, false);

            checkbox.setChecked(skill.getEnablesActions().contains(action_.getId()));
            checkbox.setText(action_.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        skill.getEnablesActions().add(new Integer(action_.getId()));
                    } else {
                        skill.getEnablesActions().remove(new Integer(action_.getId()));
                    }
                }
            });

            viewGroup.addView(checkbox);
        }

        LinkedList<CharacterEffect> effects = new LinkedList<>();
        for (int i = 0; i < CharacterEffect.ALL_EFFECTS.length; ++i) {
            effects.add(CharacterEffectFactory.createCharacterEffect(CharacterEffect.ALL_EFFECTS[i], 0));
        }
        viewGroup = (ViewGroup) view.findViewById(R.id.effects_pre_game);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, viewGroup, false);

            checkbox.setChecked(skill.getAddsDefaultEffectsPregame().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        skill.getAddsDefaultEffectsPregame().add(new Integer(effect.getId()));
                    } else {
                        skill.getAddsDefaultEffectsPregame().remove(new Integer(effect.getId()));
                    }
                }
            });

            viewGroup.addView(checkbox);
        }


        LinkedList<String> wgTypes = new LinkedList<>();
        for (Wargear wg : WargearManager.getInstance().getWargear()) {
            if (!wgTypes.contains(wg.getType())) {
                wgTypes.add(wg.getType());
            }
        }

        LinkedList<String> wgCategories = new LinkedList<>();
        for (Wargear wg : WargearManager.getInstance().getWargear()) {
            if (!wgCategories.contains(wg.getCategory())) {
                wgCategories.add(wg.getCategory());
            }
        }


        viewGroup = (ViewGroup) view.findViewById(R.id.effects_wg_type);
        for (final String type : wgTypes) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, viewGroup, false);

            checkbox.setChecked(skill.getRequiresEquipmentType().contains(type));
            checkbox.setText(type);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        skill.getRequiresEquipmentType().add(type);
                    } else {
                        skill.getRequiresEquipmentType().remove(type);
                    }
                }
            });

            viewGroup.addView(checkbox);
        }


        viewGroup = (ViewGroup) view.findViewById(R.id.effects_wg_category);
        for (final String category : wgCategories) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, viewGroup, false);

            checkbox.setChecked(skill.getRequiresEquipmentCategory().contains(category));
            checkbox.setText(category);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        skill.getRequiresEquipmentCategory().add(category);
                    } else {
                        skill.getRequiresEquipmentCategory().remove(category);
                    }
                }
            });

            viewGroup.addView(checkbox);
        }

        viewGroup = (ViewGroup) view.findViewById(R.id.required_wg);
        for (final Wargear wargear : WargearManager.getInstance().getWargear()) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, viewGroup, false);

            checkbox.setChecked(skill.getRequiresEquipmentId().contains(wargear));
            checkbox.setText(wargear.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        skill.getRequiresEquipmentId().add(wargear.getId());
                    } else {
                        skill.getRequiresEquipmentId().remove(wargear.getId());
                    }
                }
            });

            viewGroup.addView(checkbox);
        }


        return view;
    }

}
