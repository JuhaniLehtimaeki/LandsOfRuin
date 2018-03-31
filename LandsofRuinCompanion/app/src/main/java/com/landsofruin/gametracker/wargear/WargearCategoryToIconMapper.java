package com.landsofruin.gametracker.wargear;

import android.util.Log;

import com.landsofruin.gametracker.R;

import java.util.HashMap;

/**
 * Created by juhani on 11/8/13.
 */
public class WargearCategoryToIconMapper {

    private static HashMap<String, Integer> wargearCategoryToIconResource = new HashMap<String, Integer>();

    static {
        wargearCategoryToIconResource.put("Edged", R.drawable.icon_weapon_edge);
        wargearCategoryToIconResource.put("Blunt", R.drawable.icon_weapon_blunt);
        wargearCategoryToIconResource.put("Hitech", R.drawable.icon_weapon_hitech);
        wargearCategoryToIconResource.put("Machine", R.drawable.icon_weapon_machine);
        wargearCategoryToIconResource.put("Pistol", R.drawable.icon_weapon_pistol);
        wargearCategoryToIconResource.put("SMG", R.drawable.icon_weapon_smg);
        wargearCategoryToIconResource.put("Rifle", R.drawable.icon_weapon_rifle);
        wargearCategoryToIconResource.put("LMG", R.drawable.icon_weapon_lmg);
        wargearCategoryToIconResource.put("Flame", R.drawable.icon_weapon_flame);
        wargearCategoryToIconResource.put("Shotgun", R.drawable.icon_weapon_shotgun);
        wargearCategoryToIconResource.put("Sniper Rifle", R.drawable.icon_weapon_sniper);
        wargearCategoryToIconResource.put("rpg", R.drawable.icon_weapon_hitech);


    }


    public static Integer getIconResourceForCategory(String category) {
        if(category == null){
            Log.w("wargear mapping", "Asking icon for cagtegory null");
            return null;
        }
        return wargearCategoryToIconResource.get(category);
    }

}
