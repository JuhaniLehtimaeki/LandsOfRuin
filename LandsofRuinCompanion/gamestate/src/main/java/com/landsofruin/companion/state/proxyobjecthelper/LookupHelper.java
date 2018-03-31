package com.landsofruin.companion.state.proxyobjecthelper;

import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.damage.DamageLine;
import com.landsofruin.companion.state.gameruleobjects.scenario.PlayerRole;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;

/**
 * Created by juhani on 30/06/15.
 */
@ObjectiveCName("LookupHelper")
public class LookupHelper {

    private RuleObjectHelper ruleObjectHelper;

    private static LookupHelper instance = new LookupHelper();

    public static LookupHelper getInstance() {
        return instance;
    }

    private LookupHelper() {
    }

    public void setRuleObjectHelper(RuleObjectHelper ruleObjectHelper) {
        this.ruleObjectHelper = ruleObjectHelper;
    }


    public Wargear getWargearFor(int id) {
        return ruleObjectHelper.getWargearFor(id);
    }


    public WargearConsumable getWargearFor(ThrowableState wargearState) {
        return ruleObjectHelper.getWargearFor(wargearState);
    }


    public Scenario getScenarioFor(int id) {
        return ruleObjectHelper.getScenarioFor(id);
    }


    public CharacterType getCharacterTypeFor(String id) {
        return ruleObjectHelper.getCharacterTypeFor(id);
    }


    public Action getActionFor(int id) {
        return ruleObjectHelper.getActionFor(id);
    }


    public Skill getSkillFor(int id) {
        return ruleObjectHelper.getSkillFor(id);
    }

    public PlayerRole getPlayerRoleFor(int id) {
        return ruleObjectHelper.getPlayerRoleFor(id);
    }

    public DamageLine getShootingDamageLine(int number) {
        return ruleObjectHelper.getShootingDamageLine(number);
    }


    public DamageLine getCCDamageLine(int number) {
        return ruleObjectHelper.getCCDamageLine(number);
    }
}
