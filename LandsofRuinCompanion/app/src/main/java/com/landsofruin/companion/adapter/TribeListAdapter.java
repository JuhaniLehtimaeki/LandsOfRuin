package com.landsofruin.companion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.provider.clients.TribeClient;
import com.landsofruin.companion.provider.snapshots.TribeSnapshot;
import com.landsofruin.gametracker.R;

import java.util.List;

public class TribeListAdapter extends BaseAdapter {
    private List<TribeSnapshot> tribes;

    public TribeListAdapter(Context context) {
        tribes = TribeClient.getTribes(context.getContentResolver(), PlayerAccount.getPlayerName());
    }

    @Override
    public int getCount() {
        return tribes.size();
    }

    @Override
    public TribeSnapshot getItem(int position) {
        return tribes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tribe, parent, false);
        }

        TribeSnapshot tribe = getItem(position);

        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        nameView.setText(tribe.getName());

        return convertView;
    }
}
