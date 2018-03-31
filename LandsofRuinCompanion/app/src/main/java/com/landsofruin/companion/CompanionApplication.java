package com.landsofruin.companion;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.battlereport.BattleReportLoggerImpl;
import com.landsofruin.companion.newsticker.NewsTickerManager;
import com.landsofruin.companion.objecthelpers.AccountObjectHelperImpl;
import com.landsofruin.companion.objecthelpers.EventHelperImpl;
import com.landsofruin.companion.objecthelpers.IconMapperImpl;
import com.landsofruin.companion.objecthelpers.RuleObjectHelperImpl;
import com.landsofruin.companion.sound.SoundEffects;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.damage.DamageLine;
import com.landsofruin.companion.state.gameruleobjects.scenario.PlayerRole;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.gameruleobjects.scenario.ScenarioObjective;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearAccessory;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearDefensive;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.AccountHelper;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLogger;
import com.landsofruin.companion.state.proxyobjecthelper.EventsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.user.UserAccountManager;
import com.landsofruin.gametracker.actions.ActionManager;
import com.landsofruin.gametracker.charactertypes.CharacterTypeManager;
import com.landsofruin.gametracker.damage.data.DamageDataManager;
import com.landsofruin.gametracker.scenarios.ScenariosManager;
import com.landsofruin.gametracker.skills.SkillsManager;
import com.landsofruin.gametracker.wargear.WargearManager;

import java.util.LinkedList;

public class CompanionApplication extends Application {
    private static CompanionApplication instance;
    private SoundEffects soundEffects;
    private GameState joinedGame;
    private String hostingGameIdentifier;

    /**
     * Helper method to get the CompanionApplication object from an activity.
     */
    public static CompanionApplication from(Activity activity) {
        return (CompanionApplication) activity.getApplication();
    }

    /**
     * Helper method to get the CompanionApplication object from a fragment.
     */
    public static CompanionApplication from(Fragment fragment) {
        return from(fragment.getActivity());
    }

    @Deprecated // Use from() methods that take an activity/fragment object
    public static CompanionApplication getInstance() {
        return instance;
    }


    public void setHostingGameIdentifier(String hostingGameIdentifier) {
        this.hostingGameIdentifier = hostingGameIdentifier;
    }

