package com.landsofruin.companion.state;


import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Model class for describing a region on a {@link MapState}. A region is a
 * polygon with some meta data attached.
 */
@ObjectiveCName("RegionState")
public class RegionState {
    // Serialize
    private String identifier;

    private List<PointState> points = new ArrayList<>();

    // helper array
    @Exclude
    private PointState[] pointsHelper;
    private String label;
    private HashMap<String, Integer> playerNoise = new HashMap<>();


    // Skip serialization

    private transient boolean isSelected;

    private boolean isSpecialDragOutSection = false;

    private int regionType;

    /**
     * required empty state constructor for firebase. don't use this in code!
     */
    public RegionState() {

    }

    public RegionState(PointState[] points) {
        this.pointsHelper = points;


        for (int i = 0; i < points.length; ++i) {
            this.points.add(points[i]);
        }

        this.playerNoise = new HashMap<>();
        this.identifier = UUID.randomUUID().toString();


    }


    public String getIdentifier() {
        return identifier;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setAsSpecialDragOutSection() {
        isSpecialDragOutSection = true;
    }


    public void setSpecialDragOutSection(boolean specialDragOutSection) {
        isSpecialDragOutSection = specialDragOutSection;
    }

    public boolean isSpecialDragOutSection() {
        return isSpecialDragOutSection;
    }

    @Exclude
    public int getTotalNoise() {

        int ret = 0;
        for (String key : playerNoise.keySet()) {
            ret += getNoiseForPlayer(key);
        }

        return ret;
    }

    @Exclude
    public int getNoiseForPlayer(String playerIdentifier) {
        if (!playerNoise.containsKey(playerIdentifier)) {
            return 0;
        }

        return playerNoise.get(playerIdentifier);
    }

    public void addNoiseForPlayer(String playerIdentifier, int noise) {
        int currentNoise = getNoiseForPlayer(playerIdentifier);

        playerNoise.put(playerIdentifier, currentNoise + noise);

    }

    public void resetNoiseForPlayer(String playerIdentifier) {
        int noise = getNoiseForPlayer(playerIdentifier);

        noise = (int) (noise / 2);

        if (noise == 0) {
            playerNoise.remove(playerIdentifier);
        } else {
            playerNoise.put(playerIdentifier, noise);
        }
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public List<PointState> getPoints() {
        return points;
    }

    public void setPoints(List<PointState> points) {
        this.points = points;
    }

    @Exclude
    public PointState[] getPointsAsArray() {
        if (pointsHelper == null && this.points != null) {
            pointsHelper = this.points.toArray(new PointState[this.points.size()]);
        }


        return pointsHelper;
    }

    /**
     * this is used to shift the section to make room for special drag-out
     * sesctions
     *
     * @param points
     */
    @Exclude
    public void setPointsAsArray(PointState[] points) {
        this.pointsHelper = points;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getRegionType() {
        return regionType;
    }

    public void setRegionType(int regionType) {
        this.regionType = regionType;
    }


    public boolean contains(PointState point) {

        boolean inRegion = false;

        for (int i = 0, j = pointsHelper.length - 1; i < pointsHelper.length; j = i++) {
            if ((pointsHelper[i].y > point.y) != (pointsHelper[j].y > point.y)
                    && (point.x < (pointsHelper[j].x - pointsHelper[i].x)
                    * (point.y - pointsHelper[i].y)
                    / (pointsHelper[j].y - pointsHelper[i].y) + pointsHelper[i].x)) {
                inRegion = !inRegion;
            }
        }

        return inRegion;
    }

    public HashMap<String, Integer> getPlayerNoise() {
        return playerNoise;
    }

    public void setPlayerNoise(HashMap<String, Integer> playerNoise) {
        this.playerNoise = playerNoise;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof RegionState) {

            RegionState other = (RegionState) o;

            if (other.pointsHelper.length != pointsHelper.length) {
                return false;
            }

            int index = 0;
            for (PointState point : pointsHelper) {
                if (!point.equals(other.pointsHelper[index])) {
                    return false;
                }

                ++index;
            }
            return true;

        }
        return false;
    }
}
