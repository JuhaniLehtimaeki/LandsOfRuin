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
@ObjectiveCName("BattleReportLogger")
public class BattleReportLogger {

    private BattleReportLoggerInterface logger;

    public void setLogger(BattleReportLoggerInterface logger) {
        this.logger = logger;
    }

    private static BattleReportLogger instance = new BattleReportLogger();

    private BattleReportLogger() {
    }

    public static BattleReportLogger getInstance() {
        return instance;
    }


    public BattleReport createNewBattleReport(GameState gameState) {
        return logger.createNewBattleReport(gameState);
    }


    public void logEvent(BattleReportEvent event, GameState gameState) {
        logger.logEvent(event, gameState);
    }

    public void addBattleReportToUser(String battleReportId) {
        logger.addBattleReportToUser(battleReportId);
    }


    public void logAttackEvent(AttackLogItem event, CharacterState character, GameState gameState) {
        logger.logAttackEvent(event, character, gameState);
    }

}
