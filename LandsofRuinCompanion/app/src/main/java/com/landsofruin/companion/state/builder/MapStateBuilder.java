package com.landsofruin.companion.state.builder;

import com.landsofruin.companion.provider.snapshots.MapSnapshot;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.RegionState;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MapStateBuilder {


    public static MapState buildFrom(MapSnapshot mapSnapshot) {
        MapState state = new MapState();
        state.setBackgroundImageURL(mapSnapshot.getBackgroundImageUrl());
        state.setName(mapSnapshot.getTitle());
        state.setProviderIconURL(mapSnapshot.getProviderIconURL());
        state.setProviderName(mapSnapshot.getProviderName());
        state.setProviderURL(mapSnapshot.getProviderURL());

        ArrayList<RegionState> regions = new ArrayList<>();
        try {
            JSONArray mapArray = new JSONArray(mapSnapshot.getData());
            for (int i = 0; i < mapArray.length(); i++) {

                JSONArray section = mapArray.getJSONArray(i);


                PointState[] points = new PointState[section.length()];
                for (int j = 0; j < section.length(); j++) {

                    JSONArray point = section.getJSONArray(j);
                    int x = point.getInt(0) * mapSnapshot.getCellSize() * 10;
                    int y = point.getInt(1) * mapSnapshot.getCellSize() * 10;
                    points[j] = new PointState(x, y);

                }
                RegionState region = new RegionState(points);
                regions.add(region);

            }
            state.setRegionsAndAddSpecials(regions);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return state;
    }
}
