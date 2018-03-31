package com.landsofruin.companion.net.client;

import android.util.Log;

import com.landsofruin.companion.CompanionApplication;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.NetworkConstants;
import com.landsofruin.companion.net.TransitionReader;
import com.landsofruin.companion.net.TransitionWriter;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.provider.clients.GameClient;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.transition.ConnectTransition;
import com.landsofruin.companion.state.transition.PingTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.gametracker.BuildConfig;

import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread implements ClientThreadInterface {
    private static final String TAG = "LoR/ClientThread";

    private ClientService service;
    private Socket socket;
    private String hostname;
    private int port;
    private boolean isRunning;
    private GameState gameState;
    private TransitionWriter writer;

    public ClientThread(ClientService service) {
        setName(TAG);

        this.service = service;

        isRunning = true;
        gameState = CompanionApplication.getInstance().getClientGame();
    }

    public TransitionWriter getTransitionWriter() {
        return writer;
    }

    public void destroy() {
        isRunning = false;

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException exception) {
                // Ignore
            }
        }

        interrupt();
    }

    public void setServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        try {
            connect();
            sendConnectTransition();

            readTransitions();
        } catch (IOException exception) {
            Log.w(TAG, "IOException during connecting to " + hostname + ":" + port);
        }

        service.onDisconnected();

        Log.d(TAG, "Thread finished");
    }

    private void connect() throws IOException {
        socket = new Socket(hostname, port);
        socket.setSoTimeout(NetworkConstants.READ_TIMEOUT);

        Log.d(TAG, "Connected to " + hostname + ":" + port);

        writer = new TransitionWriter(socket.getOutputStream());
    }

    private void sendConnectTransition() throws IOException {
        PlayerState playerState = new PlayerState(PlayerAccount.getUniqueIdentifier(), PlayerAccount.getPlayerScreenName());

        ConnectTransition connectTransition = new ConnectTransition(playerState);
        writer.write(connectTransition);
    }

    private void readTransitions() throws IOException {
        TransitionReader reader = new TransitionReader(socket.getInputStream());
        Transition transition;

        while (isRunning && (transition = reader.readTransition()) != null) {
            transition.triggerClient(this, gameState);
            // Don't let PingTransition objects update the UI
            if (transition instanceof PingTransition) {
                continue;
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "[CLIENT] Transition received: " + transition.getClass().getSimpleName());
            }
            GameClient.saveOrCreateGameInfo(gameState);

            BusProvider.postOnMainThread(new GameStateChangedEvent());
        }
    }

    @Override
    public void write(Transition transition) {
        try {
            getTransitionWriter().write(transition);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("client", "write failed", e);
        }
    }
}
