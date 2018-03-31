package com.landsofruin.companion.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.beta.BetaMaps;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.map.MapView;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.transition.ChangeMapTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.R;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.LinkedList;

public class GameSetupTableSelectFragment extends Fragment {


    private ViewPager pager;
    private CirclePageIndicator titleIndicator;
    private ViewGroup groups;


    public static GameSetupTableSelectFragment newInstance() {
        GameSetupTableSelectFragment fragment = new GameSetupTableSelectFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
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
        View view = inflater.inflate(R.layout.fragment_game_setup_select_table, parent, false);

        pager = (ViewPager) view.findViewById(R.id.pager);
        titleIndicator = (CirclePageIndicator) view.findViewById(R.id.pager_indicator);

        groups = (ViewGroup) view.findViewById(R.id.groups);

        setMaps(null);
        updateUI();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BusProvider.getInstance().post(new FragmentRefreshEvent());
            }
        }, 1000);

        view.findViewById(R.id.all_maps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMaps(null);
            }
        });


        LinkedList<String> handled = new LinkedList<>();
        final MapState[] beatamaps = BetaMaps.createMaps();
        for (int i = 0; i < beatamaps.length; ++i) {
            TextView mapGroup = (TextView) inflater.inflate(R.layout.one_map_group, groups, false);

            if (handled.contains(beatamaps[i].getGroupName())) {
                continue;
            }
            handled.add(beatamaps[i].getGroupName());
            mapGroup.setText(beatamaps[i].getGroupName());
            final int index = i;
            mapGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMaps(beatamaps[index].getGroupName());
                }
            });

            groups.addView(mapGroup);
        }

        return view;
    }


    private void setMaps(String category) {

        int selectedIndex = -1;

        if (isAdmin()) {


            ArrayList<MapState> maps = new ArrayList<>();
            MapState selectedMap = getGame().getMap();

            int index = 0;
            MapState[] beatamaps = BetaMaps.createMaps();
            for (int i = 0; i < beatamaps.length; ++i) {

                if (category == null || category.equals(beatamaps[i].getGroupName())) {
                    maps.add(beatamaps[i]);

                    if (selectedIndex == -1 && beatamaps[i].equals(selectedMap)) {
                        selectedIndex = index;
                    }
                    ++index;
                }
            }


            if (selectedIndex == -1) {
                selectedIndex = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPickMap();
                    }
                }, 1000);

            }

            ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
            adapter.setMaps(maps);
            pager.setAdapter(adapter);


            pager.setCurrentItem(selectedIndex);


            titleIndicator.setViewPager(pager);

            titleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i2) {

                }

                @Override
                public void onPageSelected(int i) {
                    onPickMap();
                    BusProvider.getInstance().post(new FragmentRefreshEvent());
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

        } else {
            pager.setVisibility(View.GONE);
        }


    }


    private GameState getGame() {
        return ((GameSetupActivity) getActivity()).getGame();
    }


    private void sendToServer(Transition transition) {
        if (getActivity() != null) {
            ((GameSetupActivity) getActivity()).sendToServer(transition);
        }
    }

    private void onPickMap() {

        MapState map = ((ScreenSlidePagerAdapter) pager.getAdapter()).getMapAt(pager.getCurrentItem());

        BusProvider.getInstance().post(new MapChangedEvent(map));


    }


    private boolean isAdmin() {
        return ((GameSetupActivity) getActivity()).isAdmin();
    }

    private void updateUI() {

    }


    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();
    }


    public static class PagerMapFragment extends Fragment {

        private MapState map;
        private MapView mapView;
        private GameState game;
        private TextView mapName;
        private TextView providerName;
        private TextView providerUrl;
        private ImageView providerIcon;
        private View mapContainer;
        private ImageView mapBackground;


        private void updateUI() {

            if (this.map != null && this.mapView != null) {
                mapView.setGame(game);
                mapView.setMap(map);

                if (map.getBackgroundImageURL() != null) {

                    Picasso.with(getActivity()).load(map.getBackgroundImageURL()).into(new Target() {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            if (getActivity() == null) {
                                return;
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                mapView.setBackground(new BitmapDrawable(getResources(), bitmap));
                            } else {
                                mapView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                            }

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                            mapView.setBackgroundResource(R.drawable.map_background);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                } else {
                    mapView.setBackgroundResource(R.drawable.map_background);
                }
                mapName.setText(map.getName());


                if (map.getProviderName() != null) {
                    providerName.setText("by " + map.getProviderName());
                    providerName.setVisibility(View.VISIBLE);
                } else {
                    providerName.setVisibility(View.GONE);
                }


                if (map.getProviderURL() != null) {
                    providerUrl.setText("Buy online");
                    providerUrl.setPaintFlags(providerUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    providerUrl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(map.getProviderURL()));
                            startActivity(intent);
                        }
                    });
                    providerUrl.setVisibility(View.VISIBLE);
                } else {
                    providerUrl.setVisibility(View.GONE);
                }

                if (map.getProviderIconURL() != null) {
                    providerIcon.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(map.getProviderIconURL()).into(providerIcon);

                } else {
                    providerIcon.setVisibility(View.GONE);
                }

                this.mapView.invalidate();
            }
        }

        public void setMap(MapState map, GameState game) {
            this.map = map;
            this.game = game;

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_game_setup_select_table_selector_fragment, parent, false);


            mapView = (MapView) view.findViewById(R.id.map);
            mapContainer = view.findViewById(R.id.map_container);
            mapBackground = (ImageView) view.findViewById(R.id.map_background);


            mapName = (TextView) view.findViewById(R.id.table_name);
            providerName = (TextView) view.findViewById(R.id.provider_name);
            providerUrl = (TextView) view.findViewById(R.id.provider_url);
            providerIcon = (ImageView) view.findViewById(R.id.provider_icon);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            }, 500);

            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
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
        public void onFragmentRefreshEvent(FragmentRefreshEvent event) {
            updateUI();
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<MapState> maps = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setMaps(ArrayList<MapState> maps) {
            this.maps = maps;
        }

        @Override
        public Fragment getItem(int position) {
            PagerMapFragment ret = new PagerMapFragment();
            ret.setMap(this.maps.get(position), getGame());

            return ret;
        }

        @Override
        public int getCount() {
            return maps.size();
        }

        public MapState getMapAt(int postion) {
            return this.maps.get(postion);
        }
    }


    public static class FragmentRefreshEvent {

    }


    public static class MapChangedEvent{
        private MapState map;


        public MapChangedEvent(MapState map) {
            this.map = map;
        }

        public MapState getMap() {
            return map;
        }
    }

}
