package com.landsofruin.companion.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.tribemanagement.Tribe;
import com.landsofruin.companion.tribemanagement.UserAccount;

import java.util.ArrayList;

/**
 * Created by juhani on 30/10/2016.
 */

public class UserAccountManager {


    private static UserAccountManager instance = new UserAccountManager();
    private ValueEventListener userAccountValueListener;
    private UserAccount userAccount;
    private ArrayList<AccountRefreshListener> listeners = new ArrayList<>();


    public static UserAccountManager getInstance() {
        return instance;
    }


    public void addAccountRefreshListener(AccountRefreshListener listener) {

        this.listeners.add(listener);
        listener.onAccountRefreshed();
    }

    public void removeAccountRefreshListener(AccountRefreshListener listener) {
        this.listeners.remove(listener);
    }

    private UserAccountManager() {
    }

    /**
     * This must be called before user logout!
     */
    public void clean() {
        this.userAccount = null;

        if (FirebaseAuth.getInstance().getCurrentUser() != null && userAccountValueListener != null) {
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(userAccountValueListener);
        }

        fireListeners();

    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void initialise() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null && userAccountValueListener != null) {
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(userAccountValueListener);
        }

        userAccountValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userAccount = dataSnapshot.getValue(UserAccount.class);


                if (userAccount == null) {
                    // not the most beautiful way to do this but works for now
                    createUserAccount();
                } else {
                    fireListeners();
                }


            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(userAccountValueListener);

    }


    private void fireListeners() {

        for (int i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onAccountRefreshed();
        }
    }

    public interface AccountRefreshListener {
        void onAccountRefreshed();
    }

    public void saveUserAccount() {
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAccount);
    }


    private void createUserAccount() {
        userAccount = new UserAccount();
        userAccount.setUuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        saveUserAccount();
    }


    public void createEmptyDefaultTribe() {


        Tribe tribe = new Tribe();
        tribe.setId("defaultTribe");
        this.userAccount.setDefaultTribe(tribe);

        saveUserAccount();

    }
}
