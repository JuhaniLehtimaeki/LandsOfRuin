package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.landsofruin.companion.BaseGameActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.RotateMapEventsEvent;
import com.landsofruin.companion.eventbus.ShowIntelOverlay;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.WorldSection;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 *
 */
public class IntelOverlayFragment extends Fragment {

    private BaseGameActivity activity;
    private ViewGroup rootView;
    private ViewGroup intelMapContainer;
    private IntelCellViewHolder[][] intelCellViewHolders;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (BaseGameActivity) activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.intel_overlay_fragment,
                parent, false);


        intelMapContainer = (ViewGroup) rootView.findViewById(R.id.intel_map_container);

        rootView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOverlay();
            }
        });

        initIntelMap(inflater);

        PlayerState.StartingPosition startPosition = ((BaseGameActivity) getActivity()).getGame().getMe().getStartingPosition();
        if (startPosition == PlayerState.StartingPosition.NORTH || startPosition == PlayerState.StartingPosition.EAST) {
            if (intelMapContainer.getRotation() == 0) {
                intelMapContainer.setRotation(180);
            }
        }

        updateUI();
        return rootView;
    }


    private void initIntelMap(LayoutInflater inflater) {

        GameState game = activity.getGame();

        if (game == null || game.getWorld() == null) {
            Toast.makeText(activity, "Failed load intelligence map, please try again", Toast.LENGTH_SHORT).show();
            return;
        }
        List<List<WorldSection>> sections = game.getWorld().getWorldSections();


        intelCellViewHolders = new IntelCellViewHolder[sections.size()][sections.size()];
        intelMapContainer.removeAllViews();

        for (int j = 0; j < sections.size(); ++j) {

            List<WorldSection> sectionsRow = sections.get(j);
            ViewGroup row = (ViewGroup) inflater.inflate(R.layout.one_intel_row_container, intelMapContainer, false);


            for (int i = 0; i < sectionsRow.size(); ++i) {
                ViewGroup cell = (ViewGroup) inflater.inflate(R.layout.one_intel_cell, row, false);

                boolean isTableSection = false;
                if (i >= 10 && i <= 12 && j >= 10 && j <= 12) {
                    isTableSection = true;
                }


                IntelCellViewHolder cellHolder = new IntelCellViewHolder(cell, isTableSection);
                intelCellViewHolders[j][i] = cellHolder;
                row.addView(cell);
            }

            intelMapContainer.addView(row);

        }


    }


    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);


    }


    @Subscribe
    public void onRotateMapEventsEvent(
            RotateMapEventsEvent event) {


        if (intelMapContainer.getRotation() != 0) {
            intelMapContainer.setRotation(0);
        } else {
            intelMapContainer.setRotation(180);
        }
    }


    private void updateUI() {
        GameState game = activity.getGame();


        if (intelCellViewHolders != null) {
            List<List<WorldSection>> sections = game.getWorld().getWorldSections();

            if (sections == null) {
                return;
            }

            for (int j = 0; j < sections.size(); ++j) {

                List<WorldSection> sectionsRow = sections.get(j);


                for (int i = 0; i < sectionsRow.size(); ++i) {


                    intelCellViewHolders[j][i].setThreatLevel(sectionsRow.get(i).getThreadLevel());

                    if (intelCellViewHolders[j][i].isTableSection) {
                        intelCellViewHolders[j][i].setZombieNumbers(sectionsRow.get(i).getZombiesMoveInThisTurn());
                    } else {
                        intelCellViewHolders[j][i].setZombieNumbers(sectionsRow.get(i).getTotalEnemies());
                    }

                    intelCellViewHolders[j][i].setPulls(sectionsRow.get(i).getPullNorth(), sectionsRow.get(i).getPullSouth(), sectionsRow.get(i).getPullWest(), sectionsRow.get(i).getPullEast());

                }

            }
        }
    }

    private void hideOverlay() {
        if (rootView.getVisibility() == View.GONE) {
            return;
        }

        rootView.setVisibility(View.GONE);
    }


    private void showOverlay() {
        if (rootView.getVisibility() == View.VISIBLE) {
            return;
        }

        rootView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {

        updateUI();
    }

    @Subscribe
    public void onShowIntelOverlay(ShowIntelOverlay event) {

        showOverlay();
    }


    public static class IntelCellViewHolder {
        private final TextView zombieNumbers;
        private final View threatLevelView;
        private final View pullSouthIndicator;
        private final View pullWestIndicator;
        private final View pullEastIndicator;
        private final IntelCellZombieNubersIndicator zombieCountIndicator;
        private View rootView;

        private View pullNorthIndicator;

        private boolean isTableSection = false;

        public IntelCellViewHolder(View rootView, boolean isTableSection) {
            this.rootView = rootView;
            zombieNumbers = (TextView) this.rootView.findViewById(R.id.zombie_numbers);
            threatLevelView = this.rootView.findViewById(R.id.threat_level_indicator);
            this.pullNorthIndicator = this.rootView.findViewById(R.id.pull_north);
            this.pullSouthIndicator = this.rootView.findViewById(R.id.pull_south);
            this.pullWestIndicator = this.rootView.findViewById(R.id.pull_west);
            this.pullEastIndicator = this.rootView.findViewById(R.id.pull_east);

            this.zombieCountIndicator = (IntelCellZombieNubersIndicator) this.rootView.findViewById(R.id.count_indicator);

            this.isTableSection = isTableSection;

            if (isTableSection) {
                drawMapSection();
            }


        }

        private void drawMapSection() {
            rootView.setBackgroundColor(0xff000000);
            zombieNumbers.setVisibility(View.VISIBLE);
            threatLevelView.setVisibility(View.GONE);
        }

        public void setZombieNumbers(int zombies) {
            zombieNumbers.setText("" + zombies);
            zombieCountIndicator.setZombieCount(zombies);
            zombieCountIndicator.invalidate();


        }

        public void setThreatLevel(int threatLevel) {
            float percentage = threatLevel / 20f;
            threatLevelView.setBackgroundColor(Color.rgb((int) (150 * percentage), 150 - (int) (150 * percentage), 0));

        }

        public void setPulls(int pullNorth, int pullSouth, int pullWest, int pullEast) {


            this.pullNorthIndicator.setBackgroundColor(Color.rgb((int) (255 * (pullNorth / 20f)), 0, 0));
            this.pullSouthIndicator.setBackgroundColor(Color.rgb((int) (255 * (pullSouth / 20f)), 0, 0));
            this.pullWestIndicator.setBackgroundColor(Color.rgb((int) (255 * (pullWest / 20f)), 0, 0));
            this.pullEastIndicator.setBackgroundColor(Color.rgb((int) (255 * (pullEast / 20f)), 0, 0));

        }

    }

}
