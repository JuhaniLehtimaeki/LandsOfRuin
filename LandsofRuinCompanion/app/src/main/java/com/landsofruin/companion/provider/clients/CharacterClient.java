package com.landsofruin.companion.provider.clients;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.landsofruin.companion.provider.ProviderUris;
import com.landsofruin.companion.provider.contracts.CharacterContract;
import com.landsofruin.companion.provider.snapshots.CharacterSnapshot;

public class CharacterClient {
    private static final String TAG = "LoR/CharacterClient";

    public static boolean characterExists(ContentProviderClient client, String id) {
        Cursor cursor = null;

        try {
            cursor = client.query(
                ProviderUris.getCharactersUri(),
                new String[] { CharacterContract._ID },
                CharacterContract.SERVER_ID + " = ?",
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

    public static void createCharacter(ContentProviderClient client, String account, String tribeId, CharacterSnapshot snapshot, String version) {
        ContentValues values = snapshot.toContentValues();
        values.put(CharacterContract.TRIBE_ID, tribeId);
        values.put(CharacterContract.VERSION, version);
        values.put(CharacterContract.ACCOUNT, account);

        try {
            client.insert(ProviderUris.getCharactersUri(), values);

            Log.d(TAG, "(+) Created character: " + snapshot.getName());
        } catch (RemoteException e) {
            Log.w(TAG, "RemoteException during saving character", e);
        }
    }

    public static void updateCharacter(ContentProviderClient client, String tribeId, CharacterSnapshot snapshot, String version) {
        ContentValues values = snapshot.toContentValues();
        values.put(CharacterContract.TRIBE_ID, tribeId);
        values.put(CharacterContract.VERSION, version);

        try {
            client.update(
                ProviderUris.getCharactersUri(),
                values,
                CharacterContract.SERVER_ID + " = ?",
                new String[] { snapshot.getServerId() }
            );

            Log.d(TAG, "(*) Updated character: " + snapshot.getName());
        } catch (RemoteException e) {
            Log.w(TAG, "RemoteException during updating character", e);
        }
    }

    public static int deleteOutdatedCharacters(ContentProviderClient client, String account, String version) {
        try {
            return client.delete(
                ProviderUris.getCharactersUri(),
                CharacterContract.VERSION + " != ? AND " + CharacterContract.ACCOUNT + " == ?",
                new String[] { version, account }
            );
        } catch (RemoteException e) {
            Log.w(TAG, "RemoteException during deleting outdated characters", e);
            return 0;
        }
    }
}
