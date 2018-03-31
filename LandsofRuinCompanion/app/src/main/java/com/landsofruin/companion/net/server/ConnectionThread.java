package com.landsofruin.companion.net.server;

import android.util.Log;

import com.landsofruin.companion.utils.NetworkUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Thread waiting for incoming connections. Creating a new thread
 * for every new connection.
 */
public class ConnectionThread extends Thread {
    private static final String TAG = "LoR/ConnectionThread";

    private ServerSocket serverSocket;
    private List<ServerThread> threads;
    private ServerService service;

    private boolean isRunning;

    public ConnectionThread(ServerService service) {
        setName(TAG);

        this.service = service;

        threads = new LinkedList<>();
        isRunning = true;

        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException exception) {
            Log.e(TAG, "IOException during creating ServerSocket", exception);

            isRunning = false;
        }
    }

    public Iterator<ServerThread> getThreadsIterator() {
        return threads.iterator();
    }

    public List<ServerThread> getChildThreads() {
        return threads;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void destroy() {
        isRunning = false;

        try {
            serverSocket.close();
        } catch (IOException exception) {
            // Ignore
        }

        int size = threads.size();

        for (int i = 0; i < size; i++) {
            try {
                ServerThread thread = threads.get(i);
                thread.destroy();
            } catch (IndexOutOfBoundsException exception) {
                // List has been modified while iterating.. Ignore
                break;
            }
        }

        interrupt();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Log.d(TAG, "Waiting for incoming connections on port " + serverSocket.getLocalPort());

                NetworkUtils.lastPort = serverSocket.getLocalPort();

                Socket socket = serverSocket.accept();

                Log.d(TAG, "Connection from: " + socket.getInetAddress().toString());

                ServerThread thread = new ServerThread(service, socket);
                thread.start();

                threads.add(thread);
            } catch (IOException exception) {
                Log.w(TAG, "IOException during creating worker thread", exception);
            }
        }

        Log.d(TAG, "Thread finished");
    }
}
