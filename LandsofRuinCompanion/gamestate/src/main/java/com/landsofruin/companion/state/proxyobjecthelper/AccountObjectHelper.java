package com.landsofruin.companion.state.proxyobjecthelper;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 30/06/15.
 */
@ObjectiveCName("AccountObjectHelper")
public interface AccountObjectHelper {

    boolean isMe(String identifier);
}
