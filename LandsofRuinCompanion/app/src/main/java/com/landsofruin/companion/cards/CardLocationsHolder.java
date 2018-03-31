package com.landsofruin.companion.cards;

import java.util.HashMap;

/**
 * Created by juhani on 11/9/13.
 */
public class CardLocationsHolder {

    private static HashMap<String, int[]> characterToCoordinates = new HashMap<String, int[]>();


    public static void setLocationFor(String characterId, int[] coordinates) {
        characterToCoordinates.put(characterId, coordinates);
//        Log.d("coordinatedebug", "setting coordinates");
    }


    public static int[] getCoordinatesFor(String characterId) {

//        Log.d("coordinatedebug", "getCoordinatesFor " + characterId + " coodinates: " + characterToCoordinates.get(characterId));

        return characterToCoordinates.get(characterId);
    }
}
