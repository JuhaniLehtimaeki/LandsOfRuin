package com.landsofruin.companion;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.ReplaceTutorialEvent;
import com.landsofruin.companion.tutorial.TutorialUtils;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

public class TutorialsActivity extends FragmentActivity {
    private ViewGroup conainerView;
    private TextView tutorialTitle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorials);

        conainerView = (ViewGroup) findViewById(R.id.details_fragment_container);

        tutorialTitle = (TextView) findViewById(R.id.tutorial_title_for_container);

    }

    private void replaceDetailsFragment(int position) {

        conainerView.removeAllViews();


        String tutorialKey = TutorialUtils.getInstance(this).getAllTutorialKeys().get(position);

        View tutorialView = TutorialUtils.getInstance(this).getViewForTutorial(tutorialKey, getLayoutInflater(), conainerView);
        tutorialTitle.setText(TutorialUtils.getInstance(this).getTitleFor(tutorialKey));

        conainerView.addView(tutorialView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onReplaceTutorialEvent(ReplaceTutorialEvent event) {
        replaceDetailsFragment(event.getTutorialPosition());
    }
}