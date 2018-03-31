package com.landsofruin.companion.eventbus;

import com.landsofruin.companion.state.MultipleAnimationsHolder;

public class QueueAnimationsEvent {

    private MultipleAnimationsHolder animations;

    public QueueAnimationsEvent(MultipleAnimationsHolder item) {
        this.animations = item;
    }

    public MultipleAnimationsHolder getAnimations() {
        return animations;
    }
}
