package com.landsofruin.companion.net;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.utils.GsonProvider;
import com.landsofruin.gametracker.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reader class to read {@link Transition} objects from an {@link InputStream}.
 */
public class TransitionReader {
    private BufferedReader reader;

    public TransitionReader(InputStream inStream) {
        reader = new BufferedReader(
                new InputStreamReader(inStream)
        );
    }

    public Transition readTransition() throws IOException {
        Gson gson = GsonProvider.getGson();

        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        if (BuildConfig.DEBUG) {
            Log.d("network reader", "message  received:\n" + line);
        }

        try {
            Transition ret = gson.fromJson(line, Transition.class);
            return ret;
        } catch (JsonSyntaxException exception) {
            Log.e("transition", "Failed to read transition. Something is not right in the JSON: " + line, exception);
            throw exception;
        }


    }
}
