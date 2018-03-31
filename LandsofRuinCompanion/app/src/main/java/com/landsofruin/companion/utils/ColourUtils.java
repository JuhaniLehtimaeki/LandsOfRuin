package com.landsofruin.companion.utils;

/**
 * Created by juhani on 09/12/14.
 */
public abstract class ColourUtils {


    public static int getTextColourForTargetNumber(int targetNumber) {

        return 0xFFFFFFFF;

//        if (targetNumber < 10) {
//            return 0xFFdddddd;
//        } else if (targetNumber < 15) {
//            return 0xFF111111;
//        } else {
//            return 0xFFdddddd;
//        }

    }


    public static int getColourForTargetNumber(int targetNumber) {

        if (targetNumber < 10) {
            return 0x9902b91b;
        } else if (targetNumber < 15) {
            return 0x99e4b600;
        } else {
            return 0x99e23024;
        }

    }


    public static int getTextColourForNoise(int noise) {

        return 0xFFFFFFFF;

//        if (noise < 100) {
//            return 0xFFdddddd;
//        } else if (noise < 160) {
//            return 0xFF111111;
//        } else {
//            return 0xFFdddddd;
//        }

    }

    public static int getColourForNoise(int noise) {

        if (noise < 100) {
            return 0x9902b91b;
        } else if (noise < 160) {
            return 0x99e4b600;
        } else {
            return 0x99e23024;
        }

    }
}
