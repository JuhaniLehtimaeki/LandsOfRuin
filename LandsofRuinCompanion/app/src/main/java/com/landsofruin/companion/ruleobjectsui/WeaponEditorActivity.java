package com.landsofruin.companion.ruleobjectsui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class WeaponEditorActivity extends AppCompatActivity {


    private ViewGroup modesContainer;

    private LinkedList<Wargear> weapons;
    private DatabaseReference firebaseRef;

    public static void startActivity(int weaponId, Context context) {
        Intent intent = new Intent(context, WeaponEditorActivity.class);
        intent.putExtra("weaponId", weaponId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseRef = FirebaseDatabase.getInstance().getReference();

        int weaponId = getIntent().getIntExtra("weaponId", 0);
        weapons = new LinkedList<>();
        weapons.addAll(WargearManager.getInstance().getWargearByWeaponID(weaponId));
        setContentView(R.layout.activity_weapon_editor);

        final EditText nameEditText = (EditText) findViewById(R.id.name);
        final EditText clipSizeEditText = (EditText) findViewById(R.id.clip_size);
        final EditText clipNameEditText = (EditText) findViewById(R.id.clip_name);
        final EditText clipGearValueEditText = (EditText) findViewById(R.id.clip_gear_value);
        final EditText clipWeightEditText = (EditText) findViewById(R.id.clip_weight);
        final EditText camoModifierEditText = (EditText) findViewById(R.id.camo_modifier);
        final EditText weightEditText = (EditText) findViewById(R.id.weight);
        final EditText typeEditText = (EditText) findViewById(R.id.type);
        final EditText wargearTypeEditText = (EditText) findViewById(R.id.wargear_type);
        final EditText imageUriEditText = (EditText) findViewById(R.id.image_uri);


        final WargearOffensive weapon = (WargearOffensive) weapons.getFirst();

        nameEditText.setText(weapon.getName());
        clipSizeEditText.setText("" + weapon.getClipSize());
        clipNameEditText.setText("" + weapon.getClipName());
        clipGearValueEditText.setText("" + weapon.getClipGearValue());
        clipWeightEditText.setText("" + weapon.getClipWeight());
        camoModifierEditText.setText("" + weapon.getCamoModifier());
        weightEditText.setText("" + weapon.getWeight());
        typeEditText.setText("" + weapon.getType());
        wargearTypeEditText.setText("" + weapon.getWargearType());
        imageUriEditText.setText("" + weapon.getImageUri());

        if (weapon.getImageUri() != null && !weapon.getImageUri().isEmpty()) {
            Picasso.with(this).load(weapon.getImageUri()).into((ImageView) findViewById(R.id.image));
        }


        modesContainer = (ViewGroup) findViewById(R.id.modes_container);
        for (Wargear wg : weapons) {
            final WargearOffensive weapon_ = (WargearOffensive) wg;
            View oneModeView = getLayoutInflater().inflate(R.layout.weapon_editor_one_mode, modesContainer, false);

            ((EditText) oneModeView.findViewById(R.id.name)).setText(weapon_.getModeName());
            ((EditText) oneModeView.findViewById(R.id.name)).addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    weapon_.setModeName(s.toString());
                }
            });


            final EditText bulletsPerAction = ((EditText) oneModeView.findViewById(R.id.bullets_per_action));
            bulletsPerAction.setText("" + weapon_.getBulletsPerAction());
            bulletsPerAction.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        weapon_.setBulletsPerAction(Integer.parseInt(s.toString()));
                        bulletsPerAction.setError(null);
                    } catch (Exception e) {
                        bulletsPerAction.setError(e.getMessage());
                    }


                }
            });


            final EditText maxTargets = ((EditText) oneModeView.findViewById(R.id.max_targets));
            maxTargets.setText("" + weapon_.getMaxTargets());
            maxTargets.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        weapon_.setMaxTargets(Integer.parseInt(s.toString()));
                        maxTargets.setError(null);
                    } catch (Exception e) {
                        maxTargets.setError(e.getMessage());
                    }

                }
            });


            final EditText noise = ((EditText) oneModeView.findViewById(R.id.noise));
            noise.setText("" + weapon_.getNoiseLevel());
            noise.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        weapon_.setNoiseLevel(Integer.parseInt(s.toString()));
                        noise.setError(null);
                    } catch (Exception e) {
                        noise.setError(e.getMessage());
                    }

                }
            });


            final EditText shortModifier = ((EditText) oneModeView.findViewById(R.id.modifier_short));
            shortModifier.setText("" + weapon_.getModifierShort());
            shortModifier.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        weapon_.setModifierShort(Integer.parseInt(s.toString()));
                        shortModifier.setError(null);
                    } catch (Exception e) {
                        shortModifier.setError(e.getMessage());
                    }
                }
            });


            final EditText midModifier = ((EditText) oneModeView.findViewById(R.id.modifier_mid));
            midModifier.setText("" + weapon_.getModifierMid());
            midModifier.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        weapon_.setModifierMid(Integer.parseInt(s.toString()));
                        midModifier.setError(null);
                    } catch (Exception e) {
                        midModifier.setError(e.getMessage());
                    }

                }
            });


            final EditText longModifier = ((EditText) oneModeView.findViewById(R.id.modifier_long));
            longModifier.setText("" + weapon_.getModifierLong());
            longModifier.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        weapon_.setModifierLong(Integer.parseInt(s.toString()));
                        longModifier.setError(null);
                    } catch (Exception e) {
                        longModifier.setError(e.getMessage());
                    }

                }
            });


            final EditText deployedModifier = ((EditText) oneModeView.findViewById(R.id.modifier_deployed));
            deployedModifier.setText("" + weapon_.getModifierDeployed());
            deployedModifier.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        weapon_.setModifierDeployed(Integer.parseInt(s.toString()));
                        deployedModifier.setError(null);
                    } catch (Exception e) {
                        deployedModifier.setError(e.getMessage());
                    }

                }
            });


            final EditText heavyArmoured = ((EditText) oneModeView.findViewById(R.id.dice_heavy_armoured));

            heavyArmoured.setText("" + weapon_.getDiceHeavyArmored());
            heavyArmoured.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {

                    try {

                        weapon_.setDiceHeavyArmored(Integer.parseInt(s.toString()));
                        heavyArmoured.setError(null);
                    } catch (Exception e) {
                        heavyArmoured.setError(e.getMessage());
                    }

                }
            });


            final EditText lightarmoured = ((EditText) oneModeView.findViewById(R.id.dice_light_armoured));

            lightarmoured.setText("" + weapon_.getDiceLightArmored());
            lightarmoured.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        weapon_.setDiceLightArmored(Integer.parseInt(s.toString()));
                        lightarmoured.setError(null);
                    } catch (Exception e) {
                        lightarmoured.setError(e.getMessage());
                    }
                }
            });


            final EditText lightinfantry = ((EditText) oneModeView.findViewById(R.id.dice_light_infantry));

            lightinfantry.setText("" + weapon_.getDiceLightInfantry());
            lightinfantry.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        weapon_.setDiceLightInfantry(Integer.parseInt(s.toString()));
                        lightinfantry.setError(null);
                    } catch (Exception e) {
                        lightinfantry.setError(e.getMessage());
                    }


                }
            });


            final EditText heavyinfantry = ((EditText) oneModeView.findViewById(R.id.dice_heavy_infantry));
            heavyinfantry.setText("" + weapon_.getDiceHeavyInfantry());
            heavyinfantry.addTextChangedListener(new Watcher() {

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        weapon_.setDiceHeavyInfantry(Integer.parseInt(s.toString()));
                        heavyinfantry.setError(null);
                    } catch (Exception e) {
                        heavyinfantry.setError(e.getMessage());
                    }


                }
            });

            final CheckBox squadModeCheckbox = ((CheckBox) oneModeView.findViewById(R.id.available_for_squads));
            squadModeCheckbox.setChecked(weapon_.isSquadMode());
            squadModeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    weapon_.setSquadMode(isChecked);
                }
            });

            modesContainer.addView(oneModeView);
        }


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Wargear wg : weapons) {
                    WargearOffensive weapon_ = (WargearOffensive) wg;
                    weapon_.setName(nameEditText.getText().toString());
                    weapon_.setClipSize(Integer.parseInt(clipSizeEditText.getText().toString()));
                    weapon_.setClipName(clipNameEditText.getText().toString());
                    weapon_.setClipGearValue(Integer.parseInt(clipGearValueEditText.getText().toString()));
                    weapon_.setClipWeight(Float.parseFloat(clipWeightEditText.getText().toString()));
                    weapon_.setCamoModifier(Integer.parseInt(camoModifierEditText.getText().toString()));
                    weapon_.setWeight(Float.parseFloat(weightEditText.getText().toString()));
                    weapon_.setType(typeEditText.getText().toString());
                    weapon_.setWargearType(Integer.parseInt(wargearTypeEditText.getText().toString()));
                    weapon_.setImageUri(imageUriEditText.getText().toString());

                }


                for (Wargear wg : weapons) {
                    WargearOffensive weapon_ = (WargearOffensive) wg;
                    firebaseRef.child("rule/wargear/wargearoffensive").child("" + wg.getId()).setValue(weapon_);
                }

                finish();
            }
        });
    }


    static abstract class Watcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }


    }
}
