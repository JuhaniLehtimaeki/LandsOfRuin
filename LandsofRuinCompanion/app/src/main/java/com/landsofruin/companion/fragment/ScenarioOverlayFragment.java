package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.ShowScenarioInfoEvent;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

/**
 *
 */
public class ScenarioOverlayFragment extends Fragment {


    private ViewGroup rootView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.scenario_overlay_fragment,
                parent, false);


        ScenarioDetailsFragment detailsFragment = ScenarioDetailsFragment.newInstance(((GameActivity) getActivity()).getGame().getScenario());
        getChildFragmentManager().beginTransaction().replace(R.id.scenario_content, detailsFragment).commit();

        rootView.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.GONE);
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

    @Subscribe
    public void onShowScenarioInfoEvent(ShowScenarioInfoEvent event) {
        rootView.setVisibility(View.VISIBLE);


    }


}
