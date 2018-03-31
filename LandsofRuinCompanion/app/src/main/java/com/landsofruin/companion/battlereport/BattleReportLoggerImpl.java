package com.landsofruin.companion.battlereport;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.companion.state.AttackLogItem;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.battlereport.BattleReport;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;
import com.landsofruin.companion.state.battlereport.BattleReportPlayer;
import com.landsofruin.companion.state.proxyobjecthelper.BattleReportLoggerInterface;

import java.util.ArrayList;

/**
 * Created by juhani on 19/01/16.
 */
public class BattleReportLoggerImpl implements BattleReportLoggerInterface {

    private DatabaseReference firebaseRef;

    public BattleReportLoggerImpl() {
        firebaseRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public BattleReport createNewBattleReport(GameState gameState) {
        BattleReport report = new BattleReport();


        ArrayList<BattleReportPlayer> players = new ArrayList<>();
        for (PlayerState state : gameState.getPlayers()) {

            BattleReportPlayer player = new BattleReportPlayer();
            player.setId(state.getIdentifier());

            ArrayList<String> characters = new ArrayList<>();
            for (CharacterState characterState : state.getTeam().listAllTypesCharacters()) {
                characters.add(characterState.getIdentifier());
            }

            player.setCharacters(characters);
            players.add(player);
        }

        report.setPlayers(players);


        DatabaseReference ref = firebaseRef.child("battlereports").push();
        report.setId(ref.getKey());

        report.setGameName(gameState.getTitle());
        report.setScenarioId(gameState.getScenario());

        ref.setValue(report);

        return report;
    }

    @Override
    public void addBattleReportToUser(String battleReportId) {

        firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("battleReports").push().setValue(battleReportId);

    }

    @Override
    public void logEvent(BattleReportEvent event, GameState gameState) {
        DatabaseReference ref = firebaseRef.child("battlereports").child(gameState.getBattleReportServerId()).child("events").push();
        event.setId(ref.getKey());
        ref.setValue(event);
    }

    @Override
    public void logAttackEvent(AttackLogItem event, CharacterState character, GameState gameState) {
        DatabaseReference ref = firebaseRef.child("battlereports").child(gameState.getBattleReportServerId()).child("characterActions").child(character.getIdentifier()).push();
        ref.setValue(event);
    }
}
