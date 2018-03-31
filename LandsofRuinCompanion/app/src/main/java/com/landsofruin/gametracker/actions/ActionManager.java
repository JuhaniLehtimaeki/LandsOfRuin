package com.landsofruin.gametracker.actions;

import android.util.Log;

import com.landsofruin.companion.state.gameruleobjects.action.Action;

import java.util.HashMap;
import java.util.LinkedList;


public class ActionManager {

    private static ActionManager instance = new ActionManager();
    private LinkedList<Action> actions = new LinkedList<>();
    private HashMap<Integer, Action> actionLookupCache = new HashMap<>();

    private ActionManager() {
    }

    public static ActionManager getInstance() {
        return instance;
    }

    public LinkedList<Action> getActions() {
        return actions;
    }


    public Action getActionForId(int id) {

        Action ret = actionLookupCache.get(id);

        if (ret != null) {
            return ret;
        }
        for (Action action : actions) {
            if (action.getId() == id) {
                actionLookupCache.put(id, action);
                return action;
            }
        }
        Log.w("actions", "tried to find an action for id " + id
                + " but didn't find any");
        return null;
    }


    public void clearData() {
        this.actions.clear();
        this.actionLookupCache.clear();
    }

    public void addData(Action action) {
        this.actions.add(action);
    }
}
