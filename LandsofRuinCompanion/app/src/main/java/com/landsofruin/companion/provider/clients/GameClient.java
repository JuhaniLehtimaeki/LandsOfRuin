package com.landsofruin.companion.provider.clients;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.provider.contracts.GameContract;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.user.UserAccountManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Client class to manage the character data in the wastelands content provider.
 */
public class GameClient {


    public static GameState createGame() {
        GameState state = new GameState(UUID.randomUUID().toString(), "New Game");
        if (state.getFirebaseIdentifier() == null) {
            state.setFirebaseIdentifier(FirebaseDatabase.getInstance().getReference().child("savedgames").push().getKey());
        }
        FirebaseDatabase.getInstance().getReference().child("savedgames").child(state.getFirebaseIdentifier()).setValue(state);


        return state;
    }

    public static void loadGame(String identifier, final GameLoadedCallback gameLoadedCallback) {

        if (identifier.startsWith("tutorial")) {
            FirebaseDatabase.getInstance().getReference().child("tutorials").child(identifier).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GameState ret = dataSnapshot.getValue(GameState.class);
                    gameLoadedCallback.onGameLoaded(ret);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference().child("savedgames").child(identifier).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GameState ret = dataSnapshot.getValue(GameState.class);
                    gameLoadedCallback.onGameLoaded(ret);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }


    public static void saveOrCreateGameInfo(GameState state) {


        if (state.isTutorial()) {
            return;
        }

        if (state.getFirebaseIdentifier() == null) {
            Log.e("save game", "Cannot store game. The gamestate is missing a firebase ID");
            return;
        }

        GameInfo gameInfo = UserAccountManager.getInstance().getUserAccount().findGameInfoWithId(state.getFirebaseIdentifier());

        if (gameInfo == null) {
            gameInfo = new GameInfo();
            gameInfo.identifier = state.getFirebaseIdentifier();
            gameInfo.globalState = GameContract._GLOBAL_STATES.SETUP;
            long timestamp = System.currentTimeMillis();
            gameInfo.createdAt = timestamp;
            gameInfo.title = state.getTitle();

            UserAccountManager.getInstance().getUserAccount().addNewGame(gameInfo);

        } else {
            GameContract._GLOBAL_STATES globalState = GameContract._GLOBAL_STATES.RUNNING;

            if (state.getPhase().getPrimaryPhase() == PrimaryPhase.GAME_SETUP) {
                globalState = GameContract._GLOBAL_STATES.SETUP;
            }

            if (state.getPhase().getPrimaryPhase() == PrimaryPhase.GAME_END) {
                globalState = GameContract._GLOBAL_STATES.FINISHED;
            }
            gameInfo.globalState = globalState;
        }


        gameInfo.updatedAt = System.currentTimeMillis();
        gameInfo.title = state.getTitle();

        UserAccountManager.getInstance().saveUserAccount();

    }


    public static void saveGame(GameState state) {
        if (state.isTutorial()) {
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("savedgames").child(state.getFirebaseIdentifier()).setValue(state);
    }

    public static boolean hasUnfinishedGames(Context context, String account) {
        return retrieveUnfinishedGames(context, account).size() > 0;
    }

    public static List<GameInfo> retrieveUnfinishedGames(Context context, String account) {


        ArrayList<GameInfo> ret = new ArrayList<>();

        if (UserAccountManager.getInstance().getUserAccount() == null) {
            return ret;
        }

        for (GameInfo gameInfo : UserAccountManager.getInstance().getUserAccount().getGames()) {
            if (gameInfo.globalState == GameContract._GLOBAL_STATES.FINISHED) {
                continue;
            }

            ret.add(gameInfo);
        }


        return ret;


    }


    public static class GameInfo {
        public String identifier;
        public String title;
        public long createdAt;
        public long updatedAt;
        public boolean isOnline;
        public GameContract._GLOBAL_STATES globalState;

        public boolean isFinished() {
            return globalState == GameContract._GLOBAL_STATES.FINISHED;
        }
    }


    public interface GameLoadedCallback {
        void onGameLoaded(GameState gameState);
    }
}
