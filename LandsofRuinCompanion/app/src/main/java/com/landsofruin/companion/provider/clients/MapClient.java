package com.landsofruin.companion.provider.clients;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.landsofruin.companion.provider.ProviderUris;
import com.landsofruin.companion.provider.contracts.MapContract;
import com.landsofruin.companion.provider.snapshots.MapSnapshot;

public class MapClient {
    private static final String TAG = "LoR/MapClient";

    public static boolean mapExists(ContentProviderClient client, String mapId) {
        Cursor cursor = null;

        try {
            cursor = client.query(
                ProviderUris.getMapsUri(),
                new String[] { MapContract._ID },
                MapContract.SERVER_ID + " = ?",
                new String[] { mapId },
                null
            );

            return cursor.moveToNext();
        } catch(RemoteException exception) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void createMap(ContentProviderClient client, String account, MapSnapshot snapshot, String syncVersion) {
        ContentValues values = snapshot.toContentValues();
        values.put(MapContract.VERSION, syncVersion);
        values.put(MapContract.ACCOUNT, account);

        try {
            client.insert(ProviderUris.getMapsUri(), values);

            Log.d(TAG, "(+) Created map: " + snapshot.getTitle());
        } catch (RemoteException e) {
            Log.w(TAG, "RemoteException during saving map");
        }
    }

    public static void updateMap(ContentProviderClient client, MapSnapshot snapshot, String syncVersion) {
        ContentValues values = snapshot.toContentValues();
        values.put(MapContract.VERSION, syncVersion);

        try {
            client.update(
                ProviderUris.getMapsUri(),
                values,
                MapContract.SERVER_ID + " = ?",
                new String[] { snapshot.getServerId() }
            );

            Log.d(TAG, "(*) Updated map: " + snapshot.getTitle());
        } catch (RemoteException e) {
            Log.d(TAG, "RemoteException during updating map");
        }
    }

    public static int deleteOutdatedMaps(ContentProviderClient client, String account, String version) {
        try {
            return client.delete(
                ProviderUris.getMapsUri(),
                MapContract.VERSION + " != ? AND " + MapContract.ACCOUNT + " == ?",
                new String[] { version, account }
            );
        } catch (RemoteException e) {
            Log.w(TAG, "RemoteException during deleting outdated maps", e);
            return 0;
        }
    }
}
