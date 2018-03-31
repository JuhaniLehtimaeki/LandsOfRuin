package com.landsofruin.companion.beta;

import com.landsofruin.companion.state.dice.DiceUtils;

public class BattleNameGenerator {
    private static final String[] PREFIXES = {
        "Battle of",
        "First Battle of",
        "Second Battle of",
        "Third Battle of",
        "Attack on",
        "Operation",
        "Raid on",
        "Siege of",
        "Capture of",
        "Fall of",
    };

    private static final String[] SUFFIXES = {
        "Endt",
        "Endz",
        "Iesto",
        "Jeik",
        "Steg",
        "Denll",
        "Moill",
        "Dangh",
        "Mack",
        "Owary",
        "Tinc",
        "Eiso",
        "Olds",
        "Ykime",
        "Dennt",
        "Elml",
        "Wornd",
        "Phooll",
        "Usulo",
        "Onrd",
        "Dynf",
        "Ages",
        "Aughs",
        "Agary",
        "Vorld",
        "Dould",
        "Chrael",
        "Taylt",
        "Kinm",
        "Anp Urnr",
        "Tialt",
        "Morr",
        "Koent",
        "Chan",
        "Eeri",
        "Odanu",
        "Moch",
        "Ashph",
        "Perb",
        "Saud",
        "Ysere",
        "Slois",
        "Chror",
        "Yauch",
        "Oata",
        "Ulore",
        "Leal",
        "Stuin",
        "Aome",
        "Bannd",
        "Uoma",
        "Zhauch"
    };

    public static String generate() {
        String prefix = PREFIXES[DiceUtils.rollDie(PREFIXES.length) - 1];
        String suffix = SUFFIXES[DiceUtils.rollDie(SUFFIXES.length) - 1];

        return prefix + " " + suffix;
    }
}
