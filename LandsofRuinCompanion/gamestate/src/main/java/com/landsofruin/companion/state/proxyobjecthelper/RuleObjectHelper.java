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
@ObjectiveCName("RuleObjectHelper")
public interface RuleObjectHelper {

    Wargear getWargearFor(int id);

    WargearConsumable getWargearFor(ThrowableState wargearState);

    Scenario getScenarioFor(int id);

    CharacterType getCharacterTypeFor(String id);

    Action getActionFor(int id);


    Skill getSkillFor(int id);

    PlayerRole getPlayerRoleFor(int id);

    DamageLine getShootingDamageLine(int number);

    DamageLine getCCDamageLine(int number);
}
