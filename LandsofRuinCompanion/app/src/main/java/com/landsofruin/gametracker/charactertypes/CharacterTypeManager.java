package com.landsofruin.gametracker.charactertypes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;

import java.util.HashMap;
import java.util.LinkedList;

public class CharacterTypeManager {


    private static CharacterTypeManager instance = new CharacterTypeManager();

    private LinkedList<CharacterType> characterTypes = new LinkedList<>();


    private CharacterTypeManager() {
    }

    public static CharacterTypeManager getInstance() {
        return instance;
    }


    public LinkedList<CharacterType> getCharacterTypes() {
        return characterTypes;
    }

    public CharacterType getDefaultCharacterType() {
        return characterTypes.getFirst();
    }

    private HashMap<String, CharacterType> characterTypeLookupCache = new HashMap<>();

    public CharacterType getCharacterTypeByID(String id) {
        CharacterType ret = characterTypeLookupCache.get(id);

        if (ret == null) {
            for (CharacterType characterType : this.characterTypes) {
                if (characterType.getId().equals(id)) {
                    ret = characterType;
                    characterTypeLookupCache.put(id, ret);
                }
            }
        }
        return ret;

    }

    public void clearData() {
        this.characterTypes.clear();
    }

    public void addData(CharacterType characterType) {
        this.characterTypes.add(characterType);
    }

    private void readCharacterTypes() {

        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

        firebaseRef.child("rule").child("charactertype").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue(CharacterType.class);
                CharacterType characterType = dataSnapshot.getValue(CharacterType.class);
                characterTypes.add(characterType);
                firebaseRef.removeEventListener(this);
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

}
