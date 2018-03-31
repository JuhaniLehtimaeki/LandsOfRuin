package com.landsofruin.companion.provider.contracts;

import android.provider.BaseColumns;

public interface MapContract extends BaseColumns {
    String _TABLE_NAME = "maps";

    String TITLE = "title";
    String SERVER_ID = "server_id";
    String SNAPSHOT = "snapshot";
    String VERSION = "version";
    String ACCOUNT = "account";

    String[] _ALL = {
        _ID,
        SERVER_ID,
        TITLE,
        VERSION,
        ACCOUNT
    };
}
