package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.UpdateGameTitleTransition;

public class EditGameTitleDialogFragment extends DialogFragment {
    public static final String ARGUMENT_TITLE = "title";

    private GameSetupActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (GameSetupActivity) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Title");

        final EditText titleView = new EditText(activity);
        titleView.setText(getArguments().getString(ARGUMENT_TITLE));
        builder.setView(titleView);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = titleView.getText().toString();

                Transition transition = new UpdateGameTitleTransition(title);
                activity.sendToServer(transition);

                dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }
}
