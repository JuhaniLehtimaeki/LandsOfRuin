package com.landsofruin.companion.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

import com.landsofruin.gametracker.R;

public class SoundEffects implements OnLoadCompleteListener {
    private static final String TAG = "LoR/SoundEffects";

    private AudioManager audioManager;
    private SoundPool soundPool;

    private boolean soundsLoaded;
    private int soundYourTurn;
    private int soundOpponentIsWaitingYou;

    public SoundEffects(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        soundPool.setOnLoadCompleteListener(this);

        try {
            soundOpponentIsWaitingYou = soundPool.load(context, R.raw.notificationchime, 0);
            soundYourTurn = soundPool.load(context, R.raw.metalhit, 0);
        } catch (Exception exception) {
            // Had some problems loading sounds on some devices. For now
            // let's just catch any exception and log a warning. We won't
            // play sounds then..
            Log.w(TAG, "Exception during loading sounds into sound pool", exception);
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        soundsLoaded = true;
    }

    public void playYourTurnSound() {
        playSound(soundYourTurn);
    }
    public void playOpponentIsWaitingForYou() {
        playSound(soundOpponentIsWaitingYou);
    }

    private void playSound(int soundId) {
        if (!soundsLoaded) {
            return;
        }

        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;

        soundPool.play(soundId, volume, volume, 1, 0, 1f);
    }
}
