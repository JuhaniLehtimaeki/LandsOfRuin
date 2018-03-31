package com.landsofruin.companion.state.proxyobjecthelper;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 30/06/15.
 */
@ObjectiveCName("AccountHelper")
public class AccountHelper {

    private AccountObjectHelper accountObjectHelper;

    private static AccountHelper instance = new AccountHelper();

    public static AccountHelper getInstance() {
        return instance;
    }

    public AccountHelper() {
    }

    public void setAccountObjectHelper(AccountObjectHelper accountObjectHelper) {
        this.accountObjectHelper = accountObjectHelper;
    }

    public AccountObjectHelper getAccountObjectHelper() {
        return accountObjectHelper;
    }
}
