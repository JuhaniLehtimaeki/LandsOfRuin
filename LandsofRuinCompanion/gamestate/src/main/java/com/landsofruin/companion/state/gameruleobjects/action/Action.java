package com.landsofruin.companion.state.gameruleobjects.action;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

@ObjectiveCName("Action")
public class Action {

    public static final int ACTION_ID_SHOOTING = 2;
    public static final int ACTION_ID_AIM_AND_SHOOTING = 18;
    public static final int ACTION_ID_CC = 3;

    public static final int ACTION_ID_THROW = 15;
    public static final int ACTION_ID_EXECUTE_SLAVE = 20;
    public static final int ACTION_ID_ARTILLERY_STRIKE = 21;
    public static final int ACTION_ID_FIRE_BAZOOKA = 22;
    public static final int ACTION_ID_REVEAL_ENEMY = 24;


    public static final int ACTION_ID_FIRST_AID = 7;
    public static final int ACTION_ID_FORCE_FRENZY = 19;


    public static final int ACTION_MODE_NORMAL = 3;

    public static final int ACTION_MORALE_TYPE_ANY = 1;
    public static final int ACTION_MORALE_OK_CONFUSED = 2;
    public static final int ACTION_MORALE_OK = 3;


    private int id;
    private String description;
    private int actionPoints;
    private String name;
    private int maxTimesPerBattle;
    private String requiresSquad;

    private List<Integer> addsEffectSelf;
    private List<Integer> addsEffectEnemy;
    private List<Integer> addsEffectFriendly;

    private List<Integer> targetsEffectSelf;
    private List<Integer> targetsEffectEnemy;
    private List<Integer> targetsEffectFriendly;

    private int actionType;
    private List<Integer> removesEffects;
    private List<Integer> blocksActions;
    private int assignToCloseSupportSquad = 0;


    private int order = 1000;

    public Action(int id, String description, int actionPoints, int type,
                  String name) {
        super();
        this.id = id;
        this.description = description;
        this.actionPoints = actionPoints;
        this.name = name;
        this.actionType = ACTION_MODE_NORMAL;
    }

    public Action() {
    }

    public String getDescription() {
        return description;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActionPoints(int actionPoints) {
        this.actionPoints = actionPoints;
    }

    @Override
    public String toString() {
        return "action: " + name + " (" + actionPoints + "AP)";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getAddsEffectSelf() {
        return addsEffectSelf;
    }

    public void setAddsEffectSelf(List<Integer> addsEffectSelf) {
        this.addsEffectSelf = addsEffectSelf;
    }

    public List<Integer> getAddsEffectEnemy() {
        return addsEffectEnemy;
    }

    public void setAddsEffectEnemy(List<Integer> addsEffectEnemy) {
        this.addsEffectEnemy = addsEffectEnemy;
    }

    public List<Integer> getAddsEffectFriendly() {
        return addsEffectFriendly;
    }

    public void setAddsEffectFriendly(List<Integer> addsEffectFriendly) {
        this.addsEffectFriendly = addsEffectFriendly;
    }

    public List<Integer> getTargetsEffectSelf() {
        return targetsEffectSelf;
    }

    public void setTargetsEffectSelf(List<Integer> targetsEffectSelf) {
        this.targetsEffectSelf = targetsEffectSelf;
    }

    public List<Integer> getTargetsEffectEnemy() {
        return targetsEffectEnemy;
    }

    public void setTargetsEffectEnemy(List<Integer> targetsEffectEnemy) {
        this.targetsEffectEnemy = targetsEffectEnemy;
    }

    public List<Integer> getTargetsEffectFriendly() {
        return targetsEffectFriendly;
    }

    public void setTargetsEffectFriendly(List<Integer> targetsEffectFriendly) {
        this.targetsEffectFriendly = targetsEffectFriendly;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public List<Integer> getRemovesEffects() {
        return removesEffects;
    }

    public void setRemovesEffects(List<Integer> removesEffects) {
        this.removesEffects = removesEffects;
    }

    public List<Integer> getBlocksActions() {
        return blocksActions;
    }

    public void setBlocksActions(List<Integer> blocksActions) {
        this.blocksActions = blocksActions;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public int getAssignToCloseSupportSquad() {
        return assignToCloseSupportSquad;
    }

    public void setAssignToCloseSupportSquad(int assignToCloseSupportSquad) {
        this.assignToCloseSupportSquad = assignToCloseSupportSquad;
    }

    public int getMaxTimesPerBattle() {
        return maxTimesPerBattle;
    }

    public void setMaxTimesPerBattle(int maxTimesPerBattle) {
        this.maxTimesPerBattle = maxTimesPerBattle;
    }

    public String getRequiresSquad() {
        return requiresSquad;
    }

    public void setRequiresSquad(String requiresSquad) {
        this.requiresSquad = requiresSquad;
    }
}
