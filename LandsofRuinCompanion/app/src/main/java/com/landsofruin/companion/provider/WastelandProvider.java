package com.landsofruin.companion.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.landsofruin.companion.provider.contracts.CharacterContract;
import com.landsofruin.companion.provider.contracts.GameContract;
import com.landsofruin.companion.provider.contracts.MapContract;
import com.landsofruin.companion.provider.contracts.SnapshotContract;
import com.landsofruin.companion.provider.contracts.TribeContract;

/**
 * Content provider for the WASTELANDS(tm).
 *
 * com.landsofruin.wastelands/tribes
 * com.landsofruin.wastelands/characters
 * com.landsofruin.wastelands/games
 * com.landsofruin.wastelands/snapshots
 * com.landsofruin.wastelands/maps
 */
public class WastelandProvider extends ContentProvider {
    public static final String AUTHORITY = "com.landsofruin.wastelands";

    public static final String PATH_TRIBES = "tribes";
    public static final String PATH_CHARACTER = "characters";
    public static final String PATH_GAMES = "games";
    public static final String PATH_SNAPSHOTS = "snapshots";
    public static final String PATH_MAPS = "maps";

    private static final int URI_TRIBES = 1;
    private static final int URI_CHARACTERS = 2;
    private static final int URI_GAMES = 3;
    private static final int URI_SNAPSHOTS = 4;
    private static final int URI_MAPS = 5;

    private static final String MIME_SUBTYPE_TRIBE = "landsofruin.tribe";
    private static final String MIME_SUBTYPE_CHARACTER = "landsofruin.character";
    private static final String MIME_SUBTYPE_GAME = "landsofruin.game";
    private static final String MIME_SUBTYPE_SNAPSHOT = "landsofruin.snapshot";
    private static final String MIME_SUBTYPE_MAPS = "landsofruin.map";

    private Context context;
    private ProviderStorage storage;
    private UriMatcher matcher;

    @Override
    public boolean onCreate() {
        storage = new ProviderStorage(getContext());
        matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, PATH_TRIBES, URI_TRIBES);
        matcher.addURI(AUTHORITY, PATH_CHARACTER, URI_CHARACTERS);
        matcher.addURI(AUTHORITY, PATH_GAMES, URI_GAMES);
        matcher.addURI(AUTHORITY, PATH_SNAPSHOTS, URI_SNAPSHOTS);
        matcher.addURI(AUTHORITY, PATH_MAPS, URI_MAPS);

        context = getContext();

        return true;
    }

    /**
     * Get the MIME type of the data at the given URI.
     *
     * @param uri the URI to query.
     * @return a MIME type string.
     */
    @Override
    public String getType(Uri uri) {
        String multipleItemsPrefix = "vnd.android.cursor.dir/";

        switch (matcher.match(uri)) {
            case URI_TRIBES:
                return multipleItemsPrefix + MIME_SUBTYPE_TRIBE;
            case URI_CHARACTERS:
                return multipleItemsPrefix + MIME_SUBTYPE_CHARACTER;
            case URI_GAMES:
                return multipleItemsPrefix + MIME_SUBTYPE_GAME;
            case URI_SNAPSHOTS:
                return multipleItemsPrefix + MIME_SUBTYPE_SNAPSHOT;
            case URI_MAPS:
                return multipleItemsPrefix + MIME_SUBTYPE_MAPS;
            default:
                throw new IllegalStateException("No type for uri defined: " + uri.toString());
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(getTableByUri(uri));

        SQLiteDatabase db = storage.getReadableDatabase();
        assert db != null;

        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        assert cursor != null;

        cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = storage.getWritableDatabase();
        assert db != null;

        long id = db.insert(getTableByUri(uri), null, values);

        context.getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = storage.getWritableDatabase();
        assert db != null;

        int affectedRows = db.delete(getTableByUri(uri), selection, selectionArgs);
        if (affectedRows > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = storage.getWritableDatabase();
        assert db != null;

        int affectedRows = db.update(getTableByUri(uri), values, selection, selectionArgs);
        if (affectedRows > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    private String getTableByUri(Uri uri) {
        switch (matcher.match(uri)) {
            case URI_TRIBES:
                return TribeContract._TABLE_NAME;
            case URI_CHARACTERS:
                return CharacterContract._TABLE_NAME;
            case URI_GAMES:
                return GameContract._TABLE_NAME;
            case URI_SNAPSHOTS:
                return SnapshotContract._TABLE_NAME;
            case URI_MAPS:
                return MapContract._TABLE_NAME;
            default:
                throw new IllegalStateException("No table for uri defined: " + uri.toString());
        }
    }
}
