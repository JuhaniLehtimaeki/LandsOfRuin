package com.landsofruin.companion.provider.clients;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.landsofruin.companion.provider.ProviderUris;
import com.landsofruin.companion.provider.contracts.TribeContract;
import com.landsofruin.companion.provider.snapshots.TribeSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Client class to manage the character data in the wastelands content provider.
 */
public class TribeClient {
    private static final String TAG = "LoR/TribeClient";

    public static TribeSnapshot getTribeByServerId(ContentResolver resolver, String id) {
        Cursor cursor = null;

        try {
            cursor = resolver.query(
                ProviderUris.getTribesUri(),
                TribeContract._ALL,
                TribeContract.SERVER_ID + " = ?",
                new String[] { id },
                null
            );

            if (cursor == null || !cursor.moveToNext()) {
                return null;
            }

            return TribeSnapshot.fromCursor(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean tribeExists(ContentProviderClient client, String id) {
        Cursor cursor = null;

        try {
            cursor = client.query(
                ProviderUris.getTribesUri(),
                new String[] { TribeContract._ID },
                TribeContract.SERVER_ID + " = ?",
                new String[] { id },
                null
            );

            return cursor.moveToNext();
        } catch (RemoteException e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void createTribe(ContentProviderClient client, String account, TribeSnapshot snapshot, String version) {
        ContentValues values = snapshot.toContentValues();
        values.put(TribeContract.ACCOUNT, account);
        values.put(TribeContract.VERSION, version);

        try {
            client.insert(ProviderUris.getTribesUri(), values);

            Log.d(TAG, "Created new tribe: " + snapshot.getName());
        } catch (RemoteException e) {
            // Ignore for now!
            Log.e(TAG, "RemoteException during saving tribe", e);
        }
    }

    public static int deleteOutdatedTribes(ContentProviderClient client, String account, String version) {
        try {
            return client.delete(
                ProviderUris.getTribesUri(),
                TribeContract.VERSION + " != ? AND " + TribeContract.ACCOUNT + " == ?",
                new String[] { version, account }
            );
        } catch (RemoteException e) {
            // Ignore for now!
            Log.e(TAG, "RemoteException during deleting outdated tribes", e);
            return 0;
        }
    }

    public static void updateTribe(ContentProviderClient client, String account, TribeSnapshot snapshot, String version) {
        ContentValues values = snapshot.toContentValues();
        values.put(TribeContract.ACCOUNT, account);
        values.put(TribeContract.VERSION, version);

        try {
            client.update(
                ProviderUris.getTribesUri(),
                values,
                TribeContract.SERVER_ID + " = ?",
                new String[] { snapshot.getServerId() }
            );

            Log.d(TAG, "Updated tribe: " + snapshot.getName());
        } catch (RemoteException e) {
            // Ignore for now!
            Log.e(TAG, "RemoteException during updating tribe", e);
        }
    }

    public static List<String> getTribeServerIds(ContentProviderClient client, String account) {
        List<String> tribeIds = new ArrayList<String>();

        Cursor cursor = null;

        try {
            cursor = client.query(
                ProviderUris.getTribesUri(),
                new String[] { TribeContract.SERVER_ID },
                TribeContract.ACCOUNT + " = ?",
                new String[] { account },
                null
            );

            while (cursor.moveToNext()) {
                tribeIds.add(
                    cursor.getString(cursor.getColumnIndex(TribeContract.SERVER_ID))
                );
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during getting server ids for tribes", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tribeIds;
    }

    public static List<TribeSnapshot> getTribes(ContentResolver resolver, String account) {
        List<TribeSnapshot> tribes = new ArrayList<TribeSnapshot>();

        Cursor cursor = null;

        try {
            cursor = resolver.query(
                ProviderUris.getTribesUri(),
                TribeContract._ALL,
                TribeContract.ACCOUNT + " = ?",
                new String[] { account },
                TribeContract.NAME + " ASC"
            );

            while (cursor.moveToNext()) {
                tribes.add(TribeSnapshot.fromCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tribes;
    }
}
