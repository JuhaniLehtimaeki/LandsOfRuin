package com.landsofruin.companion.provider.contracts;

import android.provider.BaseColumns;

public interface CharacterContract extends BaseColumns {
    String _TABLE_NAME = "characters";

    String NAME = "name";
    String SERVER_ID = "server_id";
    String TRIBE_ID = "tribe_id";
    String SNAPSHOT = "snapshot";
    String VERSION = "version";
    String ACCOUNT = "account";

    String[] _ALL = {
        _ID,
        SERVER_ID,
        TRIBE_ID,
        NAME,
        VERSION
    };
}
