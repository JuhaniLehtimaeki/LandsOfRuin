package com.landsofruin.companion.ruleobjectsui;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.gametracker.BR;
import com.landsofruin.gametracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterTypeFragment extends Fragment {
    private static final String ARG_CHARACTER_TYPE_ID = "param1";

    private String charaterTypeId;
    private ViewDataBinding binding;
    private ValueEventListener valueListener;
    private CharacterType characterType;
    private boolean isNew = false;
    private DatabaseReference firebaseRef;


    public CharacterTypeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param characterType Parameter 1.
     * @return A new instance of fragment CharacterTypeFragment.
     */
    public static CharacterTypeFragment newInstance(String characterType) {
        CharacterTypeFragment fragment = new CharacterTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHARACTER_TYPE_ID, characterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            charaterTypeId = getArguments().getString(ARG_CHARACTER_TYPE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_character_type, container, false);
        final View view = binding.getRoot();


        firebaseRef = FirebaseDatabase.getInstance().getReference();


        if (charaterTypeId == null) {
            isNew = true;
            characterType = new CharacterType();
            binding.setVariable(BR.characterType, characterType);
        } else {
            isNew = false;
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.getValue(CharacterType.class);
                    characterType = dataSnapshot.getValue(CharacterType.class);

                    binding.setVariable(BR.characterType, characterType);
                }


                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            };

            firebaseRef.child("rule").child("charactertype").child(charaterTypeId).addValueEventListener(valueListener);
        }


        view.findViewById(R.id.type_hero).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterType.setType(CharacterType.TYPE_HERO);
            }
        });

        view.findViewById(R.id.type_squad_independent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterType.setType(CharacterType.TYPE_SQUAD_INDEPENDENT);
            }
        });

        view.findViewById(R.id.type_squad_slave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterType.setType(CharacterType.TYPE_SQUAD_SLAVE);
            }
        });

        view.findViewById(R.id.type_squad_close_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterType.setType(CharacterType.TYPE_SQUAD_CLOSE_SUPPORT);
            }
        });

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                characterType.setName(((TextView) view.findViewById(R.id.name)).getText().toString());
                characterType.setBaseAPs(Integer.parseInt(((TextView) view.findViewById(R.id.base_aps)).getText().toString()));
                characterType.setMovementPerAP(Float.parseFloat(((TextView) view.findViewById(R.id.movement_per_ap)).getText().toString()));
                characterType.setBaseTargetOffensive(Integer.parseInt(((TextView) view.findViewById(R.id.baseTargetOffensive)).getText().toString()));
                characterType.setBaseTargetOffensiveCloseCombat(Integer.parseInt(((TextView) view.findViewById(R.id.baseTargetOffensiveCloseCombat)).getText().toString()));
                characterType.setBaseTargetDefensive(Integer.parseInt(((TextView) view.findViewById(R.id.baseTargetDefensive)).getText().toString()));
                characterType.setBaseLeadership(Integer.parseInt(((TextView) view.findViewById(R.id.baseLeadership)).getText().toString()));
                characterType.setBaseMaxLoad(Integer.parseInt(((TextView) view.findViewById(R.id.baseMaxLoad)).getText().toString()));
                characterType.setBaseThrowRange(Integer.parseInt(((TextView) view.findViewById(R.id.baseThrowRange)).getText().toString()));
                characterType.setDetection(Integer.parseInt(((TextView) view.findViewById(R.id.detection)).getText().toString()));
                characterType.setBaseCamo(Integer.parseInt(((TextView) view.findViewById(R.id.baseCamo)).getText().toString()));
                characterType.setBaseSuppressionDefence(Integer.parseInt(((TextView) view.findViewById(R.id.baseSuppressionDefence)).getText().toString()));
                characterType.setBaseZombieAgroRange(Integer.parseInt(((TextView) view.findViewById(R.id.baseZombieAgroRange)).getText().toString()));
                characterType.setGearValue(Integer.parseInt(((TextView) view.findViewById(R.id.gearValue)).getText().toString()));

                if (isNew) {
                    DatabaseReference pushId = firebaseRef.child("rule").child("charactertype").push();
                    characterType.setId(pushId.getKey());
                    pushId.setValue(characterType);

                } else {
                    firebaseRef.child("rule").child("charactertype").child(charaterTypeId).setValue(characterType);
                }


                getActivity().finish();

            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (charaterTypeId != null) {
            firebaseRef.child("rule").child("charactertype").child(charaterTypeId).removeEventListener(valueListener);
        }
    }
}
