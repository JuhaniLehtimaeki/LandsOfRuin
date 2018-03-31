package com.landsofruin.companion.tribemanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.heroblueprint.HeroBlueprint;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.skills.SkillsManager;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class YourTribeCharactersActivity extends AppCompatActivity {

    private ValueEventListener heroBluePrintsValueListener;

    private LinkedList<HeroBlueprint> allHeroBlueprints = new LinkedList<>();
    private boolean isHeroBlueprintsLoaded = false;
    private ValueEventListener userAccountValueListener;
    private boolean isAccountLoaded = false;
    private UserAccount userAccount;


    private ViewPager pager;
    private ScreenSlidePagerAdapter adapter;
    private DatabaseReference firebaseRef;
    private String initialBlueprintId;


    public static Intent createIntent(Context context, String blueprintId) {
        Intent ret = new Intent(context, YourTribeCharactersActivity.class);
        ret.putExtra("blueprintId", blueprintId);
        return ret;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("blueprintId")) {
            initialBlueprintId = getIntent().getStringExtra("blueprintId");
        }

        setContentView(R.layout.activity_your_tribe_characters);


        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);

        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);


        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.pager_indicator);
        titleIndicator.setViewPager(pager);


        firebaseRef = FirebaseDatabase.getInstance().getReference();
        heroBluePrintsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                allHeroBlueprints.clear();

                for (DataSnapshot heroBlueprintSnapshot : dataSnapshot.getChildren()) {
                    HeroBlueprint heroBlueprint = heroBlueprintSnapshot.getValue(HeroBlueprint.class);
                    allHeroBlueprints.add(heroBlueprint);
                }

                isHeroBlueprintsLoaded = true;

                if (isAccountLoaded) {
                    initialiseContentAfterEverythignLoaded();
                }

            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };


        firebaseRef.child("admin").child("heroblueprint").addValueEventListener(heroBluePrintsValueListener);


        userAccountValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userAccount = dataSnapshot.getValue(UserAccount.class);

                if (userAccount.getTribe() == null) {


                    Log.e("tribe characters", "something is wrong! User's tribe is null");
                    Toast.makeText(YourTribeCharactersActivity.this, "Please complete your tribe first.", Toast.LENGTH_LONG).show();


                } else {
                    isAccountLoaded = true;

                    if (isHeroBlueprintsLoaded) {
                        initialiseContentAfterEverythignLoaded();
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };


        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(userAccountValueListener);
        setImmersiveMode();


    }


    private void setImmersiveMode() {

        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = 0; //= uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("ui mode", "Turning immersive mode mode off. ");
        } else {
            Log.i("ui mode", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }


    private void initialiseContentAfterEverythignLoaded() {

        LinkedList<String> spawnedCharacters = new LinkedList<>();
        for (TribeCharacter tribeCharacter : userAccount.getTribe().getCharactersValues()) {
            spawnedCharacters.add(tribeCharacter.getBlueprintId());
        }
        int setInitialIndex = -1;
        int index = 0;
        LinkedList<HeroBlueprint> availableHeroBlueprints = new LinkedList<>();
        for (HeroBlueprint blueprint : allHeroBlueprints) {

            if (LookupHelper.getInstance().getCharacterTypeFor(blueprint.getCharacterType()).getType() == CharacterType.TYPE_HERO) {
                if (userAccount.getAccountType() >= blueprint.getStatus()) {

                    availableHeroBlueprints.add(blueprint);
                    if (blueprint.getId().equals(initialBlueprintId)) {
                        setInitialIndex = index;
                    }

                    ++index;
                }
            }
        }


        adapter.setBluePrintsCharacters(availableHeroBlueprints);

        if (setInitialIndex != -1) {
            pager.setCurrentItem(setInitialIndex);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRef.child("user").child("heroblueprint").removeEventListener(heroBluePrintsValueListener);
        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(userAccountValueListener);

    }


    public static class TribeCharacterFragment extends Fragment {

        private static final String ARGUMENT_PC_ID_1 = "ARGUMENT_PC_ID_1";
        private TextView nameTextView;

        private HeroBlueprint heroBlueprint;
        private ImageView profilePic;
        private ViewGroup weaponsContainer;
        private ViewGroup gearContainer;
        private ViewGroup skillsContainer;
        private ValueEventListener heroBluePrintsValueListener;
        private String heroId;
        private ValueEventListener userAccountValueListener;
        private UserAccount userAccount;
        private View spawnUnlockButton;
        private TextView spawnUnlockText;
        private View changePicButton;
        private TribeCharacter tribeCharacter;
        private DatabaseReference firebaseRef;
        private ViewGroup squadsContainer;
        private boolean squadAdded = false;


        public static TribeCharacterFragment newInstance(HeroBlueprint pc1) {
            TribeCharacterFragment ret = new TribeCharacterFragment();

            Bundle args = new Bundle();
            args.putString(ARGUMENT_PC_ID_1, pc1.getId());
            ret.setArguments(args);

            return ret;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            BusProvider.getInstance().register(this);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            BusProvider.getInstance().unregister(this);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.heroblueprint_fragment, container, false);
            heroId = getArguments().getString(ARGUMENT_PC_ID_1);


            spawnUnlockButton = view.findViewById(R.id.spawn_unlock_button);
            spawnUnlockText = (TextView) view.findViewById(R.id.spawn_unlock_text);


            changePicButton = view.findViewById(R.id.change_picture_button);

            nameTextView = ((TextView) view.findViewById(R.id.character_name));


            weaponsContainer = (ViewGroup) view.findViewById(R.id.weapons);
            gearContainer = (ViewGroup) view.findViewById(R.id.gear);
            skillsContainer = (ViewGroup) view.findViewById(R.id.skills);
            squadsContainer = (ViewGroup) view.findViewById(R.id.squads);


            profilePic = (ImageView) view.findViewById(R.id.profile_pic);

            firebaseRef = FirebaseDatabase.getInstance().getReference();


            heroBluePrintsValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    heroBlueprint = dataSnapshot.getValue(HeroBlueprint.class);
                    updateUI();


                    if (userAccount != null) {
                        searchForTribeCharacter();
                    }
                }


                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            };


            firebaseRef.child("admin").child("heroblueprint").child(heroId).addValueEventListener(heroBluePrintsValueListener);


            userAccountValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    userAccount = dataSnapshot.getValue(UserAccount.class);

                    if (userAccount != null && userAccount.getTribe() != null) {
                        updateSpawnButton();

                        if (heroBlueprint != null) {
                            searchForTribeCharacter();
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            };

            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(userAccountValueListener);


            return view;
        }


        private void searchForTribeCharacter() {

            ValueEventListener characterValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    TribeCharacter tribeCharacterTmp;
                    for (DataSnapshot characterDataSnapshot : dataSnapshot.getChildren()) {
                        tribeCharacterTmp = characterDataSnapshot.getValue(TribeCharacter.class);

                        if (tribeCharacterTmp.getBlueprintId().equals(heroBlueprint.getId())) {
                            tribeCharacter = tribeCharacterTmp;
                            updateUI();
                            return;

                        }
                    }


                }


                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            };

            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").addListenerForSingleValueEvent(characterValueListener);

        }


        private void updateSpawnButton() {
            final boolean isUnlocked = userAccount.getUnlockedCharacters().contains(heroBlueprint.getId());
            LinkedList<String> spawnedCharacters = new LinkedList<>();
            for (TribeCharacter tribeCharacter : userAccount.getTribe().getCharactersValues()) {
                spawnedCharacters.add(tribeCharacter.getBlueprintId());
            }

            final boolean isSpawned = spawnedCharacters.contains(heroBlueprint.getId());

            final View.OnClickListener unlockedListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isSpawned) {


                        for (final String squadId : heroBlueprint.getCanHaveSquads()) {
                            String serverId = userAccount.getTribe().removeSpawnedCharacter(squadId);
                            if (serverId != null) {
                                firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").child(serverId).setValue(null);
                            }
                        }


                        String serverId = userAccount.getTribe().removeSpawnedCharacter(heroBlueprint.getId());
                        if (serverId != null) {
                            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").child(serverId).setValue(null);
                        }
                    }


                    final TribeCharacter newChar = TribeCharacter.createFromBlueprint(heroBlueprint);


                    if (heroBlueprint.getCanHaveSquads().size() > 0) {

                        int index = 0;
                        for (final String squadId : heroBlueprint.getCanHaveSquads()) {

                            final int indexFinal = index;
                            firebaseRef.child("admin").child("heroblueprint").child(squadId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    HeroBlueprint squadBlueprint = dataSnapshot.getValue(HeroBlueprint.class);

                                    TribeCharacter newSquad = TribeCharacter.createFromBlueprint(squadBlueprint);

                                    DatabaseReference fbRef = firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").push();
                                    newSquad.setId(fbRef.getKey());
                                    fbRef.setValue(newSquad);

                                    newChar.getSquads().add(fbRef.getKey());

                                    if (indexFinal >= heroBlueprint.getCanHaveSquads().size() - 1) {
                                        DatabaseReference fbRef2 = firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").push();
                                        newChar.setId(fbRef2.getKey());
                                        fbRef2.setValue(newChar);
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            ++index;
                        }

                    } else {
                        DatabaseReference fbRef = firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("defaultTribe").child("characters").push();
                        newChar.setId(fbRef.getKey());
                        fbRef.setValue(newChar);
                    }


                }
            };

            if (isUnlocked) {

                if (isSpawned) {
                    spawnUnlockText.setText("respawn");
                } else {
                    spawnUnlockText.setText("spawn");
                }
                spawnUnlockButton.setOnClickListener(unlockedListener);

            } else {
                spawnUnlockText.setText("Unlock");
                spawnUnlockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userAccount.getUnlockedCharacters().add(heroBlueprint.getId());
                        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("unlockedCharacters").setValue(userAccount.getUnlockedCharacters());


                        for (final String squadId : heroBlueprint.getCanHaveSquads()) {
                            firebaseRef.child("admin").child("heroblueprint").child(squadId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    HeroBlueprint squadBlueprint = dataSnapshot.getValue(HeroBlueprint.class);

                                    userAccount.getUnlockedCharacters().add(squadBlueprint.getId());
                                    firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("unlockedCharacters").setValue(userAccount.getUnlockedCharacters());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }


                        unlockedListener.onClick(v);
                    }
                });


            }


        }

        private void updateUI() {
            if (getActivity() == null) {
                return;
            }

            nameTextView.setText(heroBlueprint.getName());


            if (tribeCharacter != null) {
                Picasso.with(getActivity()).load(tribeCharacter.getCardImageUrl()).into(profilePic);

                changePicButton.setVisibility(View.VISIBLE);
                changePicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MiniaturePicsActivity.startActivity(getActivity(), tribeCharacter.getId());
                    }
                });
            } else {
                Picasso.with(getActivity()).load(heroBlueprint.getCardImageUrl()).into(profilePic);
            }


            //Weapons
            weaponsContainer.removeAllViews();
            final LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (Integer weaponId : heroBlueprint.getWeapons()) {
                LinkedList<Wargear> weaponWgs = WargearManager.getInstance().getWargearByWeaponID(weaponId);
                WargearOffensive weapon = (WargearOffensive) weaponWgs.getFirst();
                View weaponView = inflater.inflate(R.layout.one_weapon_character_select, weaponsContainer, false);

                ((TextView) weaponView.findViewById(R.id.name)).setText(weapon.getName());

                if (weapon.getBulletsPerAction() > 0) {
                    weaponView.findViewById(R.id.ammo).setVisibility(View.VISIBLE);
                    ((TextView) weaponView.findViewById(R.id.ammo)).setText(weapon.getClipName() + " x " + heroBlueprint.getAmmoForWeapon(weaponId));
                }

                Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(weapon.getCategory());
                ((ImageView) weaponView.findViewById(R.id.weapon_icon)).setImageResource(resource);

                ((TextView) weaponView.findViewById(R.id.description)).setText(weapon.getCategory());
                weaponsContainer.addView(weaponView);
            }

            //Gear
            gearContainer.removeAllViews();
            for (Integer wargearId : heroBlueprint.getWargear()) {
                if (wargearId == null) {
                    continue;
                }

                Wargear wargear = WargearManager.getInstance().getWargearById(wargearId);

                View weaponView = inflater.inflate(R.layout.one_weapon_character_select, gearContainer, false);

                ((TextView) weaponView.findViewById(R.id.name)).setText(wargear.getName());

                ((TextView) weaponView.findViewById(R.id.description)).setText(wargear.getCategory());
                gearContainer.addView(weaponView);
            }


            //Skills
            skillsContainer.removeAllViews();


            List<Integer> handledSkills = new ArrayList<>();
            List<Integer> skills = heroBlueprint.getSkills();
            for (Integer skill : skills) {
                if (handledSkills.contains(skill)) {
                    continue;
                }
                handledSkills.add(skill);
                View skillView = inflater.inflate(
                        R.layout.one_skill_character_select, skillsContainer,
                        false);

                int count = 0;
                for (int skillId : skills) {
                    if (skillId == skill) {
                        ++count;
                    }
                }

                if (count > 1) {
                    ((TextView) skillView.findViewById(R.id.skill_count)).setText(""
                            + count);

                } else {
                    skillView.findViewById(R.id.skill_count).setVisibility(View.INVISIBLE);
                }

                Skill skill_ = SkillsManager.getInstance().getSkillByID(skill);
                ((TextView) skillView.findViewById(R.id.name)).setText(skill_.getName());
                ((TextView) skillView.findViewById(R.id.description)).setText(skill_.getDescription());

                skillsContainer.addView(skillView);
            }


            // squads
            if (!squadAdded) {
                squadAdded = true;

                squadsContainer.removeAllViews();

                for (final String squadId : heroBlueprint.getCanHaveSquads()) {


                    Log.e("TMP", "squads adding " + squadId);

                    firebaseRef.child("admin").child("heroblueprint").child(squadId).addListenerForSingleValueEvent(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HeroBlueprint squadBlueprint = dataSnapshot.getValue(HeroBlueprint.class);


                            View squadView = inflater.inflate(
                                    R.layout.one_squad_character_select, squadsContainer,
                                    false);

                            Picasso.with(getContext()).load(squadBlueprint.getPortraitImageUrl()).into((ImageView) squadView.findViewById(R.id.image));

                            ((TextView) squadView.findViewById(R.id.name)).setText(squadBlueprint.getName());
                            squadsContainer.addView(squadView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            firebaseRef.child("admin").child("heroblueprint").child(heroId).removeEventListener(heroBluePrintsValueListener);
            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(userAccountValueListener);
        }
    }

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        private List<HeroBlueprint> heroBlueprints = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);

        }


        public void setBluePrintsCharacters(List<HeroBlueprint> characters) {
            this.heroBlueprints = characters;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return TribeCharacterFragment.newInstance(this.heroBlueprints.get(position));
        }

        @Override
        public int getCount() {
            return this.heroBlueprints.size();
        }


    }
}
