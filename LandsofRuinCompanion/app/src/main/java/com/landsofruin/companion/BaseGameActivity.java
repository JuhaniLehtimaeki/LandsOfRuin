package com.landsofruin.companion;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.landsofruin.companion.net.client.ClientService;
import com.landsofruin.companion.net.client.ClientService.ClientBinder;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.Transition;

public abstract class BaseGameActivity extends FragmentActivity implements ServiceConnection {
    private GameState gameState;
    private ClientService client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameState = ((CompanionApplication) getApplication()).getClientGame();

        if (gameState == null) {
            Log.e("main activity", "No Game state", new RuntimeException("There's no game attached to application"));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent clientService = new Intent(this, ClientService.class);
        bindService(clientService, this, 0);

    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(this);
    }

    public GameState getGame() {
        return gameState;
    }

    public boolean hasGame() {
        return ((CompanionApplication) getApplication()).hasClientGame();
    }

    public boolean isAdmin() {
        if (gameState == null) {
            return false;
        }

        return CompanionApplication.from(this).isHostingGame(gameState.getIdentifier());
    }

    public synchronized void sendToServer(final Transition transition) {
        if (client != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.sendToServer(transition);
                }
            }).start();

        } else {
            Toast.makeText(this, "No client service running", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        client = ((ClientBinder) binder).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        client = null;
    }
}
