package com.landsofruin.companion.net.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.landsofruin.companion.CompanionApplication;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.ServerClosedConnectionEvent;
import com.landsofruin.companion.state.transition.Transition;

import java.io.IOException;

/**
 * Service handling the connection to the server.
 */
public class ClientService extends Service {
    private static final String TAG = "LoR/ClientService";
    private static final String EXTRA_HOSTNAME = "hostname";
    private static final String EXTRA_PORT = "port";

    private ClientThread clientThread;

    public class ClientBinder extends Binder {
        public ClientService getService() {
            return ClientService.this;
        }
    }

    public static void connect(Context context, String hostname, int port) {
        Intent intent = new Intent(context, ClientService.class);
        intent.putExtra(EXTRA_HOSTNAME, hostname);
        intent.putExtra(EXTRA_PORT, port);

        context.startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (clientThread != null) {
            clientThread.destroy();
            clientThread = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            // Service has been restarted. No Intent has been provided: Can't
            // recover.
            stopSelf();
            return START_NOT_STICKY;
        }

        if (clientThread != null && clientThread.isRunning()) {
            Toast.makeText(this, "Already connected to another game",
                    Toast.LENGTH_SHORT).show();
            return START_STICKY;
        }

        clientThread = new ClientThread(this);

        Log.d(TAG, "Incoming intent");

        Bundle extras = intent.getExtras();

        clientThread.setServer(extras.getString(EXTRA_HOSTNAME), extras.getInt(EXTRA_PORT));

        clientThread.start();

        return START_STICKY;
    }

    public void sendToServer(Transition transition) {
        if (clientThread == null) {
            Log.e(TAG, "Client Thread is null. This will cause a disconnect.");
            Toast.makeText(getApplicationContext(), "Problem with connection. Please resume the game to reconnect.", Toast.LENGTH_LONG).show();

            onDisconnected();
            return;
        }

        try {
            clientThread.getTransitionWriter().write(transition);
        } catch (IOException exception) {
            Log.w(TAG, "IOException during sending transition to server",
                    exception);

            onDisconnected();
        }
    }

    public void onDisconnected() {
        if (clientThread != null) {
            if (clientThread.isRunning()) {
                CompanionApplication.getInstance().removeClientGame();

                BusProvider.postOnMainThread(new ServerClosedConnectionEvent());
            }
            if (clientThread != null) {
                clientThread.destroy();
            }
        }

        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ClientBinder();
    }
}