    public boolean isHostingGame(String gameIdentifier) {
        return gameIdentifier.equals(hostingGameIdentifier);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        CompanionApplication.instance = this;

        soundEffects = new SoundEffects(this);


        AccountHelper.getInstance().setAccountObjectHelper(new AccountObjectHelperImpl());
        LookupHelper.getInstance().setRuleObjectHelper(new RuleObjectHelperImpl());
        EventsHelper.getInstance().setEventHelperInterface(new EventHelperImpl());
        IconConstantsHelper.getInstance().setIconMapper(new IconMapperImpl());
        BattleReportLogger.getInstance().setLogger(new BattleReportLoggerImpl());

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();


        firebaseRef.child("rule").child("charactertype").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CharacterTypeManager.getInstance().clearData();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CharacterType characterType = snapshot.getValue(CharacterType.class);
                    CharacterTypeManager.getInstance().addData(characterType);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });


        firebaseRef.child("rule").child("skill").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SkillsManager.getInstance().clearData();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Skill characterType = snapshot.getValue(Skill.class);

                    LinkedList<Integer> enablesActionsList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("enablesActions").getChildren()) {
                        enablesActionsList.add(enablesSkills.getValue(Integer.class));
                    }


                    characterType.setEnablesActions(enablesActionsList);

                    SkillsManager.getInstance().addData(characterType);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });


        firebaseRef.child("rule").child("wargear").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WargearManager.getInstance().clearData();


                for (DataSnapshot snapshot : dataSnapshot.child("wargearaccessory").getChildren()) {
                    WargearAccessory wargearAccessory = snapshot.getValue(WargearAccessory.class);
                    WargearManager.getInstance().addData(wargearAccessory);
                }

                for (DataSnapshot snapshot : dataSnapshot.child("wargearconsumable").getChildren()) {
                    WargearConsumable wargearAccessory = snapshot.getValue(WargearConsumable.class);
                    WargearManager.getInstance().addData(wargearAccessory);
                }

                for (DataSnapshot snapshot : dataSnapshot.child("wargeardefensive").getChildren()) {
                    WargearDefensive wargearAccessory = snapshot.getValue(WargearDefensive.class);
                    WargearManager.getInstance().addData(wargearAccessory);
                }

                for (DataSnapshot snapshot : dataSnapshot.child("wargearoffensive").getChildren()) {
                    WargearOffensive wargearAccessory = snapshot.getValue(WargearOffensive.class);
                    WargearManager.getInstance().addData(wargearAccessory);
                }


            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });

        firebaseRef.child("rule").child("action").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActionManager.getInstance().clearData();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Action action = snapshot.getValue(Action.class);
                    ActionManager.getInstance().addData(action);


                    LinkedList<Integer> tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("addsEffectSelf").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setAddsEffectSelf(tmpList);


                    tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("addsEffectEnemy").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setAddsEffectEnemy(tmpList);


                    tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("addsEffectFriendly").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setAddsEffectFriendly(tmpList);


                    tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("removesEffects").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setRemovesEffects(tmpList);


                    tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("blocksActions").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setBlocksActions(tmpList);


                    tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("targetsEffectSelf").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setTargetsEffectSelf(tmpList);


                    tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("targetsEffectFriendly").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setTargetsEffectFriendly(tmpList);

                    tmpList = new LinkedList<>();
                    for (DataSnapshot enablesSkills : snapshot.child("targetsEffectEnemy").getChildren()) {
                        tmpList.add(enablesSkills.getValue(Integer.class));
                    }
                    action.setTargetsEffectEnemy(tmpList);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });


        firebaseRef.child("rule").child("damage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DamageDataManager.getInstance().clearData();


                for (DataSnapshot snapshot : dataSnapshot.child("damagelineclosecombat").getChildren()) {
                    DamageLine wargearAccessory = snapshot.getValue(DamageLine.class);
                    DamageDataManager.getInstance().addCCData(wargearAccessory);
                }

                for (DataSnapshot snapshot : dataSnapshot.child("damagelineshooting").getChildren()) {
                    DamageLine wargearAccessory = snapshot.getValue(DamageLine.class);
                    DamageDataManager.getInstance().addShootingData(wargearAccessory);
                }


            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });


        firebaseRef.child("rule").child("scenariodata").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ScenariosManager.getInstance().clearData();


                for (DataSnapshot snapshot : dataSnapshot.child("scenario").getChildren()) {
                    Scenario scenario = snapshot.getValue(Scenario.class);
                    ScenariosManager.getInstance().addScenarioData(scenario);
                }

                for (DataSnapshot snapshot : dataSnapshot.child("objective").getChildren()) {
                    ScenarioObjective scenarioObjective = snapshot.getValue(ScenarioObjective.class);
                    ScenariosManager.getInstance().addObjectiveData(scenarioObjective);
                }

                for (DataSnapshot snapshot : dataSnapshot.child("playerrole").getChildren()) {
                    PlayerRole playerRole = snapshot.getValue(PlayerRole.class);
                    ScenariosManager.getInstance().addPlayerRoleData(playerRole);
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });


        NewsTickerManager.getInstance().initialise();
    }

    public SoundEffects getSoundEffects() {
        return soundEffects;
    }

    /**
     * Remove currently instance of joined game (Client). Should only be called when no
     * BaseGameActivity is running anymore.
     */
    public void removeClientGame() {
        this.joinedGame = null;
    }

    /**
     * Is there currently a game joined (Client)?
     */
    public boolean hasClientGame() {
        return this.joinedGame != null;
    }

    /**
     * Get the instance of the game that is currently joined (Client).
     */
    public GameState getClientGame() {
        return joinedGame;
    }

    /**
     * Remove existing instance of joined game (if there's any) and replace it with a new
     * instance with the given title and identifier.
     */
    public void resetClientGame(String identifier, String title) {
        this.joinedGame = new GameState(identifier, title);
    }

}
