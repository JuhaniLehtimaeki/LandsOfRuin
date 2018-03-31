package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * A {@link Transition} regulary sent to check if the connection is still alive.
 */
@ObjectiveCName("PingTransition")
public class PingTransition implements Transition {
    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {

        origin.write(new PongTransition());

    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        new IllegalStateException("PingTransition should never be triggered on server");
    }
}
