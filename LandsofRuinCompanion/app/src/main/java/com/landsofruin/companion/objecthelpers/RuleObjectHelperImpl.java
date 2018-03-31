package com.landsofruin.companion.objecthelpers;

import com.landsofruin.companion.state.ThrowableState;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.damage.DamageLine;
import com.landsofruin.companion.state.gameruleobjects.scenario.PlayerRole;
import com.landsofruin.companion.state.gameruleobjects.scenario.Scenario;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.proxyobjecthelper.RuleObjectHelper;
import com.landsofruin.gametracker.actions.ActionManager;
import com.landsofruin.gametracker.charactertypes.CharacterTypeManager;
import com.landsofruin.gametracker.damage.data.DamageDataManager;
import com.landsofruin.gametracker.scenarios.ScenariosManager;
import com.landsofruin.gametracker.skills.SkillsManager;
import com.landsofruin.gametracker.wargear.WargearManager;

/**
 * Created by juhani on 02/07/15.
 */
public class RuleObjectHelperImpl implements RuleObjectHelper {

    private WargearManager wargearManager = WargearManager.getInstance();
    private ScenariosManager scenariosManager = ScenariosManager.getInstance();
    private CharacterTypeManager characterTypeManager = CharacterTypeManager.getInstance();
    private ActionManager actionManager = ActionManager.getInstance();
    private SkillsManager skillsManager = SkillsManager.getInstance();
    private DamageDataManager damageDataManager = DamageDataManager.getInstance();

    @Override
    public Wargear getWargearFor(int id) {
        return wargearManager.getWargearById(id);
    }


    @Override
    public WargearConsumable getWargearFor(ThrowableState wargearState) {
        return (WargearConsumable) wargearManager.getWargearById(wargearState.getWargearId());
    }

    @Override
    public Scenario getScenarioFor(int id) {
        return scenariosManager.getScenarioByID(id);

    }

    @Override
    public PlayerRole getPlayerRoleFor(int id) {
        return scenariosManager.getPlayerRoleByID(id);
    }


    @Override
    public CharacterType getCharacterTypeFor(String id) {
        return characterTypeManager.getCharacterTypeByID(id);
    }


    @Override
    public Action getActionFor(int id) {
        return actionManager.getActionForId(id);
    }


    @Override
    public Skill getSkillFor(int id) {
        return skillsManager.getSkillByID(id);
    }


    @Override
    public DamageLine getShootingDamageLine(int number) {
        return damageDataManager.getShootingDamageLine(number);
    }

    @Override
    public DamageLine getCCDamageLine(int number) {
        return damageDataManager.getCCDamageLine(number);
    }
}
