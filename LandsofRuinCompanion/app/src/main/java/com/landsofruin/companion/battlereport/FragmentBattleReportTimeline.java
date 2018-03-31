package com.landsofruin.companion.battlereport;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.gametracker.R;


public class FragmentBattleReportTimeline extends Fragment {
    private static final String ARG_PARAM1 = "battleReportId";

    private String batteReportId;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    public FragmentBattleReportTimeline() {
    }

    public static FragmentBattleReportTimeline newInstance(String batteReportId) {
        FragmentBattleReportTimeline fragment = new FragmentBattleReportTimeline();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batteReportId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            batteReportId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle_report_timeline, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("/battlereports/" + batteReportId + "/events");

//FIXME: update to new Firebase UI
//        adapter = new FirebaseRecyclerAdapter<BattleReportEvent, BattleReportEventViewHolder>(BattleReportEvent.class, R.layout.battle_report_one_event_item, BattleReportEventViewHolder.class, ref) {
//            @Override
//            public void populateViewHolder(BattleReportEventViewHolder battleReportEventViewHolder, final BattleReportEvent battleReportEvent, int position) {
//
//                switch (battleReportEvent.getEventType()) {
//                    case BattleReportEvent.TYPE_ASSIGN_ACTION:
//                        battleReportEventViewHolder.assignActionContainer.setVisibility(View.VISIBLE);
//                        battleReportEventViewHolder.phaseChangeContainer.setVisibility(View.GONE);
//                        battleReportEventViewHolder.performActionContainer.setVisibility(View.GONE);
//                        battleReportEventViewHolder.assignActionText.setText(battleReportEvent.getEventTitle() + " " + battleReportEvent.getActionId());
//                        break;
//                    case BattleReportEvent.TYPE_PHASE_CHANGE:
//                        battleReportEventViewHolder.phaseChangeContainer.setVisibility(View.VISIBLE);
//                        battleReportEventViewHolder.performActionContainer.setVisibility(View.GONE);
//                        battleReportEventViewHolder.assignActionContainer.setVisibility(View.GONE);
//                        battleReportEventViewHolder.phaseChangeText.setText(battleReportEvent.getEventTitle());
//                        break;
//                    case BattleReportEvent.TYPE_PERFORM_ACTION:
//                        battleReportEventViewHolder.performActionContainer.setVisibility(View.VISIBLE);
//                        battleReportEventViewHolder.phaseChangeContainer.setVisibility(View.GONE);
//                        battleReportEventViewHolder.assignActionContainer.setVisibility(View.GONE);
//                        battleReportEventViewHolder.performActionText.setText(battleReportEvent.getEventTitle() + " " + battleReportEvent.getActionId());
//                        break;
//                }
//
//            }
//        };

        recyclerView.setAdapter(adapter);


    }

    public static class BattleReportEventViewHolder extends RecyclerView.ViewHolder {


        private final TextView phaseChangeText;
        private final View phaseChangeContainer;
        private final View performActionContainer;
        private final View assignActionContainer;
        private final TextView assignActionText;
        private final TextView performActionText;

        public BattleReportEventViewHolder(View itemView) {
            super(itemView);
            phaseChangeText = (TextView) itemView.findViewById(R.id.phase_change_text);
            assignActionText = (TextView) itemView.findViewById(R.id.assign_action_text);
            performActionText = (TextView) itemView.findViewById(R.id.perform_action_text);


            phaseChangeContainer = itemView.findViewById(R.id.phase_change);
            performActionContainer = itemView.findViewById(R.id.perform_action);
            assignActionContainer = itemView.findViewById(R.id.assign_action);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        adapter.cleanup();
    }

}
