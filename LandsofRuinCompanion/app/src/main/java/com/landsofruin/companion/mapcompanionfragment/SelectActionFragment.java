package com.landsofruin.companion.mapcompanionfragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.icons.IconConstantsHelper;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actionsui.ActionViewContainer;

import java.util.List;

public class SelectActionFragment extends Fragment {

    private ActionSelectedCallback callback;
    private List<ActionViewContainer> actions;
    private ViewGroup actionsContainer;
    private String actionTitle;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.select_actions_fragment, container,
                false);


        if (actions == null) {
            return v;
        }

        ((TextView) v.findViewById(R.id.action_title)).setText(actionTitle);

        this.actionsContainer = (ViewGroup) v
                .findViewById(R.id.action_selection_container);

        this.actionsContainer.removeAllViews();

        for (final ActionViewContainer actionViewContainer : actions) {

            View actionButton = inflater.inflate(R.layout.one_select_action,
                    this.actionsContainer, false);


            Action action = LookupHelper.getInstance().getActionFor(actionViewContainer.getActionID());

            ((TextView) actionButton.findViewById(R.id.title)).setText(actionViewContainer.getActionButtonText());

            ((ImageView) actionButton.findViewById(R.id.action_icon)).setImageResource(IconConstantsHelper.getInstance().getIconResourceFor(IconConstantsHelper.getInstance().getIconIdForAction(actionViewContainer.getActionID())));

            ((TextView) actionButton.findViewById(R.id.description)).setText(action.getDescription());
            ((TextView) actionButton.findViewById(R.id.aps)).setText("" + action.getActionPoints() + "AP");


            if (!actionViewContainer.isEnabled()) {
                actionButton.setEnabled(false);
            } else {
                actionButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {


                        callback.actionSelected(actionViewContainer);

                    }
                });
            }

            this.actionsContainer.addView(actionButton);
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setActions(List<ActionViewContainer> actions, String actionTitle) {
        this.actions = actions;
        this.actionTitle = actionTitle;

    }

    public void setCallback(ActionSelectedCallback callback) {
        this.callback = callback;
    }

    public interface ActionSelectedCallback {

        void actionSelected(ActionViewContainer action);

        void actionSkipped();

        void actionCancelled();

    }
}
