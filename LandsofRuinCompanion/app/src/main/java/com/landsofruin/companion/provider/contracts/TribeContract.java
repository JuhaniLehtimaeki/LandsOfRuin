package com.landsofruin.companion.provider.contracts;

import android.provider.BaseColumns;

public interface TribeContract extends BaseColumns {
    String _TABLE_NAME = "tribes";

    String ACCOUNT = "account";
    String NAME = "name";
    String SERVER_ID = "server_id";
    String SNAPSHOT = "snapshot";
    String VERSION = "version";

    String[] _ALL = {
        _ID,
        SERVER_ID,
        NAME,
        SNAPSHOT,
        VERSION
    };
}
