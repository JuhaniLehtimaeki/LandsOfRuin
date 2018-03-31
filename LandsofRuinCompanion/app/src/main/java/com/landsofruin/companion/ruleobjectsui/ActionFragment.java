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
import com.landsofruin.gametracker.BR;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actions.ActionManager;

import java.util.LinkedList;

public class ActionFragment extends Fragment {
    private static final String ARG_ACTION_ID = "param1";

    private int actionId;
    private ViewDataBinding binding;
    private Action action;
    private boolean isNew = false;
    private DatabaseReference firebaseRef;


    public ActionFragment() {
    }


    public static ActionFragment newInstance(int action) {
        ActionFragment fragment = new ActionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION_ID, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actionId = getArguments().getInt(ARG_ACTION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_edit, container, false);
        final View view = binding.getRoot();


        firebaseRef = FirebaseDatabase.getInstance().getReference();


        if (actionId == 0) {
            isNew = true;
            action = new Action();
            binding.setVariable(BR.action, action);
        } else {
            isNew = false;
            action = ActionManager.getInstance().getActionForId(actionId);
            binding.setVariable(BR.action, action);
        }


        view.findViewById(R.id.type_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.setActionType(Action.ACTION_MORALE_OK);
            }
        });

        view.findViewById(R.id.type_confusion_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.setActionType(Action.ACTION_MORALE_OK_CONFUSED);
            }
        });


        view.findViewById(R.id.type_any).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.setActionType(Action.ACTION_MORALE_TYPE_ANY);
            }
        });


        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                action.setName(((TextView) view.findViewById(R.id.name)).getText().toString());
                action.setDescription(((TextView) view.findViewById(R.id.description)).getText().toString());
                action.setActionPoints(Integer.parseInt(
                        ((TextView) view.findViewById(R.id.action_points)).getText().toString()));
                action.setOrder(Integer.parseInt(
                        ((TextView) view.findViewById(R.id.order)).getText().toString()));

                if (isNew) {
                    //FIXME
//                    Firebase pushId = firebaseRef.child("rule").child("action").push();
//                    action.setId(pushId.getKey());
//                    pushId.setValue(action);

                } else {
                    firebaseRef.child("rule").child("action").child("" + actionId).setValue(action);
                }


                getActivity().finish();

            }
        });


        LinkedList<CharacterEffect> effects = new LinkedList<>();
        for (int i = 0; i < CharacterEffect.ALL_EFFECTS.length; ++i) {
            effects.add(CharacterEffectFactory.createCharacterEffect(CharacterEffect.ALL_EFFECTS[i], 0));
        }

        ViewGroup effectsViewGroup = (ViewGroup) view.findViewById(R.id.adds_effects_self);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getAddsEffectSelf().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getAddsEffectSelf().add(new Integer(effect.getId()));
                    } else {
                        action.getAddsEffectSelf().remove(new Integer(effect.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);
        }


        effectsViewGroup = (ViewGroup) view.findViewById(R.id.adds_effects_friendly);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getAddsEffectFriendly().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getAddsEffectFriendly().add(new Integer(effect.getId()));
                    } else {
                        action.getAddsEffectFriendly().remove(new Integer(effect.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);
        }


        effectsViewGroup = (ViewGroup) view.findViewById(R.id.adds_effects_enemy);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getAddsEffectEnemy().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getAddsEffectEnemy().add(new Integer(effect.getId()));
                    } else {
                        action.getAddsEffectEnemy().remove(new Integer(effect.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);


        }


        effectsViewGroup = (ViewGroup) view.findViewById(R.id.targets_effects_self);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getTargetsEffectSelf().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getTargetsEffectSelf().add(new Integer(effect.getId()));
                    } else {
                        action.getTargetsEffectSelf().remove(new Integer(effect.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);

        }


        effectsViewGroup = (ViewGroup) view.findViewById(R.id.targets_effects_friendly);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getTargetsEffectFriendly().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getTargetsEffectFriendly().add(new Integer(effect.getId()));
                    } else {
                        action.getTargetsEffectFriendly().remove(new Integer(effect.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);

        }


        effectsViewGroup = (ViewGroup) view.findViewById(R.id.targets_effects_enemy);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getTargetsEffectEnemy().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getTargetsEffectEnemy().add(new Integer(effect.getId()));
                    } else {
                        action.getTargetsEffectEnemy().remove(new Integer(effect.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);

        }


        effectsViewGroup = (ViewGroup) view.findViewById(R.id.removes_effects);
        for (final CharacterEffect effect : effects) {
            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getRemovesEffects().contains(effect.getId()));
            checkbox.setText(effect.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getRemovesEffects().add(new Integer(effect.getId()));
                    } else {
                        action.getRemovesEffects().remove(new Integer(effect.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);
        }


        effectsViewGroup = (ViewGroup) view.findViewById(R.id.blocks_actions);
        for (final Action action_ : ActionManager.getInstance().getActions()) {

            if (action_.getId() == this.actionId) {
                continue;
            }

            AppCompatCheckBox checkbox = (AppCompatCheckBox) inflater.inflate(R.layout.action_edit_one_effect, effectsViewGroup, false);

            checkbox.setChecked(action.getBlocksActions().contains(action_.getId()));
            checkbox.setText(action_.getName());
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        action.getBlocksActions().add(new Integer(action_.getId()));
                    } else {
                        action.getBlocksActions().remove(new Integer(action_.getId()));
                    }
                }
            });

            effectsViewGroup.addView(checkbox);
        }


        return view;
    }

}
