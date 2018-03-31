package com.landsofruin.companion.net.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.landsofruin.companion.CompanionApplication;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.ServerStartedEvent;
import com.landsofruin.companion.provider.clients.GameClient;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.DisconnectTransition;
import com.landsofruin.companion.state.transition.PongTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.BuildConfig;

import java.io.IOException;
import java.util.Iterator;

/**
 * Service running on the client hosting the game.
 */
public class ServerService extends Service {
    private static final String TAG = "LoR/ServerService";
    private static final String EXTRA_GAME_IDENTIFIER = "game_identifier";

    private GameState gameState;
    private TransmitterThread transmitterThread;
    private ConnectionThread connectionThread;
    private KeepAliveThread keepAliveThread;

    public static void start(Context context, GameState gameState) {
        Intent intent = new Intent(context, ServerService.class);

        intent.putExtra(EXTRA_GAME_IDENTIFIER, gameState.getFirebaseIdentifier());

        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        connectionThread = new ConnectionThread(this);
        transmitterThread = new TransmitterThread();
        keepAliveThread = new KeepAliveThread(connectionThread);

        Log.d(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CompanionApplication.getInstance().setHostingGameIdentifier(null);

        keepAliveThread.destroy();
        connectionThread.destroy();
        transmitterThread.destroy();

        Log.d(TAG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.d(TAG, "ServerService started without an intent ... stopping");
            stopSelf();
            return START_NOT_STICKY;
        }

        String localIdentifier = intent.getStringExtra(EXTRA_GAME_IDENTIFIER);

        GameClient.loadGame(localIdentifier, new GameClient.GameLoadedCallback() {
            @Override
            public void onGameLoaded(GameState gameState_) {
                gameState = gameState_;

                CompanionApplication.getInstance().setHostingGameIdentifier(gameState.getIdentifier());

                transmitterThread.setGame(gameState);
                transmitterThread.setPort(connectionThread.getPort());
                transmitterThread.start();

                connectionThread.start();
                keepAliveThread.start();

                BusProvider.getInstance().post(new ServerStartedEvent(gameState, connectionThread.getPort()));

                Log.d(TAG, "Hosting game " + gameState.getIdentifier());
            }
        });


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * On transition received from client:
     * <p>
     * - Trigger transition
     * - Propagate transition to clients
     * - Persist new game state to disk
     * - Persist transition for sync (if needed)
     */
    public synchronized void onTransitionReceived(ServerThread origin, Transition transition) {
        transition.triggerServer(origin, gameState);

        if (transition instanceof PongTransition) {
            // Should not be propagated back to clients..
            return;
        }
        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "[SERVER] Transition received: " + transition.getClass().getSimpleName());
        }
        dispatchTransitionToClients(transition);

        GameClient.saveGame(gameState);
    }

    private void dispatchTransitionToClients(Transition transition) {
        Iterator<ServerThread> iterator = connectionThread.getThreadsIterator();
        while (iterator.hasNext()) {
            ServerThread thread = iterator.next();

            if (thread.isConnected()) {
                try {
                    thread.getTransitionWriter().write(transition);
                } catch (IOException exception) {
                    thread.destroy();
                }
            } else {
                thread.destroy();
                //FIXME: this causes concurrent crash problems 
                iterator.remove();
            }
        }
    }


    public void onThreadDestroyed(ServerThread thread) {
        if (thread.hasPlayerIdentifier()) {
            DisconnectTransition transition = new DisconnectTransition(thread.getPlayerIdentifier());
            onTransitionReceived(thread, transition);
        } else {
            Log.d(TAG, "Thread without identifier disconnected");
        }
    }
}
