package com.landsofruin.gametracker.actionsui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actions.ActionManager;
import com.landsofruin.gametracker.actionsui.ActionViewUtil.ActionPerformPressedCallback;

public class ActionsSelfUIDialogFragment extends Fragment {
    private static final String ARGUMENT_PC_ID = "ARGUMENT_PC_ID";
    private CharacterState character;
    private Action action;
    private ActionPerformPressedCallback callback;

    public static ActionsSelfUIDialogFragment newInstance(Action action,
                                                          CharacterState character) {
        ActionsSelfUIDialogFragment f = new ActionsSelfUIDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PC_ID, character.getIdentifier());
        args.putInt("action", action.getId());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        action = ActionManager.getInstance().getActionForId(
                getArguments().getInt("action"));

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        this.character = ((BaseGameActivity) activity).getGame().findCharacterByIdentifier(getArguments()
                .getString(ARGUMENT_PC_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.action_fragment, container, false);

        TextView actionButton = (TextView) view.findViewById(R.id.action_button);


        view.findViewById(R.id.action_button_container).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callback.actionPerformPressed(null, null, null);
                    }
                });

        actionButton.setText(action.getName());


        view.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.actionCancelled();
            }
        });


        ((TextView) view.findViewById(R.id.description)).setText(action.getDescription());

        return view;
    }

    public void addCallback(ActionPerformPressedCallback callback) {
        this.callback = callback;
    }
}
