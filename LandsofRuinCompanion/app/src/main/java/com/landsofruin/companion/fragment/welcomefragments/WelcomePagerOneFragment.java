package com.landsofruin.companion.fragment.welcomefragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.TutorialShowEvent;
import com.landsofruin.companion.eventbus.TutorialShowMoraleInfoEvent;
import com.landsofruin.companion.eventbus.TutorialShowWeaponInfoEvent;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.tutorial.TutorialUtils;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

/**
 *
 */
public class WelcomePagerOneFragment extends Fragment {


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.welcome_one_fragment,
                parent, false);

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
