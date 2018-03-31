package com.landsofruin.companion;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.landsofruin.companion.adapter.NearbyGamesAdapter;
import com.landsofruin.companion.adapter.NearbyGamesAdapter.NearbyGameInfo;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.client.ClientService;
import com.landsofruin.companion.net.event.ClientConnectedEvent;
import com.landsofruin.companion.net.event.ServerClosedConnectionEvent;
import com.landsofruin.companion.net.event.ServerStartedEvent;
import com.landsofruin.companion.net.server.ServerService;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

public class NearbyGamesActivity extends FragmentActivity implements OnItemClickListener {

    private static final String TAG = "LoR/NearbyGamesActivity";
    private View connectingView;
    private NearbyGamesAdapter nearbyGamesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_games);


        nearbyGamesAdapter = new NearbyGamesAdapter();
        ListView nearbyGamesList = (ListView) findViewById(R.id.nearby_games_list);
        nearbyGamesList.setDividerHeight(0);
        nearbyGamesList.setAdapter(nearbyGamesAdapter);
        nearbyGamesList.setOnItemClickListener(this);

        connectingView = findViewById(R.id.overlay_connecting);
        connectingView.setVisibility(View.GONE);


        nearbyGamesAdapter.start();


        findViewById(R.id.back_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final EditText manualIP = (EditText) findViewById(R.id.manual_ip);
        final EditText manualPort = (EditText) findViewById(R.id.manual_port);

        findViewById(R.id.manual_connect).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                connectManual(manualIP.getText().toString(), Integer.parseInt(manualPort.getText().toString()));
            }
        });
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
    protected void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);


        Intent serverIntent = new Intent(this, ServerService.class);
        stopService(serverIntent);

        Intent clientIntent = new Intent(this, ClientService.class);
        stopService(clientIntent);
        setImmersiveMode();

    }


    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);

        nearbyGamesAdapter.stop();
    }

    @Subscribe
    public void onServerStarted(ServerStartedEvent event) {
        Log.d(TAG, "Server running for game " + event.getGameIdentifier());

        CompanionApplication.from(this).resetClientGame(event.getGameIdentifier(), event.getGameTitle());

        ClientService.connect(this, "127.0.0.1", event.getPort());
    }

    @Subscribe
    public void onClientConnected(ClientConnectedEvent event) {
        Intent intent = new Intent(this, GameSetupActivity.class);
        startActivity(intent);

        connectingView.setVisibility(View.GONE);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        NearbyGameInfo gameInfo = nearbyGamesAdapter.getItem(position);

        if (gameInfo.stateVersion != GameState.VERSION || gameInfo.appVersion != PlayerAccount.getAppVersion()) {
            Toast.makeText(this, "Server uses different app version", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!gameInfo.players.contains("|" + PlayerAccount.getUniqueIdentifier() + "|") && !gameInfo.isOpen) {
            Toast.makeText(this, "Game is not open", Toast.LENGTH_SHORT).show();
            return;
        }

        connectingView.setVisibility(View.VISIBLE);

        CompanionApplication.from(this).resetClientGame(gameInfo.gameIdentifier, gameInfo.gameTitle);

        ClientService.connect(this, gameInfo.hostname, gameInfo.port);

    }


    private void connectManual(String hostname, int port) {

        connectingView.setVisibility(View.VISIBLE);

        CompanionApplication.from(this).resetClientGame("", "");

        ClientService.connect(this, hostname, port);
    }

    @Subscribe
    public void onServerClosedConnection(ServerClosedConnectionEvent event) {
        Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT).show();

        connectingView.setVisibility(View.GONE);
    }


}
