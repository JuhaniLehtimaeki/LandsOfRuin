package com.landsofruin.companion.provider.contracts;

import android.provider.BaseColumns;

public interface GameContract extends BaseColumns {
    String _TABLE_NAME = "games";

    String LOCAL_IDENTIFIER = "local_identifier";
    String SERVER_IDENTIFIER = "server_identifier";
    String GLOBAL_STATE = "global_state";
    String SYNC_STATE = "sync_state";
    String TITLE = "title";
    String DATA = "data";
    String IS_ONLINE = "is_online";
    String ACCOUNT = "account";
    String POSITION = "position";
    String UPDATED_AT = "updated_at";
    String CREATED_AT = "created_at";

    enum _SYNC_STATES {
        UNSYNCHRONIZED(1),
        SYNCHRONIZED(2);

        int value;

        _SYNC_STATES(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    enum _GLOBAL_STATES {
        SETUP(1),
        RUNNING(2),
        FINISHED(3);

        private int value;

        _GLOBAL_STATES(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static _GLOBAL_STATES fromValue(int value) {
            for (_GLOBAL_STATES state : values()) {
                if (state.getValue() == value) {
                    return state;
                }
            }

            throw new IllegalArgumentException("No global state for value: " + value);
        }
    }

    String[] _ALL = {
        _ID,
        LOCAL_IDENTIFIER,
        SERVER_IDENTIFIER,
        GLOBAL_STATE,
        SYNC_STATE,
        TITLE,
        DATA,
        IS_ONLINE,
        UPDATED_AT,
        CREATED_AT
    };
}
