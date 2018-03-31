package com.landsofruin.companion.state.transition.threads;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.transition.Transition;

/**
 * Created by juhani on 01/07/15.
 */
@ObjectiveCName("ServerThreadInterface")
public interface ServerThreadInterface {
    void setPlayerIdentifier(String playerIdentifier);
    void write(Transition transition);
}
