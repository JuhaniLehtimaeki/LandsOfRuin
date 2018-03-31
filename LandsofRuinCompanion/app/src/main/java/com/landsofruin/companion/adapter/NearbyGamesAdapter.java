package com.landsofruin.companion.adapter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidzeitgeist.ani.discovery.Discovery;
import com.androidzeitgeist.ani.discovery.DiscoveryException;
import com.androidzeitgeist.ani.discovery.DiscoveryListener;
import com.landsofruin.companion.net.DiscoveryConstants;
import com.landsofruin.gametracker.R;

import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Adapter for listing nearby games (discovery).
 */
public class NearbyGamesAdapter extends BaseAdapter implements DiscoveryListener, Runnable {
    private static final String TAG = "LoR/NearbyGamesAdapter";

    public static class NearbyGameInfo {
        public String gameIdentifier;
        public String gameTitle;
        public String hostname;
        public String playerName;
        public int port;
        public long seenAt;
        private String device;
        public int appVersion;
        public int stateVersion;
        public String players;
        public boolean isOpen;
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    private DateFormat timeFormat;
    private List<NearbyGameInfo> games;
    private Discovery discovery;
    private UpdateThread updateThread;

    public NearbyGamesAdapter() {
        games = new ArrayList<>();
        timeFormat = DateFormat.getTimeInstance();

        discovery = new Discovery();
        discovery.setDisoveryListener(this);
    }

    public void start() {

        if (updateThread != null) {
            // already running
            return;
        }

        try {
            discovery.enable();
        } catch (DiscoveryException exception) {
            Log.e(TAG, "Discovery failed", exception);
        }

        updateThread = new UpdateThread();
        updateThread.start();
    }

    public void stop() {

        if (updateThread != null) {

            discovery.disable();
            updateThread.destroy();
            updateThread = null;
        }
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public NearbyGameInfo getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_game, parent, false);
        }

        TextView gameTitleView = (TextView) convertView.findViewById(R.id.game_title);
        TextView hostAndPortView = (TextView) convertView.findViewById(R.id.hostname_and_port);
        TextView lastSeenView = (TextView) convertView.findViewById(R.id.last_seen);
        TextView playerView = (TextView) convertView.findViewById(R.id.player);
        View statusView = convertView.findViewById(R.id.open_indicator);

        NearbyGameInfo info = getItem(position);

        gameTitleView.setText(info.gameTitle);
        hostAndPortView.setText(info.hostname + ":" + info.port);
        lastSeenView.setText(
                timeFormat.format(new Date(info.seenAt))
        );
        playerView.setText(info.playerName + " (" + info.device + ")");
        statusView.setBackgroundColor(info.isOpen ? 0xFF33691E : 0xFFB71C1C);

        return convertView;
    }

    public synchronized void updateNearbyGames() {
        Iterator<NearbyGameInfo> iterator = games.iterator();

        while (iterator.hasNext()) {
            NearbyGameInfo info = iterator.next();

            if (info.seenAt + 20000 < System.currentTimeMillis()) {
                iterator.remove();

                handler.post(this);
            }
        }
    }

    @Override
    public synchronized void onIntentDiscovered(InetAddress source, Intent intent) {
        if (!DiscoveryConstants.ACTION_GAME.equals(intent.getAction())) {
            return; // This Intent is not for us.
        }

        String gameIdentifier = intent.getStringExtra(DiscoveryConstants.EXTRA_GAME_IDENTIFIER);

        NearbyGameInfo gameInfo = null;
        for (NearbyGameInfo currentGameInfo : games) {
            if (currentGameInfo.gameIdentifier.equals(gameIdentifier)) {
                gameInfo = currentGameInfo;
                break;
            }
        }

        if (gameInfo == null) {
            gameInfo = new NearbyGameInfo();
            gameInfo.gameIdentifier = gameIdentifier;
            games.add(gameInfo);
        }

        gameInfo.gameTitle = intent.getStringExtra(DiscoveryConstants.EXTRA_GAME_TITLE);
        gameInfo.device = intent.getStringExtra(DiscoveryConstants.EXTRA_DEVICE);
        gameInfo.hostname = source.getHostAddress();
        gameInfo.port = intent.getIntExtra(DiscoveryConstants.EXTRA_PORT, 0);
        gameInfo.seenAt = System.currentTimeMillis();
        gameInfo.playerName = intent.getStringExtra(DiscoveryConstants.EXTRA_PLAYER);
        gameInfo.players = intent.getStringExtra(DiscoveryConstants.EXTRA_PLAYERS);
        gameInfo.isOpen = intent.getBooleanExtra(DiscoveryConstants.EXTRA_IS_OPEN, false);
        gameInfo.appVersion = intent.getIntExtra(DiscoveryConstants.EXTRA_APP_VERSION, 0);
        gameInfo.stateVersion = intent.getIntExtra(DiscoveryConstants.EXTRA_STATE_VERSION, 0);

        handler.post(this);
    }

    public void run() {
        notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryError(Exception exception) {
        Log.v(TAG, "Discovery error", exception);
    }

    @Override
    public void onDiscoveryStarted() {
        // Not needed

        Log.d(TAG, "Discovery started");
    }

    @Override
    public void onDiscoveryStopped() {
        // Not needed

        Log.d(TAG, "Discovery stopped");
    }

    private class UpdateThread extends Thread {
        private boolean isRunning = true;

        public void run() {
            while (isRunning) {
                updateNearbyGames();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }

        public void destroy() {
            isRunning = false;

            interrupt();
        }
    }
}
