package com.landsofruin.companion.provider.contracts;

import android.provider.BaseColumns;

public interface SnapshotContract extends BaseColumns {
    String _TABLE_NAME = "snapshots";

    String SERVER_GAME_IDENTIFIER = "server_game_identifier";
    String CREATED_AT = "created_at";
    String TYPE_NAME = "type_name";
    String POSITION = "position";
    String DATA = "data";
    String ACCOUNT = "account";

    String[] _ALL = {
        _ID,
        SERVER_GAME_IDENTIFIER,
        TYPE_NAME,
        DATA,
        POSITION,
        ACCOUNT,
        CREATED_AT
    };
}
