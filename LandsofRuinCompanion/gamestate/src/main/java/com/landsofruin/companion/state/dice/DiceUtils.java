package com.landsofruin.companion.state.dice;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.LinkedList;
import java.util.Random;

@ObjectiveCName("DiceUtils")
public class DiceUtils {


    private static final Random random = new Random();
    private static LinkedList<Integer> diceColors = new LinkedList<Integer>();

    static {
        diceColors.add(0xFF5a254f);
        diceColors.add(0xFFb22d1e);
        diceColors.add(0xFF026e3a);
        diceColors.add(0xFF1056ac);
        diceColors.add(0xFF030303);
        diceColors.add(0xFFd6dbd4);
    }

    public static int rollDie(int d) {
        if (d == 0) {
            return 0;
        }

        return random.nextInt(d) + 1;
    }

    public static LinkedList<Integer> getDiceColors() {
        return diceColors;
    }

}
