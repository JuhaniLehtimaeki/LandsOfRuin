package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by juhani on 31/12/14.
 */
@ObjectiveCName("CharacterBattleLogState")
public class CharacterBattleLogState {

    private ArrayList<AttackLogItem> attackLogItems = new ArrayList<>();
    private HashMap<String, PointState> movementHistory = new HashMap<>();


    public void addAttackLogItem(AttackLogItem item) {
        this.attackLogItems.add(item);
    }

    public List<AttackLogItem> getAttackLogItems() {
        return attackLogItems;
    }

    public void setAttackLogItems(ArrayList<AttackLogItem> attackLogItems) {
        this.attackLogItems = attackLogItems;
    }

    public void addMovementHistoryPoint(int turn, PointState point) {
        this.movementHistory.put("turn_" + turn, point);
    }

    public HashMap<String, PointState> getMovementHistory() {
        return movementHistory;
    }

    public void setMovementHistory(HashMap<String, PointState> movementHistory) {
        this.movementHistory = movementHistory;
    }
}



