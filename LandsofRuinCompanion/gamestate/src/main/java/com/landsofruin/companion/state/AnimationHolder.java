package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 11/09/15.
 */
@ObjectiveCName("AnimationHolder")
public class AnimationHolder {
    private String targetCharacter;
    private int iconId;


    public AnimationHolder(String targetCharacter, int iconId) {
        this.targetCharacter = targetCharacter;
        this.iconId = iconId;
    }

    public String getTargetCharacter() {
        return targetCharacter;
    }

    public int getIconId() {
        return iconId;
    }


}