package com.landsofruin.companion.state;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Model describing the map.
 */
@ObjectiveCName("MapState")
public class MapState {
    private static final int DRAG_OUT_SECTION_WIDTH = 15;


    public static final int MAP_REGION_TYPE_STREET = 1;
    public static final int MAP_REGION_TYPE_BUILDING_BLOCK = 2;
    public static final int MAP_REGION_TYPE_DRAG_OUT = 3;


    // Serialize
    private List<RegionState> regions;
    private List<RegionState> regionsWithSpecials;
    private String name;
    private String backgroundImageURL;
    private String providerURL;
    private String providerIconURL;
    private String providerName;
    private String groupName;


    // Skip serialization
    private transient int width = -1;
    private transient int height = -1;

    public MapState() {
        regions = new ArrayList<>();
        regionsWithSpecials = new ArrayList<>();

        width = -1;
        height = -1;
    }



    public int getWidth() {
        if (width == -1) {
            calculateBoundingBox();
        }

        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public int getHeight() {
        if (height == -1) {
            calculateBoundingBox();
        }

        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean contains(PointState point) {
        int width = getWidth();
        int height = getHeight();

        return point.x >= 0 && point.x <= width && point.y >= 0
                && point.y <= height;
    }


    public List<RegionState> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionState> regions) {
        this.regions = regions;
    }


    @Exclude
    public List<RegionState> getRegionsWithoutSpecials() {
        List<RegionState> ret = new LinkedList<>();


        for (RegionState regionState : getRegionsWithSpecials()) {
            if (!regionState.isSpecialDragOutSection()) {
                ret.add(regionState);
            }
        }

        return ret;
    }

    @Exclude
    public void setRegionsAndAddSpecials(List<RegionState> regions) {
        this.regions = regions;
        regionsWithSpecials.addAll(this.regions);
        calculateBoundingBox();

        shiftMapCordinatesToAddSpecialSections(this.regions);

        RegionState regionDragNorth = new RegionState(new PointState[]{
                new PointState(DRAG_OUT_SECTION_WIDTH, 0), new PointState(getWidth() + DRAG_OUT_SECTION_WIDTH, 0),
                new PointState(getWidth() + DRAG_OUT_SECTION_WIDTH, DRAG_OUT_SECTION_WIDTH),
                new PointState(DRAG_OUT_SECTION_WIDTH, DRAG_OUT_SECTION_WIDTH)});
        regionDragNorth.setLabel("");
        regionDragNorth.setRegionType(MAP_REGION_TYPE_DRAG_OUT);
        regionDragNorth.setAsSpecialDragOutSection();
//		regionDragNorth.setBackgroundColorSelected(DRAG_OUT_SECTION_BACKGROUND_SELECTED);
        this.regionsWithSpecials.add(regionDragNorth);


        RegionState regionDragWest = new RegionState(new PointState[]{
                new PointState(0, DRAG_OUT_SECTION_WIDTH), new PointState(DRAG_OUT_SECTION_WIDTH, DRAG_OUT_SECTION_WIDTH),
                new PointState(DRAG_OUT_SECTION_WIDTH, getHeight() + DRAG_OUT_SECTION_WIDTH),
                new PointState(0, getHeight() + DRAG_OUT_SECTION_WIDTH)});
        regionDragWest.setLabel("");
        regionDragWest.setRegionType(MAP_REGION_TYPE_DRAG_OUT);
        regionDragWest.setAsSpecialDragOutSection();
//		regionDragWest.setBackgroundColorSelected(DRAG_OUT_SECTION_BACKGROUND_SELECTED);
        this.regionsWithSpecials.add(regionDragWest);


        RegionState regionDragEast = new RegionState(new PointState[]{
                new PointState(getWidth() + DRAG_OUT_SECTION_WIDTH, DRAG_OUT_SECTION_WIDTH), new PointState(getWidth() + (DRAG_OUT_SECTION_WIDTH * 2), DRAG_OUT_SECTION_WIDTH),
                new PointState(getWidth() + (DRAG_OUT_SECTION_WIDTH * 2), DRAG_OUT_SECTION_WIDTH + getHeight()),
                new PointState(getWidth() + DRAG_OUT_SECTION_WIDTH, DRAG_OUT_SECTION_WIDTH + getHeight())});
        regionDragEast.setLabel("");
        regionDragEast.setRegionType(MAP_REGION_TYPE_DRAG_OUT);
        regionDragEast.setAsSpecialDragOutSection();
//		regionDragEast.setBackgroundColorSelected(DRAG_OUT_SECTION_BACKGROUND_SELECTED);
        this.regionsWithSpecials.add(regionDragEast);


        RegionState regionDragSouth = new RegionState(new PointState[]{
                new PointState(DRAG_OUT_SECTION_WIDTH, getHeight() + DRAG_OUT_SECTION_WIDTH), new PointState(getWidth() + DRAG_OUT_SECTION_WIDTH, getHeight() + DRAG_OUT_SECTION_WIDTH),
                new PointState(getWidth() + DRAG_OUT_SECTION_WIDTH, getHeight() + (DRAG_OUT_SECTION_WIDTH * 2)),
                new PointState(DRAG_OUT_SECTION_WIDTH, getHeight() + (DRAG_OUT_SECTION_WIDTH * 2))});
        regionDragSouth.setLabel("");
        regionDragSouth.setRegionType(MAP_REGION_TYPE_DRAG_OUT);
        regionDragSouth.setAsSpecialDragOutSection();
//		regionDragSouth.setBackgroundColorSelected(DRAG_OUT_SECTION_BACKGROUND_SELECTED);
        this.regionsWithSpecials.add(regionDragSouth);


        this.height = this.height + (DRAG_OUT_SECTION_WIDTH * 2);
        this.width = this.width + (DRAG_OUT_SECTION_WIDTH * 2);
    }

    public List<RegionState> getRegionsWithSpecials() {
        return regionsWithSpecials;
    }

    public void setRegionsWithSpecials(List<RegionState> regionsWithSpecials) {
        this.regionsWithSpecials = regionsWithSpecials;
    }

    private void shiftMapCordinatesToAddSpecialSections(
            List<RegionState> regions) {
        for (RegionState regionState : regions) {

            ArrayList<PointState> pointsList = new ArrayList<>(regionState.getPointsAsArray().length);
            PointState[] points = new PointState[regionState.getPointsAsArray().length];
            int index = 0;
            for (PointState point : regionState.getPointsAsArray()) {

                PointState newPoint = new PointState(
                        point.x + DRAG_OUT_SECTION_WIDTH, point.y
                        + DRAG_OUT_SECTION_WIDTH);
                points[index] = newPoint;
                pointsList.add(newPoint);

                ++index;
            }
            regionState.setPointsAsArray(points);
            regionState.setPoints(pointsList);
        }

    }

    private void calculateBoundingBox() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (RegionState region : regionsWithSpecials) {
            for (PointState point : region.getPointsAsArray()) {
                minX = Math.min(minX, (int) point.x);
                minY = Math.min(minY, (int) point.y);
                maxX = Math.max(maxX, (int) point.x);
                maxY = Math.max(maxY, (int) point.y);
            }
        }

        width = maxX - minX + 1;
        height = maxY - minY + 1;
    }

