package com.landsofruin.companion.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.charactereffects.CharacterEffectAdapter;
import com.landsofruin.gametracker.transition.TransitionAdapter;

/**
 * Helper class to avoid creating {@link Gson} objects everywhere.
 */
public final class GsonProvider {
    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(Transition.class, new TransitionAdapter())
                .registerTypeAdapter(CharacterEffect.class, new CharacterEffectAdapter())
                .create();
    }

    private GsonProvider() {
    }

    public static Gson getGson() {
        return gson;
    }
}
