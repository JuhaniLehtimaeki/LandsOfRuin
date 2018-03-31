package com.landsofruin.companion.state;


import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.dice.DiceUtils;
import com.landsofruin.companion.state.gameruleobjects.action.Action;
import com.landsofruin.companion.state.gameruleobjects.action.ActionContainerForAssignActions;
import com.landsofruin.companion.state.gameruleobjects.characterdata.CharacterStatModifier;
import com.landsofruin.companion.state.gameruleobjects.characterdata.UnresolvedHit;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearAccessory;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearConsumable;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearDefensive;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@ObjectiveCName("CharacterState")
public abstract class CharacterState {


    private String identifier;
    private String bluePrintIdentifier;
    private String name;
    private PointState point;
    private String tribeId;
    private List<String> regionIdentifiers;
    private int unusedActionPoints = 0;
    private int actionPointsAssignedForMovement;
    private String characterType;
    private int cumulativeNoise = 0;
    private int currentNoise = 0;
    private List<UnresolvedHit> unresolvedHits = new ArrayList<>();

    private List<Integer> currentActions = new ArrayList<>();
    private List<Integer> currentPerformedActions = new ArrayList<>();
    private List<CharacterStatModifier> modifiers = new ArrayList<>();
    private List<Integer> skills = new ArrayList<>();
    private List<Integer> wargear = new ArrayList<>();
    private List<CharacterEffect> characterEffects = new ArrayList<>();
    private List<String> effectChangesLastTurn = new ArrayList<>();
    private List<Integer> zombieTurnHitsWargearIds = new ArrayList<>();
    private HashMap<String, Integer> ammo = new HashMap<>();
    private int attackingZombies = 0;
    private int attackingZombiesDetected = 0;
    private int currentSuppressionRawValue = 0;
    private List<String> detectedByPlayers = new ArrayList<>();
    private boolean movedOnMap = false;
    private String profilePictureUri;
    private String cardPictureUri;
    private String roleIconURL;
    private HashMap<String, Integer> countedActionsThisGame = new HashMap<>();
    private CharacterBattleLogState battleLogState = new CharacterBattleLogState();


    public CharacterState(String identifier) {
        this.identifier = identifier;
        this.actionPointsAssignedForMovement = 20;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * CAREFUL with this method. Calling this can break things unless you know exactly why you need to.
     *
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addActionToCounted(int actionId) {
        if (countedActionsThisGame.get(actionId) == null) {
            countedActionsThisGame.put("" + actionId, 1);
        } else {
            countedActionsThisGame.put("" + actionId, countedActionsThisGame.get(actionId) + 1);
        }
    }

    public void addZombieTurnHitWargearId(int wargearId) {
        this.zombieTurnHitsWargearIds.add(wargearId);
    }

    public void removeZombieTurnHitWargearId(int wargearId) {
        this.zombieTurnHitsWargearIds.remove(Integer.valueOf(wargearId));
    }

    public List<Integer> getZombieTurnHitsWargearIds() {
        return this.zombieTurnHitsWargearIds;
    }

    public List<CharacterStatModifier> getModifiers() {
        return modifiers;
    }

    public void clearModifiers() {
        modifiers.clear();
    }

    public void resetZombieTurnHitsWargearIds() {
        this.zombieTurnHitsWargearIds.clear();
    }

    @Exclude
    public int getZombieHitCountForWargear(int wargearId) {
        int ret = 0;
        for (Integer wgId : this.zombieTurnHitsWargearIds) {
            if (wargearId == wgId.intValue()) {
                ++ret;
            }
        }

        return ret;
    }

    public void resetAttackingZombies() {
        attackingZombies = 0;
        attackingZombiesDetected = 0;
    }

    public int getAttackingZombies() {
        return attackingZombies;
    }

    public void setAttackingZombies(int attackingZombies) {
        this.attackingZombies = attackingZombies;
    }

    public int getAttackingZombiesDetected() {
        return attackingZombiesDetected;
    }

    public void setAttackingZombiesDetected(int attackingZombiesDetected) {
        this.attackingZombiesDetected = attackingZombiesDetected;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public void addAction(int action) {
        this.currentActions.add(action);
    }

    public void clearActions() {
        this.currentActions.clear();
        this.currentPerformedActions.clear();
    }

    public List<Integer> getCurrentActions() {
        return currentActions;
    }

    @Exclude
    public List<Integer> getActionsPerformed() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (Integer action : currentActions) {

            if (!hasActionPerformed(action)) {
                return ret;
            } else {
                ret.add(action);
            }

        }


        return ret;
    }

    @Exclude
    public boolean hasActionsToPerform() {
        for (Integer action : currentActions) {
            if (!hasActionPerformed(action)) {
                return true;
            }
        }

        return false;
    }

    public int getActionPointsAssignedForMovement() {
        return actionPointsAssignedForMovement;
    }

    public void setActionPointsAssignedForMovement(int actionPointsAssignedForMovement) {
        this.actionPointsAssignedForMovement = actionPointsAssignedForMovement;
    }

    @Exclude
    public boolean hasActionAssigend(Action action) {
        for (Integer actionState : currentActions) {
            if (actionState == action.getId()) {
                return true;
            }
        }

        return false;
    }

    public void addActionToPerformed(Integer action) {
        this.currentPerformedActions.add(action);
    }

    @Exclude
    public boolean hasActionPerformed(int actionID) {
        for (Integer actionState : currentPerformedActions) {
            if (actionState == actionID) {
                return true;
            }
        }

        return false;
    }

    @Exclude
    public int getRemainingActionPoints(GameState gamestate) {
        int aps = 0;
        for (Integer action : this.currentActions) {
            aps = aps + LookupHelper.getInstance().getActionFor(action).getActionPoints();
        }

        int availableAPs = LookupHelper.getInstance().getCharacterTypeFor(this.characterType).getBaseAPs();

        for (CharacterEffect effect : characterEffects) {
            if (effect.isActive(gamestate.getPhase().getGameTurn())) {
                availableAPs = availableAPs + effect.getAPModifier();

                availableAPs = availableAPs
                        + (int) (availableAPs * effect
                        .getAPModifierPercentage());
            }
        }

        return availableAPs - aps;
    }

    @Exclude
    public int getMovementAllowanceForNextTurn(GameState gamestate) {
        CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType());

        int movement = (int) (characterType.getMovementPerAP() * getRemainingActionPoints(gamestate));

        for (CharacterEffect effect : characterEffects) {
            if (effect.isActive(gamestate.getPhase().getGameTurn() + 1)) {
                movement = movement + effect.getMovementModifier();

                movement = movement
                        + (int) (movement * effect
                        .getMovementModifierPercentage());
            }
        }

        movement = (int) (movement * getMovementModifierFromWeight());

        movement = (int) (movement * (1 - getCurrentSuppression() / 100f));

        return movement < 0 ? 0 : movement;

    }

