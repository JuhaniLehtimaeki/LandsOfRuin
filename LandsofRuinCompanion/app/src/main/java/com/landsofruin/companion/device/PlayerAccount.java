package com.landsofruin.companion.device;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.companion.CompanionApplication;

public class PlayerAccount {

    private static DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

    private static String playerScreenName;

    public static String getUniqueIdentifier() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }


    public static void setPlayerScreenName(String screenName) {
        playerScreenName = screenName;
    }

    public static String getPlayerScreenName() {
        return playerScreenName;
    }

    public static String getPlayerName() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public static int getAppVersion() {
        Context context = CompanionApplication.getInstance();

        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0
            ).versionCode;
        } catch (NameNotFoundException exception) {
            throw new AssertionError("Can't get package info about own package");
        }
    }

    public static String getAppVersionTitle() {
        Context context = CompanionApplication.getInstance();

        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0
            ).versionName;
        } catch (NameNotFoundException exception) {
            throw new AssertionError("Can't get package info about own package");
        }
    }
}
