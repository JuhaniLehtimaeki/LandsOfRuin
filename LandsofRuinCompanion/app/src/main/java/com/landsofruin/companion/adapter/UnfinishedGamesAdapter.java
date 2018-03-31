package com.landsofruin.companion.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.landsofruin.companion.provider.clients.GameClient;
import com.landsofruin.companion.provider.clients.GameClient.GameInfo;
import com.landsofruin.gametracker.R;

import java.util.List;

/**
 * Adapter for listing previous games.
 */
public class UnfinishedGamesAdapter extends BaseAdapter {
    private Context context;
    private List<GameInfo> games;
    private String account;

    public UnfinishedGamesAdapter(Context context, String account) {
        this.context = context;
        this.games = GameClient.retrieveUnfinishedGames(context, account);
        this.account = account;
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public GameInfo getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return games.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_previous_game, parent, false);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.game_title);
        TextView lastUpdateView = (TextView) convertView.findViewById(R.id.last_update);

        GameInfo info = getItem(position);

        titleView.setText(info.title);
        lastUpdateView.setText(DateUtils.getRelativeTimeSpanString(info.updatedAt) + (info.isFinished() ? " (ended)" : ""));

        return convertView;
    }
}
