package com.landsofruin.companion.eventbus;

import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.gametracker.actionsui.ActionViewUtil;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;

/**
 * Created by juhani on 25/03/15.
 */
public class StartThrowUIEvent {

    private final WargearConsumable wargear;
    private final CharacterState character;
    private final Action action;
    private final ActionViewUtil.ActionPerformPressedCallback callback;

    public StartThrowUIEvent(WargearConsumable wargear,
                             CharacterState character, Action action, ActionViewUtil.ActionPerformPressedCallback callback) {
        this.wargear = wargear;
        this.character = character;
        this.action = action;
        this.callback = callback;
    }

    public Action getAction() {
        return action;
    }

    public CharacterState getCharacter() {
        return character;
    }

    public WargearConsumable getWargear() {
        return wargear;
    }

    public ActionViewUtil.ActionPerformPressedCallback getCallback() {
        return callback;
    }
}
