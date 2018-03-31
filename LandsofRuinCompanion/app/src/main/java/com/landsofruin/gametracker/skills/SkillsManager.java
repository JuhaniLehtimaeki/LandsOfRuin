package com.landsofruin.gametracker.skills;

import com.landsofruin.companion.state.gameruleobjects.skill.Skill;

import java.util.HashMap;
import java.util.LinkedList;

public class SkillsManager {

    private static SkillsManager instance = new SkillsManager();
    private LinkedList<Skill> skills = new LinkedList<>();
    private HashMap<Integer, Skill> skillsLookupCache = new HashMap<>();

    private SkillsManager() {
    }

    public static SkillsManager getInstance() {
        return instance;
    }

    public LinkedList<Skill> getSkills() {
        return skills;
    }


    public Skill getSkillByID(int id) {
        Skill ret = skillsLookupCache.get(id);
        if (ret != null) {
            return ret;
        }


        for (Skill skill : skills) {
            if (id == skill.getId()) {
                skillsLookupCache.put(id, skill);
                return skill;
            }
        }
        return null;
    }


    public void clearData() {
        this.skills.clear();
        this.skillsLookupCache.clear();
    }

    public void addData(Skill skill) {
        this.skills.add(skill);
    }


}
