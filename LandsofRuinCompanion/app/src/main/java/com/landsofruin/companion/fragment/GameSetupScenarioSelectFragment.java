package com.landsofruin.companion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.ChangeScenarioTransition;
import com.landsofruin.companion.state.transition.ReadySetupScenarioSelectTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.scenarios.ScenariosManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class GameSetupScenarioSelectFragment extends Fragment {

    private View doneButton;
    private TextView validationText;
    private RecyclerView scenariosRecyclerView;
    private ScenariosAdapter adapter;

    public static GameSetupScenarioSelectFragment newInstance() {
        GameSetupScenarioSelectFragment fragment = new GameSetupScenarioSelectFragment();
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
        View view = inflater.inflate(R.layout.fragment_game_setup_select_scenario, parent, false);

        view.findViewById(R.id.non_admin_fragment_container);


        doneButton = view.findViewById(R.id.done_button);
        scenariosRecyclerView = (RecyclerView) view.findViewById(R.id.scenarios_recycler_view);

        if (isAdmin()) {
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendToServer(new ReadySetupScenarioSelectTransition());
                }
            });

            setupScenarioSelector();


        } else {
            view.findViewById(R.id.non_admin_info).setVisibility(View.VISIBLE);
        }

        doneButton.setEnabled(false);


        if (getGame() == null) {
            getActivity().finish();
            return view;
        }


        validationText = (TextView) view.findViewById(R.id.scenario_validation_text);


        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        TextView scenatioTitleText = (TextView) view.findViewById(R.id.scenario_title);
        TextView mapTitleText = (TextView) view.findViewById(R.id.map_title);
        TextView teamTitleText = (TextView) view.findViewById(R.id.team_title);
        TextView overviewTitleText = (TextView) view.findViewById(R.id.overview_title);
        TextView startGameTitleText = (TextView) view.findViewById(R.id.start_game_title);

        scenatioTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        mapTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        teamTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        overviewTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        startGameTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));

        updateUI();


        return view;
    }


    private void setupScenarioSelector() {


        scenariosRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new ScenariosAdapter(getActivity(), this);
        adapter.setSelected(getGame().getScenario());
        scenariosRecyclerView.setAdapter(adapter);


    }

    private void validateState() {
        Scenario scenario = ScenariosManager.getInstance().getScenarioByID(getGame().getScenario());
        if (scenario == null) {
            doneButton.setEnabled(false);
            validationText.setText("Please select scenario");
        } else {
            doneButton.setEnabled(true);
            validationText.setText("Press ready to continue");
        }

    }

    private GameState getGame() {
        return ((GameSetupActivity) getActivity()).getGame();
    }

    private void sendToServer(Transition transition) {
        ((GameSetupActivity) getActivity()).sendToServer(transition);
    }

    private void onPickScenario(int position) {
        if (getActivity() == null) {
            return;
        }

        Scenario scenario = ScenariosManager.getInstance().getScenarios().get(position);
        int scenarioId = scenario.getId();

        if (LookupHelper.getInstance().getScenarioFor(getGame().getScenario()) == null || scenarioId != LookupHelper.getInstance().getScenarioFor(getGame().getScenario()).getId()) {
            ChangeScenarioTransition transition = new ChangeScenarioTransition(scenarioId);
            sendToServer(transition);
        }
    }


    private void onPickScenarioWithId(int id) {
        if (getActivity() == null) {
            return;
        }


        if (LookupHelper.getInstance().getScenarioFor(getGame().getScenario()) == null || id != LookupHelper.getInstance().getScenarioFor(getGame().getScenario()).getId()) {
            ChangeScenarioTransition transition = new ChangeScenarioTransition(id);
            sendToServer(transition);
        }
    }


    private boolean isAdmin() {
        return ((GameSetupActivity) getActivity()).isAdmin();
    }

    private int getSelectedIndex() {
        GameState game = getGame();

        if (LookupHelper.getInstance().getScenarioFor(game.getScenario()) == null) {
            return -1;
        }

        for (int i = 0; i < ScenariosManager.getInstance().getScenarios().size(); ++i) {
            if (ScenariosManager.getInstance().getScenarios().get(i).getId() == game.getScenario()) {
                return i;

            }
        }
        return -1;
    }

    private void updateUI() {
        if (isAdmin()) {
            validateState();
        }

    }

    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();
    }


    public boolean isPlayerRolesReady(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {
            if (player.getScenarioPlayerRole() == -1) {
                return false;
            }
        }
        return gameState.isScenarioPlayerRolesReady();
    }


    public static class ScenariosAdapter extends RecyclerView.Adapter<ScenariosAdapter.ViewHolder> {

        private int selected = -1;

        private Context context;
        private final LinkedList<Scenario> scenarios;
        private GameSetupScenarioSelectFragment parent;


        public ScenariosAdapter(Context context, final GameSetupScenarioSelectFragment parent) {
            scenarios = (LinkedList<Scenario>) ScenariosManager.getInstance().getScenarios().clone();

            Collections.sort(scenarios, new Comparator<Scenario>() {
                @Override
                public int compare(Scenario scenario, Scenario t1) {
                    int currentPlayers = parent.getGame().getPlayers().size();
                    boolean scenarioAvailable = true;
                    int minCount = scenario.getMinPlayerCount();
                    int maxCount = scenario.getMaxPlayerCount();
                    if (minCount > currentPlayers || (maxCount != -1 && maxCount < currentPlayers)) {
                        scenarioAvailable = false;
                    }


                    boolean scenariot1Available = true;
                    int minCountt1 = t1.getMinPlayerCount();
                    int maxCountt1 = t1.getMaxPlayerCount();
                    if (minCountt1 > currentPlayers || (maxCountt1 != -1 && maxCountt1 < currentPlayers)) {
                        scenariot1Available = false;
                    }


                    if (scenarioAvailable && !scenariot1Available) {
                        return -1;
                    } else if (!scenarioAvailable && scenariot1Available) {
                        return 1;
                    }


                    return scenario.getName().compareTo(t1.getName());
                }
            });


            this.parent = parent;
            this.context = context;
        }

        public void setSelected(int selected) {
            this.selected = selected;
            notifyDataSetChanged();
        }

        public int getSelected() {
            return selected;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.one_scenario_selector_scenario, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final Scenario scenario = this.scenarios.get(position);

            int currentPlayers = parent.getGame().getPlayers().size();
            boolean scenarioAvailable = true;

            Picasso.with(context).load(scenario.getScenarioImageUri()).into(holder.scenarioImage);
            holder.scenarioTitle.setText(scenario.getName());
            holder.scenarioDescription.setText(scenario.getDescription());


            int minCount = scenario.getMinPlayerCount();
            int maxCount = scenario.getMaxPlayerCount();

            if (maxCount == -1) { // unlimited
                holder.playerCount.setText("For " + minCount + "+ players");
            } else if (maxCount == minCount) {
                holder.playerCount.setText("For " + minCount + " player" + (maxCount > 1 ? "s" : ""));
            } else {
                holder.playerCount.setText("For " + minCount + " to " + maxCount + " players");
            }

            if (minCount > currentPlayers || (maxCount != -1 && maxCount < currentPlayers)) {
                scenarioAvailable = false;
            }


            if (scenarioAvailable) {
                holder.itemView.setAlpha(1f);
                holder.scenarioImage.setAlpha(0.5f);
                holder.playerCount.setTextColor(0xFFFFFFFF);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selected = scenario.getId();
                        parent.onPickScenarioWithId(selected);
                        notifyDataSetChanged();
                        parent.validateState();
                    }
                });

            } else {
                holder.itemView.setAlpha(0.5f);
                holder.scenarioImage.setAlpha(1f);
                holder.playerCount.setTextColor(0xFFFF0000);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
            if (scenario.getId() == selected) {
                holder.selectedHighlight.setVisibility(View.VISIBLE);
            } else {
                holder.selectedHighlight.setVisibility(View.GONE);
            }


        }

        @Override
        public int getItemCount() {
            return scenarios.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final ImageView scenarioImage;
            private final TextView scenarioTitle;
            private final TextView scenarioDescription;
            private final TextView playerCount;
            private final View selectedHighlight;

            public ViewHolder(View v) {
                super(v);


                this.scenarioImage = (ImageView) v.findViewById(R.id.scenario_image);
                this.scenarioTitle = (TextView) v.findViewById(R.id.scenario_title);
                this.scenarioDescription = (TextView) v.findViewById(R.id.scenario_description);
                this.playerCount = (TextView) v.findViewById(R.id.players_count);
                this.selectedHighlight = v.findViewById(R.id.selected_highlight);


            }
        }
    }

}
