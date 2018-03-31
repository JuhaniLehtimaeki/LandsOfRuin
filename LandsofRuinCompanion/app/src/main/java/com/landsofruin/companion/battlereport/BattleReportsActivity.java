package com.landsofruin.companion.battlereport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.landsofruin.companion.state.battlereport.BattleReport;
import com.landsofruin.companion.tribemanagement.UserAccount;
import com.landsofruin.gametracker.R;

public class BattleReportsActivity extends AppCompatActivity {

    private ViewGroup reportsContainer;
    private UserAccount userAccount;
    private ValueEventListener userAccountValueListener;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseRef = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_battle_reports);

        reportsContainer = (ViewGroup) findViewById(R.id.reports_container);


        userAccountValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userAccount = dataSnapshot.getValue(UserAccount.class);

                for (String battleRepId : userAccount.getBattleReports().values()) {
                    firebaseRef.child("battlereports").child(battleRepId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            BattleReport battleReport = dataSnapshot.getValue(BattleReport.class);
                            addBattleReport(battleReport);
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }

        };
        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(userAccountValueListener);
    }


    private void addBattleReport(final BattleReport battleReport) {
        View battleReportView = getLayoutInflater().inflate(R.layout.one_battle_report, reportsContainer, false);

        ((TextView) battleReportView.findViewById(R.id.name)).setText(battleReport.getGameName());

        battleReportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BattleReportTabsActivity.startNewActivity(BattleReportsActivity.this, battleReport.getId());
            }
        });


        reportsContainer.addView(battleReportView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
