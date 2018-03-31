package com.landsofruin.companion.tutorial;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.landsofruin.companion.CompanionApplication;
import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.client.ClientService;
import com.landsofruin.companion.net.event.ClientConnectedEvent;
import com.landsofruin.companion.net.event.ServerClosedConnectionEvent;
import com.landsofruin.companion.net.event.ServerStartedEvent;
import com.landsofruin.companion.net.server.ServerService;
import com.landsofruin.companion.provider.clients.GameClient;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

public class Tutorial1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial1);


        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                GameClient.loadGame("tutorial1", new GameClient.GameLoadedCallback() {
                    @Override
                    public void onGameLoaded(GameState gameState) {
                        for (PlayerState player : gameState.getPlayers()) {
                            player.setConnected(false);
                        }

                        GameClient.saveGame(gameState);

                        ServerService.start(Tutorial1Activity.this, gameState);
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveMode();
        BusProvider.getInstance().register(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onServerStarted(ServerStartedEvent event) {
        Log.d("server", "Server running for game " + event.getGameIdentifier());

        CompanionApplication.from(this).resetClientGame(event.getGameIdentifier(), event.getGameTitle());

        ClientService.connect(this, "127.0.0.1", event.getPort());
    }

    @Subscribe
    public void onClientConnected(ClientConnectedEvent event) {
        Intent intent = new Intent(this, GameSetupActivity.class);
        startActivity(intent);
    }


    @Subscribe
    public void onServerClosedConnection(ServerClosedConnectionEvent event) {
        Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT).show();

    }


}
