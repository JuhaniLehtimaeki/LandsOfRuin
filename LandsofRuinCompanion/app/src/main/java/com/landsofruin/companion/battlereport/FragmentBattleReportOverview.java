package com.landsofruin.companion.battlereport;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.AppConstants;
import com.landsofruin.companion.state.battlereport.BattleReport;
import com.landsofruin.gametracker.R;


public class FragmentBattleReportOverview extends Fragment {
    private static final String ARG_PARAM1 = "battleReportId";

    private String battleReportId;
    private BattleReport battlereport;
    private ValueEventListener battleReportValueListener;
    private DatabaseReference firebaseRef;


    public FragmentBattleReportOverview() {
    }

    public static FragmentBattleReportOverview newInstance(String batteReportId) {
        FragmentBattleReportOverview fragment = new FragmentBattleReportOverview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batteReportId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            battleReportId = getArguments().getString(ARG_PARAM1);
        }

        firebaseRef = FirebaseDatabase.getInstance().getReference();

        battleReportValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                battlereport = dataSnapshot.getValue(BattleReport.class);


            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        firebaseRef.child("battlereports").child(battleReportId).addListenerForSingleValueEvent(battleReportValueListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle_report_overview, container, false);


        return view;
    }

}
