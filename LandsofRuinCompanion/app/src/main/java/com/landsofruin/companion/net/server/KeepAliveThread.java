package com.landsofruin.companion.net.server;

import android.util.Log;

import com.landsofruin.companion.net.NetworkConstants;
import com.landsofruin.companion.state.transition.PingTransition;

import java.io.IOException;
import java.util.List;

public class KeepAliveThread extends Thread {
    private static final String TAG = "LoR/KeepAliveThread";
    private ConnectionThread connectionThread;
    private boolean isRunning;

    public KeepAliveThread(ConnectionThread connectionThread) {
        this.connectionThread = connectionThread;
        this.isRunning = true;
    }

    public void destroy() {
        isRunning = false;

        interrupt();
    }

    @Override
    public void run() {
        while (isRunning) {
            sendPingTransitionToClients();

            try {
                Thread.sleep(NetworkConstants.PING_INTERVAL);
            } catch (InterruptedException exception) {
                // Ignore exception
            }
        }

        Log.d(TAG, "Thread finished");
    }

    private void sendPingTransitionToClients() {
        List<ServerThread> threads = connectionThread.getChildThreads();
        int size = threads.size();

        for (int i = 0; i < size; i++) {
            try {
                ServerThread thread = threads.get(i);

                thread.getTransitionWriter().write(new PingTransition());
            } catch (IndexOutOfBoundsException exception) {
                // List has been modified while iterating.. Ignore
                break;
            } catch (IOException e) {
                // Not important for keep alive, just ignore
            }
        }
    }
}