    @Exclude
    public int getMovementAllowance(GameState gamestate) {
        CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType());

        int movement = (int) (characterType.getMovementPerAP() * getActionPointsAssignedForMovement());

        for (CharacterEffect effect : characterEffects) {
            if (effect.isActive(gamestate.getPhase().getGameTurn())) {
                movement = movement + effect.getMovementModifier();

                movement = movement
                        + (int) (movement * effect
                        .getMovementModifierPercentage());
            }
        }

        movement = (int) (movement * getMovementModifierFromWeight());

        movement = (int) (movement * (1 - getCurrentSuppression() / 100f));

        return movement < 0 ? 0 : movement;
    }

    @Exclude
    public float getMovementModifierFromWeight() {
        float freeAmmount = getMaxLoad() * 0.1f;

        float extraItems = getTotalItemWeight() - freeAmmount;

        if (extraItems > 0) {
            float modifier = 1f - (extraItems / getMaxLoad());
            if (modifier < 0) {
                modifier = 0;
            }

            return modifier;
        } else {
            return 1;
        }
    }

    @Exclude
    public int getMaxLoad() {
        int maxLoad = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType()).getBaseMaxLoad();
        // TODO: when strength skills are added add them here
        return maxLoad;
    }

    public HashMap<String, Integer> getAmmo() {
        return ammo;
    }

    public void setAmmo(HashMap<String, Integer> ammo) {
        this.ammo = ammo;
    }

    @Exclude
    public float getTotalItemWeight() {
        float totalWeight = 0;
        ArrayList<Integer> handledWeapons = new ArrayList<>();

        for (Integer wargear : this.wargear) {
            totalWeight += LookupHelper.getInstance().getWargearFor(wargear).getWeight();

            if (LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearOffensive) {
                WargearOffensive wgOffensive = (WargearOffensive) LookupHelper.getInstance().getWargearFor(wargear);
                if (handledWeapons.contains(wargear)) {
                    continue;
                }
                handledWeapons.add(wargear);

                if (wgOffensive.getBulletsPerAction() == 0) {
                    continue;
                }

                int clips = getAmmoFor(wgOffensive.getWeaponId())
                        / wgOffensive.getClipSize();
                totalWeight += clips * wgOffensive.getClipWeight();

                // getAmmoFor(id)
            }

        }
        return totalWeight;
    }


    public void removeAllActionsOfId(int actionId) {
        ArrayList<Integer> remove = new ArrayList<>();

        for (Integer action : this.currentActions) {
            if (action == actionId) {
                remove.add(action);
            }
        }

        this.currentActions.removeAll(remove);
    }


    public void addUnresolvedHit(UnresolvedHit hit) {
        this.unresolvedHits.add(hit);
    }

    public List<UnresolvedHit> getUnresolvedHits() {
        return unresolvedHits;
    }

    public void clearUnresolvedHits() {
        this.unresolvedHits.clear();
    }

    @Exclude
    public int getCurrentOffensiveEffectModifier(GameState gamestate) {
        int modifier = 0;

        for (CharacterEffect effect : characterEffects) {
            if (effect.isActive(gamestate.getPhase().getGameTurn())) {
                modifier = modifier + effect.getOffensiveModifier();
            }
        }

        return modifier;
    }

    @Exclude
    public int getCurrentDefensiveModifierShooting(GameState gamestate, boolean isLightCover, boolean isHeavyCover) {
        int ret = getCurrentDefensiveModifierShooting(gamestate);
        if (isHeavyCover) {
            ret += GameConstants.HEAVY_COVER_MODIFIER;
        } else if (isLightCover) {
            ret += GameConstants.LIGHT_COVER_MODIFIER;
        }

        return ret;
    }

    @Exclude
    public int getCurrentDefensiveModifierShooting(GameState gamestate) {
        int target = getCurrentTargetDefensive(gamestate);

        for (Integer currentWargear : wargear) {
            if (LookupHelper.getInstance().getWargearFor(currentWargear) instanceof WargearDefensive) {

                target += ((WargearDefensive) LookupHelper.getInstance().getWargearFor(currentWargear)).getDefensiveModifierShooting();
            }
        }

        for (CharacterEffect effect : getCharacterEffects()) {
            target += effect.getDefensiveModifierShooting();
        }


        return target;
    }

    @Exclude
    public int getCurrentDefensiveModifierCC(GameState gamestate, boolean isLightCover, boolean isHeavyCover) {
        int ret = getCurrentDefensiveModifierCC(gamestate);
        if (isHeavyCover) {
            ret += GameConstants.HEAVY_COVER_MODIFIER;
        } else if (isLightCover) {
            ret += GameConstants.LIGHT_COVER_MODIFIER;
        }

        return ret;
    }

    @Exclude
    private int getCurrentDefensiveModifierCC(GameState gamestate) {
        int target = getCurrentTargetDefensive(gamestate);

        for (Integer currentWargear : wargear) {
            if (LookupHelper.getInstance().getWargearFor(currentWargear) instanceof WargearDefensive) {
                target += (((WargearDefensive) LookupHelper.getInstance().getWargearFor(currentWargear)).getDefensiveModifierClosecombat());
            }
        }

        for (CharacterEffect effect : getCharacterEffects()) {
            target -= effect.getDefensiveModifierCC();
        }


        return target;
    }

    @Exclude
    public int getCurrentDefensiveModifierZombies(GameState gamestate) {
        int target = getCurrentTargetDefensive(gamestate);

        for (Integer wargear2 : wargear) {
            if (LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearDefensive) {
                target = target
                        + (((WargearDefensive) LookupHelper.getInstance().getWargearFor(wargear2))
                        .getDefensiveModifierZombies());
            }
        }

        for (CharacterEffect effect : getCharacterEffects()) {
            target += effect.getDefensiveModifierZombie();
        }

        return target;
    }

    @Exclude
    public int getCurrentDefensiveModifierExplosives(GameState gamestate) {
        int target = 0;

        for (Integer wargear2 : wargear) {
            if (LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearDefensive) {
                target = target
                        + (((WargearDefensive) LookupHelper.getInstance().getWargearFor(wargear2))
                        .getDefensiveModifierExplosives());
            }
        }

        return target;
    }

    @Exclude
    public int getCurrentOffensiveModifier(GameState gamestate) {
        int ret = 0;

        for (CharacterStatModifier modif : modifiers) {
            if (modif.getLastEffectiveTurn() >= gamestate.getPhase()
                    .getGameTurn()) {

                int modifier = modif.getOffenciveModifier();

                if (modifier < 0) {
                    for (CharacterEffect effect : getCharacterEffects()) {
                        modifier = (int) (modifier - (modifier * effect.getOffensiveModifierResistance()));
                    }
                }

                ret = ret + modifier;
            }
        }


        return ret;
    }

    @Exclude
    public int getCurrentDefensiveModifier(GameState gamestate) {
        int ret = 0;

        for (CharacterStatModifier modif : modifiers) {
            if (modif.getLastEffectiveTurn() >= gamestate.getPhase()
                    .getGameTurn()) {
                ret = ret + modif.getDefensiveModifier();
            }
        }

        return ret;
    }


    public void addModifier(CharacterStatModifier modifier) {
        this.modifiers.add(modifier);
    }

    public void addAllModifiers(
            ArrayList<CharacterStatModifier> createCharacterModifiersFrom) {
        this.modifiers.addAll(createCharacterModifiersFrom);
    }

    public void addSkill(Integer skill) {
        this.skills.add(skill);
    }

    public List<Integer> getSkills() {
        return skills;
    }

    @Exclude
    public int getSkillModifiersDefensive() {
        int ret = 0;

        for (Integer skill : getSkills()) {
            ret = ret + LookupHelper.getInstance().getSkillFor(skill).getDefensiveModifier();
        }

        return ret;
    }


    public List<Integer> getWargear() {
        return wargear;
    }

    public void addWargear(int wargear) {
        this.wargear.add(wargear);
    }

    public void removeConsumableWargear(WargearConsumable wargear, TeamState team) {

        if (wargear.getConsumesSquadMember() != null) {
            for (CharacterStateSquad squad : team.getSquads()) {
                if (wargear.getConsumesSquadMember().equals(squad.getBluePrintIdentifier())) {
                    squad.killASquadMember();
                    return;
                }
            }

            Log.e("squads consumed", "did not find a squad to consume. something is wrong!");
            return;
        }


        Integer remove = null;

        for (Integer wg : this.wargear) {
            if (wg == wargear.getId()) {
                remove = wg;
            }
        }
        if (remove != null) {
            this.wargear.remove(remove);
        }
    }

    @Exclude
    public int getCurrentTargetDefensive(GameState gamestate) {
        return (getCurrentDefensiveModifier(gamestate))
                + (getSkillModifiersDefensive())
                + ((getUnusedActionPoints() / 5));
    }

    @Exclude
    public List<ActionContainerForAssignActions> getAvailableActionsForAssignActions(GameState gameState) {
        ArrayList<ActionContainerForAssignActions> ret = new ArrayList<>();
        for (Integer skill : getSkills()) {


            if (!isSkillEnabledWithWargear(LookupHelper.getInstance().getSkillFor(skill), gameState)) {
                continue;
            }

            List<Integer> enablesActions = LookupHelper.getInstance().getSkillFor(skill).getEnablesActions();

            for (int i : enablesActions) {
                Action action = LookupHelper.getInstance().getActionFor(i);

                if (action.getMaxTimesPerBattle() > 0) {
                    if (countedActionsThisGame.get("" + action.getId()) != null && countedActionsThisGame.get("" + action.getId()) >= action.getMaxTimesPerBattle()) {
                        ret.add(new ActionContainerForAssignActions(action,
                                false, "Can be used only " + action.getMaxTimesPerBattle() + " time(s) per battle."));
                        continue;
                    }
                }

                if (action.getRequiresSquad() != null) {
                    boolean squadFound = false;
                    for (CharacterStateSquad squad : gameState.findPlayerByCharacterIdentifier(this.getIdentifier()).getTeam().getSquads()) {
                        if (squad.getBluePrintIdentifier().equals(action.getRequiresSquad())) {

                            if (squad.getSquadSize() > 0) {
                                squadFound = true;
                                break;
                            }
                        }
                    }

                    if (!squadFound) {
                        ret.add(new ActionContainerForAssignActions(action,
                                false, "Required cohort either dead or not with the hero."));
                        continue;
                    }
                }


                if (action != null && !ret.contains(action)) {

                    StringBuffer reason = new StringBuffer();
                    if (isActionEnabled(action,
                            this, gameState, reason)) {
                        ret.add(new ActionContainerForAssignActions(action,
                                true, ""));
                    } else {
                        if (reason.length() > 0) {
                            ret.add(new ActionContainerForAssignActions(action,
                                    false, reason.toString()));
                        }
                    }
                }
            }

        }


        Collections.sort(ret,
                new Comparator<ActionContainerForAssignActions>() {

                    @Override
                    public int compare(ActionContainerForAssignActions lhs,
                                       ActionContainerForAssignActions rhs) {


                        return (lhs.getAction().getOrder()) - (rhs.getAction().getOrder());
                    }
                });

        return ret;
    }

    @Exclude
    private boolean isSkillEnabledWithWargear(Skill skill, GameState gameState) {
        for (Integer wargear : getWargear()) {

            if (isSkillRequirementsMet(skill, LookupHelper.getInstance().getWargearFor(wargear), gameState)) {
                return true;
            }

        }

        return false;
    }

    @Exclude
    public boolean hasSkill(int id) {
        for (int skill : skills) {
            if (skill == id) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    public ArrayList<String> getDefensiveModifiersExplanationsShooting(GameState gamestate) {
        ArrayList<String> ret = new ArrayList<>();
        ret.addAll(getDefensiveModifiersExplanations(gamestate));


        for (Integer wargear2 : wargear) {

            if (LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearDefensive) {

                ret.add(LookupHelper.getInstance().getWargearFor(wargear2).getName() + " "
                        + ((WargearDefensive) LookupHelper.getInstance().getWargearFor(wargear2))
                        .getDefensiveModifierShooting());
            }
        }


        for (CharacterEffect effect : getCharacterEffects()) {
            ret.add(effect.getName() + " "
                    + effect.getDefensiveModifierShooting());
        }

        return ret;
    }

    @Exclude
    public ArrayList<String> getDefensiveModifiersExplanationsCC(GameState gamestate) {
        ArrayList<String> ret = new ArrayList<>();
        ret.addAll(getDefensiveModifiersExplanations(gamestate));


        for (Integer wargear2 : wargear) {

            if (LookupHelper.getInstance().getWargearFor(wargear2) instanceof WargearDefensive) {

                ret.add(LookupHelper.getInstance().getWargearFor(wargear2).getName() + " "
                        + ((WargearDefensive) LookupHelper.getInstance().getWargearFor(wargear2))
                        .getDefensiveModifierClosecombat());
            }
        }


        for (CharacterEffect effect : getCharacterEffects()) {
            ret.add(effect.getName() + " "
                    + effect.getDefensiveModifierCC());
        }


        return ret;
    }


    @Exclude
    private ArrayList<String> getDefensiveModifiersExplanations(GameState gamestate) {
        ArrayList<String> ret = new ArrayList<>();


        if (getCurrentDefensiveModifier(gamestate) != 0) {
            ret.add("Modifiers from wounds "
                    + getCurrentDefensiveModifier(gamestate));
        }

        if (getSkillModifiersDefensive() != 0) {
            ret.add("Modifiers from skills "
                    + getSkillModifiersDefensive());
        }

        if ((getUnusedActionPoints() / 5) != 0) {
            ret.add("Modifiers from moving "
                    + (getUnusedActionPoints() / 5));
        }

        return ret;
    }

    @Exclude
    public ArrayList<String> getTargetNumberExplanations(
            WargearOffensive wargear, GameState gamestate) {

        ArrayList<String> ret = new ArrayList<>();


        if (wargear.isTypeCC()) {
            ret.add("Base "
                    + LookupHelper.getInstance().getCharacterTypeFor(getCharacterType())
                    .getBaseTargetOffensiveCloseCombat() + "+");
        } else {
            ret.add("Base "
                    + LookupHelper.getInstance().getCharacterTypeFor(getCharacterType())
                    .getBaseTargetOffensive() + "+");
        }


        if (isWeaponDeployed()) {
            int deployedModifier = wargear.getModifierDeployed();

            if (deployedModifier != 0) {
                ret.add("Deployed weapon +" + deployedModifier);
            }
        }

        if (getCurrentOffensiveModifier(gamestate) != 0) {
            ret.add("Modifiers from wounds "
                    + getCurrentOffensiveModifier(gamestate));
        }

        if (getCurrentOffensiveEffectModifier(gamestate) != 0) {
            ret.add("Modifiers from effects "
                    + getCurrentOffensiveEffectModifier(gamestate));
        }


        for (Integer skill : getSkills()) {
            if (LookupHelper.getInstance().getSkillFor(skill).getOffensiveModifier() != 0
                    && isSkillRequirementsMet(LookupHelper.getInstance().getSkillFor(skill), wargear, gamestate)) {

                ret.add("Skill modifier: " + LookupHelper.getInstance().getSkillFor(skill).getName() + " +"
                        + LookupHelper.getInstance().getSkillFor(skill).getOffensiveModifier());
            }
        }


        List<WargearAccessory> accessories = getAccessoriesFor(wargear.getWeaponId(),
                this.getWargear());
        for (WargearAccessory wargearAccessory : accessories) {
            if (wargearAccessory.getRequiredEffect() != -1) {

                List<CharacterEffect> effect = getCharacterEffects();
                for (CharacterEffect characterEffect : effect) {
                    if (characterEffect.getId() == wargearAccessory
                            .getRequiredEffect()) {

                        if (wargearAccessory.getOffensiveModifier() != 0) {
                            ret.add("Accessory modifier " + wargearAccessory.getName() + " "
                                    + wargearAccessory.getOffensiveModifier());
                        }
                        break;
                    }
                }

            } else {
                ret.add("Accessory modifier " + wargearAccessory.getName() + " "
                        + wargearAccessory.getOffensiveModifier());
            }

        }


        if (getCurrentSuppression() > 0) {
            ret.add("Suppression " + -(getCurrentSuppression() / 10));

        }

        return ret;
    }

    @Exclude
    public boolean isWargearSkillRequirementsMet(Wargear wargear) {
        List<Integer> requiredSkills = wargear.getRequiresSkills();
        boolean hasAllSkills = true;

        for (int skillId : requiredSkills) {

            if (!this.hasSkill(skillId)) {
                hasAllSkills = false;
                break;
            }

        }

        return hasAllSkills;

    }

    @Exclude
    public int getTargetNumber(WargearOffensive wargear, GameState gamestate, boolean isShortRange, boolean isMidRange, boolean isLongRange, boolean isAiming, int defence) {

        int targetNumber;
        if (isShortRange) {
            targetNumber = getTargetNumberShort(wargear, gamestate);
        } else if (isMidRange) {
            targetNumber = getTargetNumberMid(wargear, gamestate);
        } else {
            targetNumber = getTargetNumberLong(wargear, gamestate);
        }

        if (isAiming) {
            targetNumber -= GameConstants.AIM_MODIFIER;
        }

        if (isWeaponDeployed()) {
            targetNumber -= wargear.getModifierDeployed();
        }


        targetNumber = targetNumber + defence;


        if (targetNumber <= 1) {
            targetNumber = 2;
        }


        if (targetNumber >= 20) {
            targetNumber = 20;
        }
        return targetNumber;
    }


    @Exclude
    public int getTargetNumberShort(WargearOffensive wargear, GameState gamestate) {
        return getTargetNumber(wargear, wargear.getModifierShort(), gamestate);
    }

    @Exclude
    public int getTargetNumberMid(WargearOffensive wargear, GameState gamestate) {
        return getTargetNumber(wargear, wargear.getModifierMid(), gamestate);
    }

    @Exclude
    public int getTargetNumberLong(WargearOffensive wargear, GameState gamestate) {
        return getTargetNumber(wargear, wargear.getModifierLong(), gamestate);
    }

    @Exclude
    public int getSkillModifiersForWeapon(Wargear wargear, GameState gameState) {
        int ret = 0;
        for (Integer skill : getSkills()) {
            if (LookupHelper.getInstance().getSkillFor(skill).getOffensiveModifier() != 0
                    && isSkillRequirementsMet(LookupHelper.getInstance().getSkillFor(skill), wargear, gameState)) {
                ret = ret + LookupHelper.getInstance().getSkillFor(skill).getOffensiveModifier();
            }
        }

        return ret;

    }

    @Exclude
    private int getTargetNumber(Wargear wargear, int rangeModifier, GameState gamestate) {


        int base = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType())
                .getBaseTargetOffensive();

        if (wargear.isTypeCC()) {
            base = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType())
                    .getBaseTargetOffensiveCloseCombat();
        }


        int ret = base
                + (-getCurrentOffensiveModifier(gamestate))
                + (-getCurrentOffensiveEffectModifier(gamestate));

        ret = ret - rangeModifier;


        ret = ret - getSkillModifiersForWeapon(wargear, gamestate);


        ArrayList<WargearAccessory> accessories = getAccessoriesFor(wargear.getWeaponId(),
                this.getWargear());
        for (WargearAccessory wargearAccessory : accessories) {
            if (wargearAccessory.getRequiredEffect() != -1) {

                List<CharacterEffect> effect = getCharacterEffects();
                for (CharacterEffect characterEffect : effect) {
                    if (characterEffect.getId() == wargearAccessory
                            .getRequiredEffect()) {
                        ret = ret - wargearAccessory.getOffensiveModifier();
                        break;
                    }
                }

            } else {

                ret = ret - wargearAccessory.getOffensiveModifier();
            }

        }

        ret = ret + getCurrentSuppression() / 10;

        if (ret > 20) {
            ret = 20;
        }

        return ret;
    }

    // TODO: Note, this will not work if a skill requires more than one thing!!
    @Exclude
    private boolean isSkillRequirementsMet(Skill skill, Wargear wargear, GameState gameState) {
        if (wargear == null) {
            return false;
        }

        Log.d("character", "checking skill requirements for " + skill.getName());

        List<String> requiredCategory = skill.getRequiresEquipmentCategory();
        Log.d("character", "checking requirement category " + requiredCategory);
        if (requiredCategory != null && !requiredCategory.isEmpty()) {
            for (String string : requiredCategory) {
                if (!wargear.getCategory().equals(string)) {
                    Log.d("character",
                            "skill " + skill.getName() + " rejected. category "
                                    + string
                                    + " doesn't match to wargear category "
                                    + wargear.getCategory());
                    return false;
                }
            }
        }

        List<String> requiredType = skill.getRequiresEquipmentType();
        Log.d("character", "checking requirement type " + requiredType);
        if (requiredType != null && !requiredType.isEmpty()) {
            for (String string : requiredType) {
                if (!wargear.getType().equals(string)) {
                    Log.d("character",
                            "skill " + skill.getName() + " rejected. type "
                                    + string
                                    + " doesn't match to wargear type "
                                    + wargear.getType());
                    return false;
                }
            }
        }

        List<Integer> requiredId = skill.getRequiresEquipmentId();
        Log.d("character", "checking requirement id " + requiredId);
        if (requiredId != null && !requiredId.isEmpty()) {
            for (int integer : requiredId) {
                if (wargear.getWeaponId() != integer) {

                    Log.d("character",
                            "skill " + skill.getName() + " rejected. ID "
                                    + requiredId
                                    + " doesn't match to wargear ID "
                                    + wargear.getWeaponId());
                    return false;
                } else {
                    if (wargear instanceof WargearConsumable && ((WargearConsumable) wargear).getRequiresSquad() != null) {

                        String requiredSquad = ((WargearConsumable) wargear).getRequiresSquad();

                        boolean squadFound = false;

                        TeamState team = gameState.findTeamByCharacterIdentifier(this.identifier);
                        for (CharacterStateSquad squad : team.getSquads()) {

                            if (requiredSquad.equals(squad.getBluePrintIdentifier())) {
                                squadFound = true;
                                if (squad.getSquadSize() <= 0) {


                                    Log.d("character",
                                            "skill " + skill.getName() + " rejected. ID "
                                                    + requiredId
                                                    + " doesn't have squad members left in" + squad.getName());

                                    return false;
                                }

                            }
                        }

                        if (!squadFound) {
                            Log.d("character",
                                    "skill " + skill.getName() + " rejected. ID "
                                            + requiredId
                                            + " doesn't have required squad");

                            return false;
                        }


                    }
                }
            }
        }

        Log.d("character", "skill " + skill.getName() + " with modifier "
                + skill.getOffensiveModifier() + " accepted");
        return true;
    }

    public List<CharacterEffect> getCharacterEffects() {
        return characterEffects;
    }

    public void addCharacterEffect(CharacterEffect effect) {
        this.characterEffects.add(effect);
    }

    public void removeCharacterEffect(CharacterEffect effect) {
        this.characterEffects.remove(effect);
    }

    public void removeCharacterEffect(int characterEffectId) {
        ArrayList<CharacterEffect> characterEffectsToRemove = new ArrayList<CharacterEffect>();
        for (CharacterEffect characterEffect : getCharacterEffects()) {
            if (characterEffect.getId() == characterEffectId) {
                characterEffectsToRemove.add(characterEffect);
            }
        }

        for (CharacterEffect characterEffect : characterEffectsToRemove) {
            this.removeCharacterEffect(characterEffect);
        }
    }

    public List<String> getEffectChangesLastTurn() {
        return effectChangesLastTurn;
    }

    public void addEffectChangeLastTurn(String effectChange) {
        effectChangesLastTurn.add(effectChange);
    }

    public void clearEffectChangesLastTurn() {
        effectChangesLastTurn.clear();
    }

    @Exclude
    public int getAmmoFor(int id) {
        try {
            return this.ammo.get("" + id);
        } catch (Exception e) {
            return 0;
        }
    }

    @Exclude
    public void setAllAmmo(HashMap<String, Integer> ammo) {
        this.ammo = ammo;
    }


    public void reduceAmmoBy(int id, int ammo) {
        int newAmount = getAmmoFor(id) - ammo;
        if (newAmount < 0) {
            newAmount = 0;
        }
        setAmmo(id, newAmount);
    }

    @Exclude
    private void setAmmo(int id, int ammo) {

        if (ammo < 0) {
            ammo = 0;
        }
        this.ammo.put("" + id, ammo);
    }

    public void addNoise(int noise) {
        cumulativeNoise += noise;
    }

    public int getCumulativeNoise() {
        return cumulativeNoise;
    }

    public void resetUnusedActionPoints() {
        this.unusedActionPoints = LookupHelper.getInstance().getCharacterTypeFor(this.characterType)
                .getBaseAPs() - (unresolvedHits.size() * 5);

        if (this.unusedActionPoints < 0) {
            this.unusedActionPoints = 0;
        }

    }

    public void reduceUnusedActionPoints(int aps) {
        this.unusedActionPoints = this.unusedActionPoints - aps;

        if (this.unusedActionPoints < 0) {
            this.unusedActionPoints = 0;
        }
    }

    public int getUnusedActionPoints() {
        return unusedActionPoints;
    }

    public int getCurrentNoise() {
        return currentNoise;
    }

    public void setCurrentNoise(int currentNoise) {
        this.currentNoise = currentNoise;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CharacterState) {
            return ((CharacterState) o).getIdentifier().equals(this.identifier);
        } else {
            return false;
        }
    }

    public PointState getCenterPoint() {
        return point;
    }

    public void setCenterPoint(PointState point) {
        this.point = point;
    }

    /**
     * Like equals() but named differently as we only compare identifiers here
     */
    @Exclude
    public boolean isSame(CharacterState character) {
        if (character == null) {
            return false;
        }

        return this.identifier.equals(character.identifier);
    }

    @Exclude
    public boolean isOnMap() {
        return point != null;
    }

    public void updatePosition(PointState point, List<String> regionIdentifiers) {
        this.point = point;

        this.regionIdentifiers = regionIdentifiers;

//        this.boundingBox = calculateBoundingBox();
    }

    public void updatePositionOffTable() {
        this.point = null;
        if (this.regionIdentifiers != null) {
            this.regionIdentifiers.clear();
        }
//        this.boundingBox = null;
    }

    public void setRegions(List<String> regions) {
        this.regionIdentifiers = regions;
    }

    public List<String> getRegions() {
        return regionIdentifiers;
    }

    @Exclude
    public boolean isDown() {
        return isDead() || isUnconsious() || isPinned();
    }

    @Exclude
    public boolean isUnconsious() {
        for (CharacterEffect effect : getCharacterEffects()) {
            if (effect.getId() == CharacterEffect.ID_UNCONSCIOUS) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    public boolean isPinned() {
        for (CharacterEffect effect : getCharacterEffects()) {
            if (effect.getId() == CharacterEffect.ID_PINNED) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    public boolean isDead() {


        if (this instanceof CharacterStateSquad && ((CharacterStateSquad) this).getSquadSize() <= 0) {
            return true;
        }

        for (CharacterEffect effect : getCharacterEffects()) {
            if (effect.getId() == CharacterEffect.ID_DEAD) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    public boolean isWeaponDeployed() {
        for (CharacterEffect effect : getCharacterEffects()) {
            if (effect.getId() == CharacterEffect.ID_WEAPON_DEPLOYED) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    public boolean isBleeding() {
        for (CharacterEffect effect : getCharacterEffects()) {
            if (effect.getId() == CharacterEffect.ID_BLEEDING) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    public int getLeadership() {
        int ld = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType()).getBaseLeadership();

        for (Integer skill : getSkills()) {
            ld += LookupHelper.getInstance().getSkillFor(skill).getLeadershipModifier();
        }

        return ld;
    }

    @Exclude
    public int getDetection() {
        return LookupHelper.getInstance().getCharacterTypeFor(getCharacterType()).getDetection();
    }

    @Exclude
    public boolean hasEffect(int id) {
        for (CharacterEffect effect : getCharacterEffects()) {
            if (effect.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    public boolean isHidden() {
        for (CharacterEffect effect : getCharacterEffects()) {
            if (effect.getId() == CharacterEffect.ID_HIDDEN) {
                return true;
            }
        }
        return false;
    }

    public void addPregameDefaultEffects() {
        for (Integer skill : getSkills()) {
            if (LookupHelper.getInstance().getSkillFor(skill).getAddsDefaultEffectsPregame() != null) {
                for (int effectId : LookupHelper.getInstance().getSkillFor(skill)
                        .getAddsDefaultEffectsPregame()) {
                    if (hasEffect(effectId)) {
                        continue;
                    }
                    addCharacterEffect(CharacterEffectFactory
                            .createCharacterEffect(effectId, 0));
                }
            }
        }
    }

    public void removePregameDefaultEffects() {
        this.characterEffects.clear();
    }

    public void addDetectedByPlayer(String playerId) {
        this.detectedByPlayers.add(playerId);
    }

    public List<String> getDetectedByPlayers() {
        return detectedByPlayers;
    }

    @Exclude
    public boolean isDetectedByPlayer(String playerId) {
        return this.detectedByPlayers.contains(playerId);
    }

    @Exclude
    public int getCamoRating() {
        int ret = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType()).getBaseCamo();

        for (Integer wg : getWargear()) {
            ret = ret + LookupHelper.getInstance().getWargearFor(wg).getCamoModifier();
        }

        // TODO: add from skills
        return ret;
    }

    public boolean tryToDetect(int camoRating) {
        int modifier = camoRating - getDetection();
        int target = 50 + modifier;
        int roll = DiceUtils.rollDie(100);
        return roll >= target;
    }

    @Exclude
    public int getSuppressionDefence() {
        int ret = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType())
                .getBaseSuppressionDefence();

        // add effects from gear and skills
        return ret;
    }

    @Exclude
    public int getCurrentSuppression() {
        if (LookupHelper.getInstance().getCharacterTypeFor(getCharacterType()).getType() == CharacterType.TYPE_SQUAD_SLAVE) {
            return 0;
        }

        int ret = currentSuppressionRawValue - getSuppressionDefence();

        if (ret > 100) {
            ret = 100;
        }


        for (CharacterEffect effect : getCharacterEffects()) {
            ret = (int) (ret - (ret * effect.getSuppressionResistance()));
        }

        return ret;
    }

    public void resetSuppression() {
        this.currentSuppressionRawValue = 0;
    }

    public void addSuppression(int suppression) {
        this.currentSuppressionRawValue += suppression;
    }

    public boolean isMovedOnMapThisTurn() {
        return movedOnMap;
    }

    public void setMovedOnMapThisTurn(boolean movedOnMap) {
        this.movedOnMap = movedOnMap;
    }


    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public String getCardPictureUri() {
        return cardPictureUri;
    }

    public void setCardPictureUri(String cardPictureUri) {
        this.cardPictureUri = cardPictureUri;
    }

    public CharacterBattleLogState getBattleLogState() {
        return battleLogState;
    }

    public void setBattleLogState(CharacterBattleLogState battleLogState) {
        this.battleLogState = battleLogState;
    }

    public int getCurrentGearValue() {
        int ret = 0;


        ArrayList<Integer> handledWG = new ArrayList<>();
        for (Integer wg : this.wargear) {

            int weaponId = LookupHelper.getInstance().getWargearFor(wg).getWeaponId();
            if (handledWG.contains(weaponId)) {
                continue;
            }
            handledWG.add(weaponId);


            ret += LookupHelper.getInstance().getWargearFor(wg).getGearValue();


            if (LookupHelper.getInstance().getWargearFor(wg) instanceof WargearOffensive) {
                WargearOffensive offensiveWg = (WargearOffensive) LookupHelper.getInstance().getWargearFor(wg);

                int clips = getAmmoFor(offensiveWg.getWeaponId()) / (
                        offensiveWg.getClipSize() > 0 ? offensiveWg.getClipSize() : 1);
                ret += clips * offensiveWg.getClipGearValue();
            }
        }

        for (Integer skill : this.skills) {
            ret += LookupHelper.getInstance().getSkillFor(skill).getGearValue();
        }


        return ret;
    }

    @Exclude
    private boolean isActionEnabled(Action action,
                                    CharacterState playerCharacter, GameState gameState,
                                    StringBuffer reasonForDisabled) {

        int teamStatus = gameState.getMe().getTeam().getTeamStatus(gameState);
        int actionType = action.getActionType();

        List<Integer> addEffectSelf = action.getAddsEffectSelf();
        if (isTarget(addEffectSelf)) {
            List<CharacterEffect> effects = playerCharacter
                    .getCharacterEffects();

            for (CharacterEffect characterEffect : effects) {
                if (isTargetForAny(addEffectSelf, characterEffect.getId(), playerCharacter)
                        && !characterEffect.canStack()) {

                    // check if the effect is also removed ie. replaced
                    List<Integer> targetEffectSelf = action.getTargetsEffectSelf();

                    if (isTargetEffectSelfActive(targetEffectSelf,
                            playerCharacter)) {
                        if (!(teamStatus >= actionType)) {
                            reasonForDisabled.append("Team morale too low");
                            return false;
                        }

                        if (playerCharacter.getRemainingActionPoints(gameState) < action
                                .getActionPoints()) {
                            reasonForDisabled.append("Not enough APs");
                            return false;
                        }
                    } else {
                        return false;
                    }

                }
            }

        }

        List<Integer> targetEffectFriendly = action.getTargetsEffectFriendly();
        if (isTarget(targetEffectFriendly)) {

            if (!getAvailableTargetsForFriendlyAction(action, playerCharacter,
                    gameState).isEmpty()) {

            } else {
                reasonForDisabled.append("No available targets");
                return false;
            }

        }


        List<Integer> targetEffectEnemy = action.getTargetsEffectEnemy();
        if (isTarget(targetEffectEnemy)) {

            boolean enemiesFound = false;

            for (int effectId : targetEffectEnemy) {
                if (!getEnemyTargetsForEffect(gameState, effectId).isEmpty()) {
                    enemiesFound = true;
                }
            }


            if (!enemiesFound) {
                // not add description. We don't want the action to show at all.
                return false;
            }

        }


        List<Integer> targetEffectSelf = action.getTargetsEffectSelf();
        if (isTarget(targetEffectSelf)) {

            if (isTargetEffectSelfActive(targetEffectSelf, playerCharacter)) {

                if (!(teamStatus >= actionType)) {
                    reasonForDisabled.append("Team morale too low");
                    return false;
                }

                if (playerCharacter.getRemainingActionPoints(gameState) < action
                        .getActionPoints()) {
                    reasonForDisabled.append("Not enough APs");
                    return false;
                }

            } else {
                return false;
            }

        }

        if (!(teamStatus >= actionType)) {
            reasonForDisabled.append("Team morale too low");
            return false;
        }

        if (playerCharacter.getRemainingActionPoints(gameState) < action
                .getActionPoints()) {
            reasonForDisabled.append("Not enough APs");
            return false;
        }

        for (Integer action_ : playerCharacter.getCurrentActions()) {
            if (LookupHelper.getInstance().getActionFor(action_).getBlocksActions() != null) {
                for (int blocks : LookupHelper.getInstance().getActionFor(action_).getBlocksActions()) {
                    if (blocks == action.getId()) {
                        reasonForDisabled.append("Cannot be performed with "
                                + LookupHelper.getInstance().getActionFor(action_).getName());
                        return false;
                    }
                }

            }
        }

        return true;

    }

    @Exclude
    public List<CharacterState> getEnemyTargetsForEffect(GameState gameState, int effect) {

        ArrayList<CharacterState> ret = new ArrayList<>();

        for (PlayerState playerState : gameState.getPlayers()) {
            if (playerState.isMe()) {
                continue;
            }


            for (CharacterStateHero characterStateHero : playerState.getTeam().getHeroes()) {

                for (CharacterEffect effect1 : characterStateHero.getCharacterEffects()) {
                    if (effect == effect1.getId()) {

                        if (effect == CharacterEffect.ID_HIDDEN) {
                            // only add detected hidden characters

                            if (characterStateHero.isDetectedByPlayer(gameState.getMe().getIdentifier())) {
                                ret.add(characterStateHero);
                            }

                        } else {
                            ret.add(characterStateHero);
                            break;
                        }


                    }
                }


            }

        }
        return ret;
    }

    @Exclude
    private boolean isTargetEffectSelfActive(List<Integer> targetEffectSelf,
                                             CharacterState playerCharacter) {

        List<CharacterEffect> effects = playerCharacter
                .getCharacterEffects();

        if (effects.isEmpty()) {
            return false;
        }

        if (isTargetForAny(targetEffectSelf, effects)) {
            return true;
        } else {
            return false;
        }

    }

    @Exclude
    private boolean isTarget(List<Integer> effects) {
        for (int i : effects) {
            if (i > 0) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    private boolean isTargetForAny(List<Integer> effects, int targetEffect, CharacterState playerCharacter) {
        for (int i : effects) {
            if (i == targetEffect) {
                return true;
            }

            if (i == CharacterEffect.ID_WOUNDED_PLACEHOLDER) {
                //this is not an actual effect but a placeholder for wounded
                if (playerCharacter != null && !playerCharacter.getModifiers().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Exclude
    public ArrayList<CharacterState> getAvailableTargetsForFriendlyAction(
            Action action, CharacterState playerCharacterSource,
            GameState gameState) {
        ArrayList<CharacterState> ret = new ArrayList<>();

        List<Integer> targetEffectFriendly = action.getTargetsEffectFriendly();

        if (targetEffectFriendly.isEmpty()) {
            ret.addAll(gameState.getOwnCharacters());
        } else {

            if (isTarget(targetEffectFriendly)) {

                List<CharacterState> characters = gameState.getOwnCharacters();
                for (CharacterState playerCharacter : characters) {

                    if (playerCharacter.isDead()) {
                        continue;
                    }
                    List<CharacterEffect> effects = playerCharacter
                            .getCharacterEffects();
                    for (CharacterEffect characterEffect : effects) {
                        if (isTargetForAny(targetEffectFriendly,
                                characterEffect.getId(), playerCharacter)) {
                            ret.add(playerCharacter);
                            break;
                        }

                    }

                    for (int effect : targetEffectFriendly) {
                        if (effect == CharacterEffect.ID_WOUNDED_PLACEHOLDER) {
                            if (!playerCharacter.getModifiers().isEmpty() && !ret.contains(playerCharacter)) {

                                ret.add(playerCharacter);
                            }
                        }
                    }
                }


            }
        }
        return ret;
    }

    @Exclude
    private boolean isTargetForAny(List<Integer> targetEffectSelf,
                                   List<CharacterEffect> effects) {

        for (CharacterEffect characterEffect : effects) {
            if (isTargetForAny(targetEffectSelf, characterEffect.getId(), null)) {
                return true;
            }
        }
        return false;

    }


    /**
     * util method for figuring out which attachable wargear a wargear has
     *
     * @param wargearId
     * @param charactersWargear
     * @return
     */
    @Exclude
    public ArrayList<WargearAccessory> getAccessoriesFor(int wargearId,
                                                         List<Integer> charactersWargear) {
        ArrayList<WargearAccessory> ret = new ArrayList<>();

        for (Integer wargear : charactersWargear) {
            if (!(LookupHelper.getInstance().getWargearFor(wargear) instanceof WargearAccessory)) {
                continue;
            }

            List<Integer> attachableTo = ((WargearAccessory) LookupHelper.getInstance().getWargearFor(wargear))
                    .getAttachableTo();
            for (int i : attachableTo) {
                if (i == wargearId) {
                    ret.add((WargearAccessory) LookupHelper.getInstance().getWargearFor(wargear));
                    continue;
                }
            }
        }

        return ret;
    }


    public String getTribeId() {
        return tribeId;
    }

    public void setTribeId(String tribeId) {
        this.tribeId = tribeId;
    }

    public String getBluePrintIdentifier() {
        return bluePrintIdentifier;
    }

    public void setBluePrintIdentifier(String bluePrintIdentifier) {
        this.bluePrintIdentifier = bluePrintIdentifier;
    }

    public String getRoleIconURL() {
        return roleIconURL;
    }

    public void setRoleIconURL(String roleIconURL) {
        this.roleIconURL = roleIconURL;
    }

    public int getCurrentSuppressionRawValue() {
        return currentSuppressionRawValue;
    }

    public void setCurrentSuppressionRawValue(int currentSuppressionRawValue) {
        this.currentSuppressionRawValue = currentSuppressionRawValue;
    }
}
