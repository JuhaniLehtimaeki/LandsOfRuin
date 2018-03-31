package com.landsofruin.companion.state.transition;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.threads.ClientThreadInterface;
import com.landsofruin.companion.state.transition.threads.ServerThreadInterface;

/**
 * Common interface for transitions changing the GameState.
 * <p/>
 * We distinguish between triggering a {@link Transition} on the client and
 * the server because the server might add additional data to the transition
 * (simulation) before synchronizing it to the clients.
 */
@ObjectiveCName("Transition")
public interface Transition {
    /**
     * Trigger this transition on a client.
     *
     * @param gameState The {@link GameState} of the client.
     */
    public abstract void triggerClient(ClientThreadInterface origin, GameState gameState);

    /**
     * Trigger this transition on the server.
     *
     * @param gameState The {@link GameState} of the server.
     */
    public abstract void triggerServer(ServerThreadInterface origin, GameState gameState);

    /**
     * Returns weather this transition is relevant for synchronizing to the cloud servers.
     * <p/>
     * Return true if this transition is needed to calculate experience and statistics or
     * to generate a battle log. Otherwise return false if this transition does not contain
     * any information that could be used in the future. For example a StartGameTransition
     * should return true because it sets important game parameters. However a PingTransition
     * does not contain any information at all and should therefore be discarded.
     */
    public abstract boolean isRelevantForServerSync();

//    String getQualifiedClassName();
}
