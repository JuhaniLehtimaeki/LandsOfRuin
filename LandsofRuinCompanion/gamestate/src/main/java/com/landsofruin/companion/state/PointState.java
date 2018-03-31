package com.landsofruin.companion.state;


import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Model class for a point on a {@link MapState}.
 */
@ObjectiveCName("PointState")
public class PointState {
    public float x;
    public float y;


    /**
     * required constructor for firebase, don't use in code!
     */
    public PointState() {

    }

    public PointState(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PointState) {
            return ((PointState) o).x == x && ((PointState) o).y == y;
        }
        return false;
    }
}
