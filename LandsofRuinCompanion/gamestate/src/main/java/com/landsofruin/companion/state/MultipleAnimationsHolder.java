package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.LinkedList;

/**
 * Created by juhani on 16/09/15.
 */
@ObjectiveCName("MultipleAnimationsHolder")
public class MultipleAnimationsHolder {
    private boolean isSequential = false;
    private LinkedList<AnimationHolder> animations = new LinkedList<>();

    public MultipleAnimationsHolder(boolean isSequential) {
        this.isSequential = isSequential;

    }

    public boolean isSequential() {
        return isSequential;
    }


    public void addOneAnimationEffectHolder(AnimationHolder animation) {
        animations.add(animation);
    }

    public LinkedList<AnimationHolder> getAnimations() {
        return animations;
    }
}
