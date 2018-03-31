package com.landsofruin.companion.indicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.eventbus.EndThrowUIEvent;
import com.landsofruin.companion.eventbus.HideCommandPanelManuallyEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PhaseState;
import com.landsofruin.companion.state.PrimaryPhase;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

/**

 */
public class GameIndicatorFragment extends Fragment {

    private PhaseState displayPhase;
    private TextView turnCounter;

    private BaseGameActivity activity;
    private View environmentEnemy;
    private View environmentMy;
    private View actionEnemy;
    private View actionMy;
    private View syncEnemy;
    private View syncMy;
    private View commandEnemy;
    private View commandMy;
    private int currentTurn;

    //init to true to make sure we update first time
    boolean phaseChanged = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_indicator, container, false);
        GameState gameState = getGameState();


        if (gameState == null || gameState.getPhase() == null) {
            getActivity().finish();
            return view;
        }


        assert view != null;


        turnCounter = (TextView) view.findViewById(R.id.turn_counter);

        displayPhase = gameState.getPhase().copy();


        actionEnemy = view.findViewById(R.id.action_enemy);
        actionMy = view.findViewById(R.id.action_my);


        syncEnemy = view.findViewById(R.id.sync_enemy);
        syncMy = view.findViewById(R.id.sync_my);

        commandEnemy = view.findViewById(R.id.command_enemy);
        commandMy = view.findViewById(R.id.command_my);


        environmentEnemy = view.findViewById(R.id.environment_enemy);
        environmentMy = view.findViewById(R.id.environment_my);

        return view;
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        if (!(activity instanceof BaseGameActivity)) {
            throw new RuntimeException("Activity has to be derived from BaseGameActivity");
        }

        this.activity = (BaseGameActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        updateUI(getGameState());
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);

    }

    @Subscribe
    public void onGameStateChangedEventReceived(GameStateChangedEvent event) {
        updateUI(getGameState());
    }


    private void setNewTurn(int turn) {
        turnCounter.setText("" + turn);
    }


    protected synchronized void updateUI(GameState gameState) {


        while (!isDisplayPhaseSameAs(gameState.getPhase())) {
            if (gameState.isPhaseChangeUndoInEffect()) {
                advanceBackwards();
            } else {
                advance();
            }


        }
        final PhaseState phase = gameState.getPhase();


        if (currentTurn != gameState.getPhase().getGameTurn()) {
            currentTurn = gameState.getPhase().getGameTurn();
            setNewTurn(currentTurn);
        }


        boolean isMine = phase.isMine();
        switch (phase.getPrimaryPhase()) {
            case PrimaryPhase.ACTION:

                actionEnemy.animate().alpha(isMine ? 0 : 1);
                syncEnemy.animate().alpha(0);
                commandEnemy.animate().alpha(0);
                environmentEnemy.animate().alpha(0);


                actionMy.animate().alpha(isMine ? 1 : 0);
                syncMy.animate().alpha(0);
                commandMy.animate().alpha(0);
                environmentMy.animate().alpha(0);
                break;


            case PrimaryPhase.MOVE:

                actionEnemy.animate().alpha(0);
                syncEnemy.animate().alpha(isMine ? 0 : 1);
                commandEnemy.animate().alpha(0);
                environmentEnemy.animate().alpha(0);


                actionMy.animate().alpha(0);
                syncMy.animate().alpha(isMine ? 1 : 0);
                commandMy.animate().alpha(0);
                environmentMy.animate().alpha(0);
                break;

            case PrimaryPhase.ASSIGN_ACTIONS:

                actionEnemy.animate().alpha(0);
                syncEnemy.animate().alpha(0);
                commandEnemy.animate().alpha(isMine ? 0 : 1);
                environmentEnemy.animate().alpha(0);


                actionMy.animate().alpha(0);
                syncMy.animate().alpha(0);
                commandMy.animate().alpha(isMine ? 1 : 0);
                environmentMy.animate().alpha(0);
                break;

            case PrimaryPhase.ZOMBIES:

                actionEnemy.animate().alpha(0);
                syncEnemy.animate().alpha(0);
                commandEnemy.animate().alpha(0);
                environmentEnemy.animate().alpha(isMine ? 0 : 1);


                actionMy.animate().alpha(0);
                syncMy.animate().alpha(0);
                commandMy.animate().alpha(0);
                environmentMy.animate().alpha(isMine ? 1 : 0);
                break;
        }

        if (phase.isPreGamePhase()) {
            actionEnemy.animate().alpha(0);
            syncEnemy.animate().alpha(0);
            commandEnemy.animate().alpha(0);
            environmentEnemy.animate().alpha(0);


            actionMy.animate().alpha(0);
            syncMy.animate().alpha(0);
            commandMy.animate().alpha(0);
            environmentMy.animate().alpha(0);

        }

        phaseChanged = false;
    }


    private boolean isDisplayPhaseSameAs(PhaseState phaseState) {

        //handle end game separately
        if (phaseState.getPrimaryPhase() == PrimaryPhase.GAME_END) {
            return true;
        }
        return phaseState.getPrimaryPhase() == displayPhase.getPrimaryPhase()
                && phaseState.getCurrentPlayer().equals(displayPhase.getCurrentPlayer());
    }

    private synchronized void advance() {
        BusProvider.getInstance().post(new EndThrowUIEvent());
        BusProvider.getInstance().post(new CharacterSelectedEvent(null));
        BusProvider.getInstance().post(new HideCommandPanelManuallyEvent());
        displayPhase = displayPhase.getNext(getGameState());

        phaseChanged = true;
    }


    private synchronized void advanceBackwards() {
        BusProvider.getInstance().post(new EndThrowUIEvent());
        displayPhase = displayPhase.getPrevious(getGameState());

        phaseChanged = true;
    }


    public GameState getGameState() {
        return activity.getGame();
    }

    public void sendToServer(Transition transition) {
        activity.sendToServer(transition);
    }
}