    public List<RegionState> findRegionsByPoint(PointState point) {
        List<RegionState> regions = new ArrayList<>();

        for (RegionState region : this.regionsWithSpecials) {
            if (region.contains(point)) {
                regions.add(region);
            }
        }

        return regions;
    }

    public RegionState findRegionByIdentifier(String identifier) {
        RegionState region = null;

        for (RegionState currentRegion : regionsWithSpecials) {
            if (currentRegion.getIdentifier().equals(identifier)) {
                region = currentRegion;
                break;
            }
        }

        return region;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof MapState) {
            MapState other = (MapState) o;

            if (other.regions.size() != regions.size()) {
                return false;
            }

            int index = 0;
            for (RegionState region : regions) {
                if (!region.equals(other.regions.get(index))) {
                    return false;
                }
                return true;
            }

            return true;
        }

        return false;
    }

    public void setBackgroundImageURL(String backgroundImageURL) {
        this.backgroundImageURL = backgroundImageURL;
    }

    public String getBackgroundImageURL() {
        return backgroundImageURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getProviderURL() {
        return providerURL;
    }

    public void setProviderURL(String providerURL) {
        this.providerURL = providerURL;
    }

    public String getProviderIconURL() {
        return providerIconURL;
    }

    public void setProviderIconURL(String providerIconURL) {
        this.providerIconURL = providerIconURL;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
