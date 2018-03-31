package com.landsofruin.companion.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.landsofruin.companion.GameLobbyActivity;
import com.landsofruin.companion.adapter.UnfinishedGamesAdapter;
import com.landsofruin.companion.device.PlayerAccount;

public class UnfinishedGamesDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private UnfinishedGamesAdapter adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adapter = new UnfinishedGamesAdapter(getActivity(), PlayerAccount.getPlayerName());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select game");
        builder.setAdapter(adapter, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int position) {
        GameLobbyActivity activity = (GameLobbyActivity) getActivity();

        activity.onHostPreviousGame(adapter.getItem(position).identifier);

        dismiss();
    }
}
