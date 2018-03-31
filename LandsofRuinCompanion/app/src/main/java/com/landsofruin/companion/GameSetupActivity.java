package com.landsofruin.companion;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.fragment.GameSetupMapSelectFragment;
import com.landsofruin.companion.fragment.GameSetupPlayersLobbyFragment;
import com.landsofruin.companion.fragment.GameSetupReviewLastPageFragment;
import com.landsofruin.companion.fragment.GameSetupScenarioRoleSelectFragment;
import com.landsofruin.companion.fragment.GameSetupScenarioSelectFragment;
import com.landsofruin.companion.fragment.GameSetupTableLocationSelectFragment;
import com.landsofruin.companion.fragment.GameSetupTeamSelectFirebaseFragment;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.SecondaryPhase;
import com.landsofruin.companion.state.transition.SetupStepBackTransition;
import com.landsofruin.companion.tribemanagement.UserAccount;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

/**
 * Activity for the game setup phase. Only the "admin" (hosting player) can set
 * most of the options.
 */
public class GameSetupActivity extends BaseGameActivity {

    private int showingPhase = -1;


    private ValueEventListener userAccountValueListener;
    private UserAccount userAccount;
    private DatabaseReference firebaseRef;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // check the state of the current game.
        GameState game = getGame();

        if (game == null) {
            finish();
            return;
        }

        if (game.getPhase().getPrimaryPhase() != PrimaryPhase.GAME_SETUP) {
            goToGame();
            return;
        }


        setContentView(R.layout.activity_game_setup);


        updateUI();
    }


    private void goToGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        this.finish();
    }


    private void updateUI() {
        int secondaryPhase = getGame().getPhase().getSecondaryPhase();

        if (secondaryPhase != this.showingPhase) {

            this.showingPhase = secondaryPhase;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (secondaryPhase) {

                case SecondaryPhase.SETUP_PLAYERS:
                    transaction.replace(R.id.setup_details_container, GameSetupPlayersLobbyFragment.newInstance());
                    break;

                case SecondaryPhase.SETUP_SCENARIO_SELECT:
                    transaction.replace(R.id.setup_details_container, GameSetupScenarioSelectFragment.newInstance());
                    break;

                case SecondaryPhase.SETUP_SCENARIO_ROLES:
                    transaction.replace(R.id.setup_details_container, GameSetupScenarioRoleSelectFragment.newInstance());
                    break;

                case SecondaryPhase.SETUP_MAP_SELECT:
                    transaction.replace(R.id.setup_details_container, GameSetupMapSelectFragment.newInstance());
                    break;

                case SecondaryPhase.SETUP_TABLE:
                    transaction.replace(R.id.setup_details_container, GameSetupTableLocationSelectFragment.newInstance());

                    break;
                case SecondaryPhase.SETUP_TEAM:
                    transaction.replace(R.id.setup_details_container, GameSetupTeamSelectFirebaseFragment.newInstance());

                    break;
                case SecondaryPhase.SETUP_OVERVIEW:
                    transaction.replace(R.id.setup_details_container, GameSetupReviewLastPageFragment.newInstance());

                    break;

            }

            transaction.commit();

        }
        firebaseRef = FirebaseDatabase.getInstance().getReference();

        userAccountValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userAccount = dataSnapshot.getValue(UserAccount.class);

                if (userAccount.getTribe() == null) {
                    Log.e("game setup", "User's tribe is not setup yet. exitting");
                    Toast.makeText(GameSetupActivity.this, "Please complete your tribe setup first", Toast.LENGTH_LONG).show();
                    finish();
                }
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(userAccountValueListener);

    }


    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        setImmersiveMode();
    }

    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (isFinishing()) {
            return; // Ignore further events
        }

        GameState game = getGame();
        if (game.getPhase().getPrimaryPhase() != PrimaryPhase.GAME_SETUP) {
            goToGame();
            return;
        }

        updateUI();


    }


    @Override
    public void onBackPressed() {


        if (getGame().getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_PLAYERS) {
            super.onBackPressed();
        } else {
            sendToServer(new SetupStepBackTransition());

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseRef != null) {
            firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(userAccountValueListener);
        }
    }
}
