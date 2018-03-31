package com.landsofruin.companion.state.gameruleobjects.skill;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

@ObjectiveCName("Skill")
public class Skill {


    public static final int SKILL_ID_INFILTRATE = 15;



    private int id;
    private int maxCount;
    private String name;
    private String description;
    private List<Integer> enablesActions = new ArrayList<>();
    private List<Integer> enablesSkills = new ArrayList<>();
    private int offensiveModifier;
    private int defensiveModifier;

    private List<String> requiresEquipmentType = new ArrayList<>();
    private List<String> requiresEquipmentCategory = new ArrayList<>();
    private List<Integer> requiresEquipmentId = new ArrayList<>();

    private int leadershipModifier;
    private List<Integer> addsDefaultEffectsPregame = new ArrayList<>();

    private int gearValue = 0;

    public Skill() {
    }
//
//    public Skill(int id, String name, String description, List<Integer> enablesActions,
//                 int offensiveModifier, int defensiveModifier) {
//        super();
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.enablesActions = enablesActions;
//        this.offensiveModifier = offensiveModifier;
//        this.defensiveModifier = defensiveModifier;
//    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getEnablesActions() {
        return enablesActions;
    }

    public int getOffensiveModifier() {
        return offensiveModifier;
    }

    public void setDefensiveModifier(int defensiveModifier) {
        this.defensiveModifier = defensiveModifier;
    }

    public void setOffensiveModifier(int offensiveModifier) {
        this.offensiveModifier = offensiveModifier;
    }

    public int getDefensiveModifier() {
        return defensiveModifier;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void setEnablesSkills(List<Integer> enablesSkills) {
        this.enablesSkills = enablesSkills;
    }

    public void setRequiresEquipmentCategory(List<String> requiresEquipmentCategory) {
        this.requiresEquipmentCategory = requiresEquipmentCategory;
    }

    public void setRequiresEquipmentId(List<Integer> requiresEquipmentId) {
        this.requiresEquipmentId = requiresEquipmentId;
    }

    public void setRequiresEquipmentType(List<String> requiresEquipmentType) {
        this.requiresEquipmentType = requiresEquipmentType;
    }

    public List<String> getRequiresEquipmentCategory() {
        return requiresEquipmentCategory;
    }

    public List<Integer> getRequiresEquipmentId() {
        return requiresEquipmentId;
    }

    public List<String> getRequiresEquipmentType() {
        return requiresEquipmentType;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setLeadershipModifier(int leadershipModifier) {
        this.leadershipModifier = leadershipModifier;
    }

    public int getLeadershipModifier() {
        return leadershipModifier;
    }

    public List<Integer> getEnablesSkills() {
        return enablesSkills;
    }

    public void setAddsDefaultEffectsPregame(List<Integer> addsDefaultEffectsPregame) {
        this.addsDefaultEffectsPregame = addsDefaultEffectsPregame;
    }

    public void setEnablesActions(List<Integer> enablesActions) {
        this.enablesActions = enablesActions;
    }

    public List<Integer> getAddsDefaultEffectsPregame() {
        return addsDefaultEffectsPregame;
    }

    public void setGearValue(int gearValue) {
        this.gearValue = gearValue;
    }

    public int getGearValue() {
        return gearValue;
    }

}
