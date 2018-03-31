package com.landsofruin.companion.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.landsofruin.companion.provider.contracts.CharacterContract;
import com.landsofruin.companion.provider.contracts.GameContract;
import com.landsofruin.companion.provider.contracts.MapContract;
import com.landsofruin.companion.provider.contracts.SnapshotContract;
import com.landsofruin.companion.provider.contracts.TribeContract;

/**
 * SQLite storage implementation for the wastelands content provider.
 */
public class ProviderStorage extends SQLiteOpenHelper {
    private static final String TAG = "LoR/ProviderStorage";

    public static final String DATABASE_NAME = "wastelands";
    public static final int DATABASE_VERSION = 21;

    public ProviderStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createGamesTable(db);
        createSnapshotsTable(db);
        createTribesTable(db);
        createCharactersTable(db);
        createMapsTable(db);
    }

    private void createGamesTable(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + GameContract._TABLE_NAME + " ("
            + GameContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GameContract.LOCAL_IDENTIFIER + " TEXT NOT NULL, "
            + GameContract.SERVER_IDENTIFIER + " TEXT, "
            + GameContract.SYNC_STATE + " INTEGER NOT NULL, "
            + GameContract.GLOBAL_STATE + " INTEGER NOT NULL, "
            + GameContract.TITLE + " TEXT NOT NULL, "
            + GameContract.DATA + " BLOB NOT NULL, "
            + GameContract.ACCOUNT + " TEXT NOT NULL, "
            + GameContract.IS_ONLINE + " INTEGER NOT NULL, "
            + GameContract.UPDATED_AT + " INTEGER NOT NULL, "
            + GameContract.CREATED_AT + " INTEGER NOT NULL"
            + ");"
        );
    }

    private void createSnapshotsTable(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + SnapshotContract._TABLE_NAME + " ("
            + SnapshotContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SnapshotContract.SERVER_GAME_IDENTIFIER + " TEXT NOT NULL, "
            + SnapshotContract.DATA + " BLOB NOT NULL, "
            + SnapshotContract.TYPE_NAME + " TEXT NOT NULL, "
            + SnapshotContract.POSITION + " INTEGER NOT NULL, "
            + SnapshotContract.ACCOUNT + " TEXT NOT NULL, "
            + SnapshotContract.CREATED_AT + " INTEGER NOT NULL"
            + ");"
        );
    }

    private void createTribesTable(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TribeContract._TABLE_NAME + " ("
            + TribeContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TribeContract.ACCOUNT + " TEXT NOT NULL, "
            + TribeContract.SERVER_ID + " TEXT NOT NULL, "
            + TribeContract.NAME + " TEXT NOT NULL, "
            + TribeContract.SNAPSHOT + " BLOB NOT NULL, "
            + TribeContract.VERSION + " TEXT NOT NULL"
            + ");"
        );
    }

    private void createCharactersTable(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + CharacterContract._TABLE_NAME + " ("
            + CharacterContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CharacterContract.SERVER_ID + " TEXT NOT NULL, "
            + CharacterContract.TRIBE_ID + " TEXT NOT NULL, "
            + CharacterContract.NAME + " TEXT NOT NULL, "
            + CharacterContract.SNAPSHOT + " BLOB NOT NULL, "
            + CharacterContract.VERSION + " TEXT NOT NULL, "
            + CharacterContract.ACCOUNT + " TEXT NOT NULL"
            + ");"
        );
    }

    private void createMapsTable(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + MapContract._TABLE_NAME + " ("
            + MapContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MapContract.SERVER_ID + " TEXT NOT NULL, "
            + MapContract.TITLE + " TEXT NOT NULL, "
            + MapContract.SNAPSHOT + " BLOB NOT NULL, "
            + MapContract.VERSION + " TEXT NOT NULL, "
            + MapContract.ACCOUNT + " TEXT NOT NULL"
            + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);

        // XXX: ONLY DURING DEVELOPMENT!

        dropAllTables(db);
        onCreate(db);
    }

    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + GameContract._TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + SnapshotContract._TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TribeContract._TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + CharacterContract._TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + MapContract._TABLE_NAME + ";");
    }
}
