package com.landsofruin.companion.fragment.welcomefragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.landsofruin.companion.GameLobbyActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.gametracker.R;

/**
 *
 */
public class WelcomePagerLoginFragment extends Fragment {


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.welcome_login_fragment,
                parent, false);


        rootView.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameLobbyActivity) getActivity()).startLogin();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);


    }


    @Override
    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

}
