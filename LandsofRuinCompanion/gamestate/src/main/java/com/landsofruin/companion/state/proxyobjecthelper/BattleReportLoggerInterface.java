package com.landsofruin.companion.state.proxyobjecthelper;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.AttackLogItem;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.battlereport.BattleReport;
import com.landsofruin.companion.state.battlereport.BattleReportEvent;

/**
 * Created by juhani on 19/01/16.
 */
@ObjectiveCName("BattleReportLoggerInterface")
public interface BattleReportLoggerInterface {

    BattleReport createNewBattleReport(GameState gameState);

    void addBattleReportToUser(String battleReportId);

    void logEvent(BattleReportEvent event, GameState gameState);

    void logAttackEvent(AttackLogItem event, CharacterState character, GameState gameState);



}
