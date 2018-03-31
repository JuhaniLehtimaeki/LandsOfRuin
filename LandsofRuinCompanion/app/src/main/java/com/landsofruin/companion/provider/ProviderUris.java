package com.landsofruin.companion.provider;

import android.net.Uri;

/**
 * Helper class for generating Uri objects to access the wastelands content provider.
 */
public class ProviderUris {
    private static Uri getBaseUri() {
        return new Uri.Builder()
            .scheme("content")
            .authority(WastelandProvider.AUTHORITY)
            .build();
    }

    public static Uri getTribesUri() {
        return getBaseUri().buildUpon()
            .appendPath(WastelandProvider.PATH_TRIBES)
            .build();
    }

    public static Uri getCharactersUri() {
        return getBaseUri().buildUpon()
            .appendPath(WastelandProvider.PATH_CHARACTER)
            .build();
    }

    public static Uri getGamesUri() {
        return getBaseUri().buildUpon()
            .appendPath(WastelandProvider.PATH_GAMES)
            .build();
    }

    public static Uri getSnapshotsUri() {
        return getBaseUri().buildUpon()
            .appendPath(WastelandProvider.PATH_SNAPSHOTS)
            .build();
    }

    public static Uri getMapsUri() {
        return getBaseUri().buildUpon()
            .appendPath(WastelandProvider.PATH_MAPS)
            .build();
    }
}
