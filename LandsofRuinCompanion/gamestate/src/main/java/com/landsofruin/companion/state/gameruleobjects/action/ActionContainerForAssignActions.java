package com.landsofruin.companion.state.gameruleobjects.action;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("ActionContainerForAssignActions")
public class ActionContainerForAssignActions {

    private Action action;
    private boolean enabled;
    private String disabledText;

    public ActionContainerForAssignActions(Action action, boolean enabled,
                                           String disabledText) {
        super();
        this.action = action;
        this.enabled = enabled;
        this.disabledText = disabledText;
    }

    public Action getAction() {
        return action;
    }

    public String getDisabledText() {
        return disabledText;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
