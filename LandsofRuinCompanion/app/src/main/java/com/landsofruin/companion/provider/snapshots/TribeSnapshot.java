package com.landsofruin.companion.provider.snapshots;

import android.content.ContentValues;
import android.database.Cursor;

import com.landsofruin.companion.provider.contracts.TribeContract;
import com.landsofruin.companion.tribemanagement.Tribe;
import com.landsofruin.companion.utils.GsonProvider;

public class TribeSnapshot {
    private String serverId;
    private String name;
    private String smallTribeLogoUri;
    private String largeTribeLogoUri;

    private TribeSnapshot(String serverId, String name, String smallTribeLogoUri, String largeTribeLogoUri) {
        this.serverId = serverId;
        this.name = name;
        this.smallTribeLogoUri = smallTribeLogoUri;
        this.largeTribeLogoUri = largeTribeLogoUri;
    }

    public static TribeSnapshot fromTribe(Tribe tribe) {
        TribeSnapshot ret = new TribeSnapshot(tribe.getId(), tribe.getName(), tribe.getSmallTribeLogoUri(), tribe.getLargeTribeLogoUri());
        return ret;
    }

    public static TribeSnapshot fromCursor(Cursor cursor) {
        String data = cursor.getString(cursor.getColumnIndex(TribeContract.SNAPSHOT));
        return GsonProvider.getGson().fromJson(data, TribeSnapshot.class);
    }


    public String getServerId() {
        return serverId;
    }

    public String getName() {
        return name;
    }

    public String getSmallTribeLogoUri() {
        return smallTribeLogoUri;
    }

    public String getLargeTribeLogoUri() {
        return largeTribeLogoUri;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TribeContract.SERVER_ID, serverId);
        values.put(TribeContract.NAME, name);
        values.put(TribeContract.SNAPSHOT, GsonProvider.getGson().toJson(this));

        return values;
    }
}
