package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * Pong {@link Transition} to answer a {@link PingTransition}.
 */
@ObjectiveCName("PongTransition")
public class PongTransition implements Transition {
    @Override
    public boolean isRelevantForServerSync() {
        return false;
    }

    @Override
    public void triggerClient(ClientThreadInterface origin, GameState gameState) {
        new IllegalStateException("PongTransition should never be triggered on the client.");
    }

    @Override
    public void triggerServer(ServerThreadInterface origin, GameState gameState) {
        // Everything's okay if this transition has been received. Just do nothing.
    }
}
