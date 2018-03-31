package com.landsofruin.companion.eventbus;


import com.landsofruin.companion.state.transition.CreateThrowableTransition;

public class ThrowableDragCompleMapEvent {
    private  CreateThrowableTransition transition;

    public ThrowableDragCompleMapEvent(CreateThrowableTransition transition) {
        this.transition = transition;
    }

    public CreateThrowableTransition getTransition() {
        return transition;
    }
}
