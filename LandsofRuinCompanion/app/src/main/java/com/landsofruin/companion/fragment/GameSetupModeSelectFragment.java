package com.landsofruin.companion.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.state.transition.ChangeGameModeTransition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.CirclePageIndicator;

public class GameSetupModeSelectFragment extends Fragment {


    private ScreenSlidePagerAdapter adapter;
    private ViewPager pager;
    private TextView modeName;
    private TextView modeText;


    public static GameSetupModeSelectFragment newInstance() {
        GameSetupModeSelectFragment fragment = new GameSetupModeSelectFragment();
        return fragment;
    }

    private static String getModeText(Context context, int mode) {
        switch (mode) {
            case GameState.GAME_MODE_ONLINE:
                return context.getString(R.string.game_mode_text_online);
            case GameState.GAME_MODE_BASIC:
                return context.getString(R.string.game_mode_text_basic);
            case GameState.GAME_MODE_ADVANCED:
                return context.getString(R.string.game_mode_text_advanced);
        }
        return "";
    }

    private static String getModeName(Context context, int mode) {
        switch (mode) {
            case GameState.GAME_MODE_ONLINE:
                return context.getString(R.string.game_mode_name_online);
            case GameState.GAME_MODE_BASIC:
                return context.getString(R.string.game_mode_name_basic);
            case GameState.GAME_MODE_ADVANCED:
                return context.getString(R.string.game_mode_name_advanced);
        }
        return "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_setup_select_gamemode, parent, false);

        modeName = (TextView) view.findViewById(R.id.mode_name);
        modeText = (TextView) view.findViewById(R.id.mode_text);

        if (isAdmin()) {


            int selectedIndex = getGame().getGameMode();

            pager = (ViewPager) view.findViewById(R.id.pager);

            modeName.setVisibility(View.GONE);
            modeText.setVisibility(View.GONE);
            adapter = new ScreenSlidePagerAdapter(getActivity(), getChildFragmentManager());
            pager.setAdapter(adapter);

            CirclePageIndicator titleIndicator = (CirclePageIndicator) view.findViewById(R.id.pager_indicator);
            titleIndicator.setViewPager(pager);


            titleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i2) {

                }

                @Override
                public void onPageSelected(int i) {
                    onPickMode();
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            if (selectedIndex == -1) {
                selectedIndex = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPickMode();
                    }
                }, 1000);

            }

            pager.setCurrentItem(selectedIndex);
        } else {
            view.findViewById(R.id.pager_indicator).setVisibility(View.GONE);
            view.findViewById(R.id.pager).setVisibility(View.GONE);
        }


        updateUI();
        return view;
    }

    private GameState getGame() {
        return ((GameSetupActivity) getActivity()).getGame();
    }

    private void sendToServer(Transition transition) {
        ((GameSetupActivity) getActivity()).sendToServer(transition);
    }

    private void onPickMode() {
        int gamemode = this.adapter.getModeAt(pager.getCurrentItem());
        ChangeGameModeTransition transition = new ChangeGameModeTransition(gamemode);
        sendToServer(transition);
    }

    private boolean isAdmin() {
        return ((GameSetupActivity) getActivity()).isAdmin();
    }

    private void updateUI() {
        GameState game = getGame();

        modeName.setText(getModeName(getActivity(), game.getGameMode()));
        modeText.setText(getModeText(getActivity(), game.getGameMode()));

    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();
    }

    public static class PagerModeFragment extends Fragment {

        private int gameMode;
        private GameState game;
        private TextView modeText;
        private TextView modeName;
        private Context context;

        private void updateUI() {
            if (modeName != null) {
                modeName.setText(getModeName(context, gameMode));
                modeText.setText(getModeText(context, gameMode));
            }
        }

        public void setMode(int gamemode, GameState game, Context context) {
            this.gameMode = gamemode;
            this.game = game;
            this.context = context;
            updateUI();
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_game_setup_select_mode_selector_fragment, parent, false);

            modeName = (TextView) view.findViewById(R.id.mode_name);
            modeText = (TextView) view.findViewById(R.id.mode_text);

            updateUI();
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            updateUI();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Context context;

        public ScreenSlidePagerAdapter(Context context, FragmentManager fm) {
            super(fm);

            this.context = context;

        }


        @Override
        public Fragment getItem(int position) {
            PagerModeFragment ret = new PagerModeFragment();

            int mode = GameState.GAME_MODE_BASIC;
            switch (position) {

                case 0:
                    mode = GameState.GAME_MODE_BASIC;
                    break;
                case 1:
                    mode = GameState.GAME_MODE_ADVANCED;
                    break;
                case 2:
                    mode = GameState.GAME_MODE_ONLINE;
                    break;
            }

            ret.setMode(mode, getGame(), context);

            return ret;
        }

        @Override
        public int getCount() {
            return 2;
            //FIXME: change to 3 when online mode comes back
        }

        public int getModeAt(int position) {
            int mode = GameState.GAME_MODE_BASIC;
            switch (position) {
                case 0:
                    mode = GameState.GAME_MODE_BASIC;
                    break;
                case 1:
                    mode = GameState.GAME_MODE_ADVANCED;
                    break;
                case 2:
                    mode = GameState.GAME_MODE_ONLINE;
                    break;
            }
            return mode;
        }
    }
}
