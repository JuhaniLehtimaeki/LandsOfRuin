package com.landsofruin.companion.net.server;

import android.util.Log;

import com.landsofruin.companion.net.NetworkConstants;
import com.landsofruin.companion.net.TransitionReader;
import com.landsofruin.companion.net.TransitionWriter;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

import java.io.IOException;
import java.net.Socket;

/**
 * Thread for reading {@link Transition} objects from the clients and passing them
 * back to the service.
 */
public class ServerThread extends Thread implements ServerThreadInterface{
    private static final String TAG = "LoR/ServerThread";

    private ServerService service;
    private Socket socket;
    private String playerIdentifier;

    private TransitionWriter writer;
    private TransitionReader reader;

    private boolean running;

    public ServerThread(ServerService service, Socket socket) throws IOException {
        setName(TAG);

        socket.setSoTimeout(NetworkConstants.READ_TIMEOUT);

        this.service = service;
        this.socket = socket;

        reader = new TransitionReader(socket.getInputStream());
        writer = new TransitionWriter(socket.getOutputStream());

        running = true;
    }

    public String getPlayerIdentifier() {
        return playerIdentifier;
    }

    public boolean hasPlayerIdentifier() {
        return playerIdentifier != null;
    }

    public void setPlayerIdentifier(String playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
    }

    @Override
    public void write(Transition transition) {
        try {
            getTransitionWriter().write(transition);
        } catch (IOException e) {
            throw new RuntimeException("Fix me!");
        }
    }

    public TransitionWriter getTransitionWriter() {
        return writer;
    }

    public boolean isConnected() {
        return socket.isConnected() && running;
    }

    public void destroy() {
        if (!running) {
            return;
        }

        running = false;

        try {
            socket.close();
        } catch (IOException exception) {
            // Ignore
        }

        service.onThreadDestroyed(this);

        interrupt();
    }

    @Override
    public void run() {
        Transition transition;

        try {
            while (running && (transition = reader.readTransition()) != null) {
                service.onTransitionReceived(this, transition);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException during reading transitions", exception);
        }

        destroy();
    }
}
