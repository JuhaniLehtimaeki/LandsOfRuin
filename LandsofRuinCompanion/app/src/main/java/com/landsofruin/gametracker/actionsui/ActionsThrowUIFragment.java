package com.landsofruin.gametracker.actionsui;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.EndThrowUIEvent;
import com.landsofruin.companion.eventbus.ThrowableDragCompleMapEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.RegionState;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.CreateThrowableTransition;
import com.landsofruin.companion.utils.ThrowableDragShadowBuilder;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.actions.ActionManager;
import com.landsofruin.gametracker.actionsui.ActionViewUtil.ActionPerformPressedCallback;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;

public class ActionsThrowUIFragment extends Fragment {
    private static final String ARGUMENT_PC_ID = "ARGUMENT_PC_ID";
    private CharacterState character;
    private Action action;
    private ActionPerformPressedCallback callback;
    private WargearConsumable wargear;
    private View doneButton;
    private CreateThrowableTransition transition;


    public static ActionsThrowUIFragment newInstance(WargearConsumable wargear,
                                                     CharacterState character, Action action) {
        ActionsThrowUIFragment f = new ActionsThrowUIFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PC_ID, character.getIdentifier());
        args.putInt("wargear", wargear.getId());
        args.putInt("action", action.getId());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        action = ActionManager.getInstance().getActionForId(
                getArguments().getInt("action"));

        int wargearId = getArguments().getInt("wargear");
        wargear = (WargearConsumable) WargearManager.getInstance()
                .getWargearById(wargearId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.action_throw_fragment, container,
                false);


        if (action.getId() == Action.ACTION_ID_EXECUTE_SLAVE) {
            ((TextView) v.findViewById(R.id.title)).setText("Execute\n"
                    + wargear.getName());
        } else if (action.getId() == Action.ACTION_ID_ARTILLERY_STRIKE) {
            ((TextView) v.findViewById(R.id.title)).setText("Call in\n"
                    + wargear.getName());
        } else if (action.getId() == Action.ACTION_ID_FIRE_BAZOOKA) {
            ((TextView) v.findViewById(R.id.title)).setText("Fire\n"
                    + wargear.getName());
        } else {
            ((TextView) v.findViewById(R.id.title)).setText("Throwing\n"
                    + wargear.getName());
        }


        StringBuffer evolutionBuffer = new StringBuffer();
        for (int diameter : wargear.getTemplateSizeEvolution()) {
            if (evolutionBuffer.length() > 0) {
                evolutionBuffer.append(" - ");
            }
            evolutionBuffer.append(diameter);
        }

        ((TextView) v.findViewById(R.id.evolution)).setText("Diameter evolution:\n" + evolutionBuffer);

        int range = LookupHelper.getInstance().getCharacterTypeFor(character.getCharacterType())
                .getBaseThrowRange();


        range = (int) (range * wargear.getThrowRangeModifier());
        if (action.getId() == Action.ACTION_ID_EXECUTE_SLAVE) {
            range = 10;
        }


        if (action.getId() == Action.ACTION_ID_ARTILLERY_STRIKE) {
            ((TextView) v.findViewById(R.id.range)).setText("Range:\nunlimited");
            v.findViewById(R.id.grenade_info_container).setVisibility(View.GONE);
            v.findViewById(R.id.bazooka_info_container).setVisibility(View.GONE);
        } else if (action.getId() == Action.ACTION_ID_FIRE_BAZOOKA) {
            ((TextView) v.findViewById(R.id.range)).setText("Range:\nunlimited, requires Line of Sight");
            v.findViewById(R.id.grenade_info_container).setVisibility(View.GONE);
            v.findViewById(R.id.artillery_info_container).setVisibility(View.GONE);
        } else {
            ((TextView) v.findViewById(R.id.range)).setText("Range:\n" + range);
            v.findViewById(R.id.artillery_info_container).setVisibility(View.GONE);
            v.findViewById(R.id.bazooka_info_container).setVisibility(View.GONE);
        }


        v.findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        BusProvider.getInstance().post(new EndThrowUIEvent());
                        GameState gameState = ((GameActivity) getActivity()).getGame();
                        gameState.getWorld().clearTemporarThrowableStates();
                        BusProvider.getInstance().post(new GameStateChangedEvent());
                    }
                });


        doneButton = v.findViewById(R.id.done_button);
        doneButton.setEnabled(false);
        v.findViewById(R.id.done_button).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GameState gameState = ((GameActivity) getActivity()).getGame();
                        gameState.getWorld().clearTemporarThrowableStates();

                        callback.actionCancelled();
                        BusProvider.getInstance().post(new EndThrowUIEvent());
                        ((GameActivity) getActivity()).sendToServer(transition);


                    }
                });


        v.findViewById(R.id.drag_icon).setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        startThrowaleDrag(v);
                        return true;
                    }
                });

        return v;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);


        if (((BaseGameActivity) activity).getGame() == null) {
            Toast.makeText(activity, "Something went wrong. Please continue the game from \"Resume Game\"", Toast.LENGTH_LONG).show();
            Log.e("gamestate", "No gamestate in activity. Killing activity.");
            ((BaseGameActivity) activity).finish();
        } else {

            BusProvider.getInstance().register(this);
            this.character = ((BaseGameActivity) activity).getGame().findCharacterByIdentifier(getArguments()
                    .getString(ARGUMENT_PC_ID));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    public boolean startThrowaleDrag(View view) {
        String identifier = ThrowableState.THROWABLE_DRAG_IDENTIFIER_PREFIX
                + wargear.getId() + "," + action.getId() + ","
                + character.getIdentifier();

        ClipData data = ClipData.newPlainText(identifier, identifier);
        ThrowableDragShadowBuilder shadowBuilder = new ThrowableDragShadowBuilder(
                view);
        view.startDrag(data, shadowBuilder, null, 0);

        return true;
    }

    public void addCallback(ActionPerformPressedCallback callback) {
        this.callback = callback;
    }

    @Subscribe
    public void onThrowableDragCompleMapEvent(ThrowableDragCompleMapEvent event) {

        this.transition = event.getTransition();
        doneButton.setEnabled(true);

        GameState gameState = ((GameActivity) getActivity()).getGame();

        ThrowableState newThrowable = new ThrowableState(this.transition.getWargearId(), gameState.getMe().getIdentifier());

        LinkedList<String> regions = new LinkedList<String>();
        for (RegionState region : gameState.getMap().findRegionsByPoint(this.transition.getPoint())) {
            regions.add(region.getIdentifier());
        }

        newThrowable.updatePosition(this.transition.getPoint(), regions);

        gameState.getWorld().clearTemporarThrowableStates();
        gameState.getWorld().addTemporarThrowableState(newThrowable);
    }
}
