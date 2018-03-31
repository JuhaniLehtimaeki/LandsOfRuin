package com.landsofruin.companion.net.server;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.androidzeitgeist.ani.transmitter.Transmitter;
import com.androidzeitgeist.ani.transmitter.TransmitterException;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.net.DiscoveryConstants;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.SecondaryPhase;

/**
 * Thread for constantly transmitting game information for discovery.
 */
public class TransmitterThread extends Thread {
    private static final String TAG = "LoR/TransmitterThread";
    private static final int TIME_BETWEEN_TRANSMISSIONS = 3000;

    private GameState gameState;
    private int port;
    private boolean isRunning;

    public TransmitterThread() {
        setName(TAG);

        this.isRunning = true;
    }

    public void setGame(GameState gameState) {
        this.gameState = gameState;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void destroy() {
        isRunning = false;

        interrupt();
    }

    @Override
    public void run() {
        Transmitter transmitter = new Transmitter();
        Intent intent = createDiscoveryIntent();

        Log.d(TAG, "Transmitting game to network");

        while (isRunning) {
            updateDiscoveryIntent(intent);
            transmitIntent(transmitter, intent);
            waitForNextTransmission();
        }

        Log.d(TAG, "Thread finished");
    }

    private Intent createDiscoveryIntent() {
        Intent intent = new Intent(DiscoveryConstants.ACTION_GAME);

        intent.putExtra(DiscoveryConstants.EXTRA_GAME_IDENTIFIER, gameState.getIdentifier());
        intent.putExtra(DiscoveryConstants.EXTRA_GAME_TITLE, gameState.getTitle());
        intent.putExtra(DiscoveryConstants.EXTRA_PORT, port);
        intent.putExtra(DiscoveryConstants.EXTRA_DEVICE, Build.MODEL);
        intent.putExtra(DiscoveryConstants.EXTRA_PLAYER, PlayerAccount.getPlayerName());
        intent.putExtra(DiscoveryConstants.EXTRA_APP_VERSION, PlayerAccount.getAppVersion());
        intent.putExtra(DiscoveryConstants.EXTRA_STATE_VERSION, gameState.getVersion());

        return intent;
    }

    private void updateDiscoveryIntent(Intent intent) {
        StringBuilder builder = new StringBuilder("|");

        for (PlayerState player : gameState.getPlayers()) {
            builder.append(player.getIdentifier());
            builder.append("|");
        }
        intent.putExtra(DiscoveryConstants.EXTRA_GAME_TITLE, gameState.getTitle());
        intent.putExtra(DiscoveryConstants.EXTRA_PLAYERS, builder.toString());
        intent.putExtra(DiscoveryConstants.EXTRA_IS_OPEN, gameState.getPhase().getSecondaryPhase() == SecondaryPhase.SETUP_PLAYERS);
    }

    private void transmitIntent(Transmitter transmitter, Intent intent) {
        try {
            transmitter.transmit(intent);
        } catch (TransmitterException exception) {
            Log.v(TAG, "Exception during transmitting discovery Intent", exception);
        }
    }

    private void waitForNextTransmission() {
        try {
            Thread.sleep(TIME_BETWEEN_TRANSMISSIONS);
        } catch (InterruptedException exception) {
            // Ignore exception
        }
    }
}
