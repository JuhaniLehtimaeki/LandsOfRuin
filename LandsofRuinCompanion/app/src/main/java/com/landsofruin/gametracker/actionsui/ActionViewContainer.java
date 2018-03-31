package com.landsofruin.gametracker.actionsui;

import android.support.v4.app.Fragment;

public class ActionViewContainer {

    private String actionButtonText;
    private Fragment performActionFragment;
    private int actionID;
    private boolean enabled;
    private ActionViewUtil.ActionPerformPressedCallback pressedCallback;


    public ActionViewContainer(String actionButtonText,
                               Fragment performActionFragment, int actionID, boolean enabled, ActionViewUtil.ActionPerformPressedCallback pressedCallback) {

        this.actionButtonText = actionButtonText;
        this.performActionFragment = performActionFragment;
        this.actionID = actionID;
        this.enabled = enabled;
        this.pressedCallback = pressedCallback;
    }


    public ActionViewContainer(String actionButtonText,
                               Fragment performActionFragment, int actionID, ActionViewUtil.ActionPerformPressedCallback pressedCallback) {
        this(actionButtonText, performActionFragment, actionID, true, pressedCallback);
    }

    // if this is not null this is executed instead of creating fragment
    public ActionViewUtil.ActionPerformPressedCallback getPressedCallback() {
        return pressedCallback;
    }

    public String getActionButtonText() {
        return actionButtonText;
    }

    public Fragment getPerformActionFragment() {
        return performActionFragment;
    }

    public int getActionID() {
        return actionID;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
