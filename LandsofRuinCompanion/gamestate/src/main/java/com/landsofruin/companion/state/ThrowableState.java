package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;

import java.util.List;

@ObjectiveCName("ThrowableState")
public class ThrowableState {

    public static final transient String THROWABLE_DRAG_IDENTIFIER_PREFIX = "THROWABLE_DRAG_IDENTIFIER_PREFIX";

    private PointState point;

    private int wargearId;
    private String owningPlayerId;
    private int turnCounter = 0;


    private List<String> regionIdentifiers_String;

    public ThrowableState(int wargearId, String owningPlayerId) {
        this.wargearId = wargearId;
        this.owningPlayerId = owningPlayerId;
    }


    public int getWargearId() {
        return wargearId;
    }

    public PointState getCenterPoint() {
        return point;
    }

    public String getOwningPlayerId() {
        return owningPlayerId;
    }

    public void updatePosition(PointState point, List<String> regionIdentifiers) {
        this.point = point;

        this.regionIdentifiers_String = regionIdentifiers;

    }


    private int currentTemplateSize = 0;

    public void advanceTurn() {
        int oldTemplate = getTemplateSize();

        ++this.turnCounter;

        if (this.turnCounter > LookupHelper.getInstance().getWargearFor(this).getTemplateSizeEvolution().size()) {
            currentTemplateSize = 0;
        } else {
            currentTemplateSize = LookupHelper.getInstance().getWargearFor(this).getTemplateSizeEvolution().get(this.turnCounter - 1) ;
        }

        if (oldTemplate == 0 && currentTemplateSize != 0) {
            addNoiseThisTurn = LookupHelper.getInstance().getWargearFor(this).getHitNoise();
        } else {
            addNoiseThisTurn = 0;
        }

    }




    public boolean shouldBeRemovedStartOfAction() {
        if (this.turnCounter+1 > LookupHelper.getInstance().getWargearFor(this).getTemplateSizeEvolution().size()) {
            return true;
        }

        return false;
    }

    private int addNoiseThisTurn = 0;

    public int getAddNoiseThisTurn() {
        return addNoiseThisTurn;
    }


    public int getTemplateSize() {
        return currentTemplateSize;

    }

    public List<String> getRegions() {
        return regionIdentifiers_String;
    }

}
