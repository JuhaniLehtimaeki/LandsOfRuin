package com.landsofruin.gametracker.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Utils {

    public static List<Integer> parseIntArrayFromCSVs(String csvs) {
        if (csvs == null) {
            return new ArrayList<>();
        }

        ArrayList<Integer> tmp = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(csvs, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            try {
                tmp.add(Integer.parseInt(token));
            } catch (Exception e) {
                Log.w("utils",
                        "Tried to read int value but failed from Sting: "
                                + csvs + " token: " + token, e);
            }
        }

        return tmp;
    }


    public static List<String> parseStringArrayFromCSVs(String csvs) {
        if (csvs == null) {
            return new ArrayList<>();
        }

        ArrayList<String> tmp = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(csvs, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            tmp.add(token);

        }


        return tmp;
    }

}
