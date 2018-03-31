package com.landsofruin.companion.provider.snapshots;


import android.content.ContentValues;
import android.database.Cursor;

import com.landsofruin.companion.provider.contracts.CharacterContract;
import com.landsofruin.companion.provider.contracts.MapContract;
import com.landsofruin.companion.utils.GsonProvider;

public class MapSnapshot {
    private String serverId;
    private String title;
    private int width;
    private int height;
    private int cellSize;
    private String data;
    private String providerURL;
    private String providerIconURL;
    private String providerName;
    private String backgroundImageUrl;

    private MapSnapshot(String serverId, String title, int width, int height, int cellSize, String data, String providerURL, String providerIconURL, String providerName, String backgroundImageUrl) {
        this.serverId = serverId;
        this.title = title;
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.data = data;
        this.providerIconURL = providerIconURL;
        this.providerURL = providerURL;
        this.providerName = providerName;
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getServerId() {
        return serverId;
    }


    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public String getProviderIconURL() {
        return providerIconURL;
    }

    public String getData() {
        return data;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderURL() {
        return providerURL;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCellSize() {
        return cellSize;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(MapContract.SERVER_ID, serverId);
        values.put(MapContract.TITLE, title);
        values.put(MapContract.SNAPSHOT, GsonProvider.getGson().toJson(this));

        return values;
    }

    public static MapSnapshot fromCursor(Cursor cursor) {
        String data = cursor.getString(cursor.getColumnIndex(CharacterContract.SNAPSHOT));
        return GsonProvider.getGson().fromJson(data, MapSnapshot.class);
    }
}
