package com.landsofruin.companion.net;

import android.util.Log;

import com.google.gson.Gson;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.utils.GsonProvider;
import com.landsofruin.gametracker.BuildConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Writer class to write {@link Transition} objects to an {@link OutputStream}.
 */
public class TransitionWriter {
    private OutputStreamWriter writer;

    public TransitionWriter(OutputStream outStream) {
        writer = new OutputStreamWriter(
                outStream
        );
    }

    public void write(final Transition transition) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = GsonProvider.getGson();

                String message = gson.toJson(transition, Transition.class);
                if (BuildConfig.DEBUG) {
                    Log.d("network writer", "message sent:\n" + message);
                }
                try {
                    writer.write(message);
                    writer.write("\n");
                    writer.flush();
                } catch (Exception e) {
                    Log.e("network", "failed to write transition", e);
                }

            }
        }).start();

    }
}
