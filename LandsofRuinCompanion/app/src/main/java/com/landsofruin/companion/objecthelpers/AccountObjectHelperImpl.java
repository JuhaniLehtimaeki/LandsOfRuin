package com.landsofruin.companion.objecthelpers;

import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.state.proxyobjecthelper.AccountObjectHelper;

/**
 * Created by juhani on 02/07/15.
 */
public class AccountObjectHelperImpl implements AccountObjectHelper {
    @Override
    public boolean isMe(String identifier) {

        if (PlayerAccount.getUniqueIdentifier() == null) {
            return false;
        }

        return PlayerAccount.getUniqueIdentifier().equals(identifier);

    }
}
