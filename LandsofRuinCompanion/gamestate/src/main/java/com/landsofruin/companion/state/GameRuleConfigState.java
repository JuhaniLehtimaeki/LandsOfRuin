package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 09/11/15.
 */
@ObjectiveCName("GameState")
public class GameRuleConfigState {

    private boolean delayedDamageModel = true;
    private boolean advancedCoverModel = true;


    public boolean isDelayedDamageModel() {
        return delayedDamageModel;
    }

    public void setDelayedDamageModel(boolean delayedDamageModel) {
        this.delayedDamageModel = delayedDamageModel;
    }

    public boolean isAdvancedCoverModel() {
        return advancedCoverModel;
    }

    public void setAdvancedCoverModel(boolean advancedCoverModel) {
        this.advancedCoverModel = advancedCoverModel;
    }
}
