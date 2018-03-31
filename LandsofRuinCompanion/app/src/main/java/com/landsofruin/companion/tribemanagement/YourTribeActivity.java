package com.landsofruin.companion.tribemanagement;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.landsofruin.companion.battlereport.BattleReportsActivity;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.heroblueprint.HeroBlueprint;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.user.UserAccountManager;
import com.landsofruin.gametracker.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class YourTribeActivity extends AppCompatActivity {

    private ValueEventListener heroBluePrintsValueListener;


    private LinkedList<HeroBlueprint> heroBlueprints = new LinkedList<>();
    private boolean isHeroBlueprintsLoaded = false;
    private boolean isAccountLoaded = false;

    private TextView tribeNameTextView;
    //    private ViewGroup charactersContainer;
    private TextView smallTribeIcon;
    private TextView largeTribeIcon;
    private ViewGroup characterContainer;
    private DatabaseReference firebaseRef;
    private UserAccountManager.AccountRefreshListener accountListener;
    private TextView userNameTextView;
    private ImageView reclaimerIcon;
    private ImageView baleanIcon;
    private View completedGamesButton;
    private TextView pastBattlesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseRef = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "PLease make sure your internet connection is working and that you're logged in to Lands of Ruin and try again. Sorry for the inconvinience!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        setContentView(R.layout.activity_your_tribe);
        completedGamesButton = findViewById(R.id.completed_games);

        tribeNameTextView = (TextView) findViewById(R.id.tribe_name);
        smallTribeIcon = (TextView) findViewById(R.id.small_icon_url);
        largeTribeIcon = (TextView) findViewById(R.id.large_icon_url);
        userNameTextView = (TextView) findViewById(R.id.user_name);
        pastBattlesTextView = (TextView) findViewById(R.id.past_battles);


        characterContainer = (ViewGroup) findViewById(R.id.characters_container);

        completedGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourTribeActivity.this, BattleReportsActivity.class));
            }
        });


        heroBluePrintsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                heroBlueprints.clear();

                for (DataSnapshot heroBlueprintSnapshot : dataSnapshot.getChildren()) {
                    HeroBlueprint heroBlueprint = heroBlueprintSnapshot.getValue(HeroBlueprint.class);
                    heroBlueprints.add(heroBlueprint);
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


        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        firebaseRef.child("admin").child("heroblueprint").addValueEventListener(heroBluePrintsValueListener);


        accountListener = new UserAccountManager.AccountRefreshListener() {
            @Override
            public void onAccountRefreshed() {


                UserAccount userAccount = UserAccountManager.getInstance().getUserAccount();

                if (userAccount == null) {
                    // user account manager will create one automatically
                } else if (userAccount.getTribe() == null) {
                    UserAccountManager.getInstance().createEmptyDefaultTribe();
                } else {
                    isAccountLoaded = true;
                    if (isHeroBlueprintsLoaded) {
                        initialiseContentAfterEverythignLoaded();
                    }
                }

            }
        };

        UserAccountManager.getInstance().addAccountRefreshListener(accountListener);


        reclaimerIcon = ((ImageView) findViewById(R.id.reclaimers_icon));
        baleanIcon = ((ImageView) findViewById(R.id.baleans_icon));

        baleanIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                largeTribeIcon.setText("http://landsofruin.com/app_assets/balean_large.png");
                smallTribeIcon.setText("http://landsofruin.com/app_assets/balean_small.png");


                baleanIcon.setSelected(true);
                reclaimerIcon.setSelected(false);
            }
        });


        Picasso.with(this).load("http://landsofruin.com/app_assets/balean_large.png").into(baleanIcon);


        reclaimerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smallTribeIcon.setText("http://landsofruin.com/app_assets/reclaimers_small.png");
                largeTribeIcon.setText("http://landsofruin.com/app_assets/reclaimers_large.png");

                baleanIcon.setSelected(false);
                reclaimerIcon.setSelected(true);
            }
        });

        Picasso.with(this).load("http://landsofruin.com/app_assets/reclaimers_large.png").into(reclaimerIcon);


    }


    private void save() {
        UserAccount userAccount = UserAccountManager.getInstance().getUserAccount();
        if (userAccount != null) {
            userAccount.getTribe().setName(tribeNameTextView.getText().toString());
            userAccount.getTribe().setLargeTribeLogoUri(largeTribeIcon.getText().toString());
            userAccount.getTribe().setSmallTribeLogoUri(smallTribeIcon.getText().toString());
            userAccount.setUsername(userNameTextView.getText().toString());
        }

        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAccount);
    }

    private void initialiseContentAfterEverythignLoaded() {
        UserAccount userAccount = UserAccountManager.getInstance().getUserAccount();
        tribeNameTextView.setText(userAccount.getTribe().getName());
        smallTribeIcon.setText(userAccount.getTribe().getSmallTribeLogoUri());
        largeTribeIcon.setText(userAccount.getTribe().getLargeTribeLogoUri());
        userNameTextView.setText(userAccount.getUsername());
        LayoutInflater inflater = LayoutInflater.from(this);

        if (userAccount.getBattleReports() != null && !userAccount.getBattleReports().isEmpty()) {
            completedGamesButton.setVisibility(View.VISIBLE);

            pastBattlesTextView.setText("" + userAccount.getBattleReports().size() + " completed battles");
        } else {
            completedGamesButton.setVisibility(View.GONE);
        }


        //TODO: this is a bad way to do it but must be replaced once custom is implemented anyways
        if ("http://landsofruin.com/app_assets/balean_small.png".equals(userAccount.getTribe().getSmallTribeLogoUri())) {
            baleanIcon.setSelected(true);
            reclaimerIcon.setSelected(false);
        } else if ("http://landsofruin.com/app_assets/reclaimers_small.png".equals(userAccount.getTribe().getSmallTribeLogoUri())) {
            baleanIcon.setSelected(false);
            reclaimerIcon.setSelected(true);
        } else {
            baleanIcon.setSelected(false);
            reclaimerIcon.setSelected(false);
        }


        characterContainer.removeAllViews();


        LinkedList<HeroBlueprint> availableHeroBlueprints = new LinkedList<>();
        for (HeroBlueprint blueprint : heroBlueprints) {

            if (LookupHelper.getInstance().getCharacterTypeFor(blueprint.getCharacterType()).getType() == CharacterType.TYPE_HERO) {
                if (userAccount.getAccountType() >= blueprint.getStatus()) {
                    availableHeroBlueprints.add(blueprint);
                }
            }
        }


        for (final HeroBlueprint blueprint : availableHeroBlueprints) {

            if (LookupHelper.getInstance().getCharacterTypeFor(blueprint.getCharacterType()).getType() == CharacterType.TYPE_HERO) {
                View characterView = inflater.inflate(R.layout.one_hero, characterContainer, false);

                boolean isSpawned = false;
                for (TribeCharacter tribeCharacter : userAccount.getTribe().getCharactersValues()) {
                    if (tribeCharacter.getBlueprintId().equals(blueprint.getId())) {


                        if (tribeCharacter.getSpawnedFromBlueprintVersion() < blueprint.getVersion()) {
                            characterView.findViewById(R.id.needs_update_text).setVisibility(View.VISIBLE);
                        } else {
                            characterView.findViewById(R.id.spawned_text).setVisibility(View.VISIBLE);
                        }
                        isSpawned = true;

                        break;
                    }
                }

                if (!isSpawned) {
                    characterView.findViewById(R.id.unlock_text).setVisibility(View.VISIBLE);
                }

                ((TextView) characterView.findViewById(R.id.name)).setText(blueprint.getName());
                Picasso.with(this).load(blueprint.getCardImageUrl()).into(((ImageView) characterView.findViewById(R.id.image)));


                characterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(YourTribeCharactersActivity.createIntent(YourTribeActivity.this, blueprint.getId()));
                    }
                });

                characterContainer.addView(characterView);
            }
        }


    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRef.child("user").child("heroblueprint").removeEventListener(heroBluePrintsValueListener);

        if (accountListener != null) {
            UserAccountManager.getInstance().removeAccountRefreshListener(accountListener);
        }
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

}
