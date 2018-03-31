package com.landsofruin.companion.state;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.j2objc.annotations.ObjectiveCName;
import com.landsofruin.companion.state.gameruleobjects.action.ActionContainerForAssignActions;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by juhani on 23/01/16.
 */
@ObjectiveCName("CharacterStateSquad")
public class CharacterStateSquad extends CharacterState {

    private int deadSquadMembers = 0;

    private List<SquadMember> members = new LinkedList<>();

    private String heroIdentifier;


    /**
     * required empty constructor for firebase. don't use in code!
     */
    public CharacterStateSquad() {
        super(null);
    }

    public CharacterStateSquad(String identifier) {
        super(identifier);
    }

    @Exclude
    public int getSquadSize() {
        return members.size();
    }

    public void addSquadMember(SquadMember squadMember) {
        this.members.add(squadMember);
    }

    @Override
    public void reduceAmmoBy(int id, int ammo) {
        for (SquadMember member : this.members) {
            member.reduceAmmoBy(id, ammo);
        }
    }


    @Override
    @Exclude
    public int getRemainingActionPoints(GameState gamestate) {
        if (getSquadSize() <= 0) {
            return 0;
        }

        return super.getRemainingActionPoints(gamestate);
    }

    @Override
    @Exclude
    public List<ActionContainerForAssignActions> getAvailableActionsForAssignActions(GameState gameState) {
        if (getSquadSize() <= 0) {
            return new LinkedList<>();
        }
        return super.getAvailableActionsForAssignActions(gameState);
    }

    @Override
    @Exclude
    public List<Integer> getCurrentActions() {
        if (getSquadSize() <= 0) {
            return new LinkedList<>();
        }
        return super.getCurrentActions();
    }

    /**
     * @return true if a squad member was killed
     */
    public boolean killASquadMember() {
        if (this.members.isEmpty()) {
            return false;
        }
        ++deadSquadMembers;
        this.members.remove(this.members.size() - 1);
        return true;
    }

    @Override
    @Exclude
    public void setAllAmmo(HashMap<String, Integer> ammo) {

        for (SquadMember member : this.members) {
            member.setAllAmmo((HashMap<String, Integer>) ammo.clone());
        }
    }

    @Exclude
    private void setAmmo(int id, int ammo) {

        if (ammo < 0) {
            ammo = 0;
        }
        for (SquadMember member : this.members) {
            member.setAmmoForWeapon(id, ammo);
        }
    }

    @Override
    @Exclude
    public int getAmmoFor(int id) {
        int ret = 0;

        for (SquadMember member : this.members) {
            ret += member.getAmmoFor(id);
        }
        return ret;
    }


    @Exclude
    public float getTotalItemWeight() {

        float totalWeight = 0;
        ArrayList<Integer> handledWeapons = new ArrayList<>();

        for (Integer wargear : getWargear()) {
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
                totalWeight += (clips * wgOffensive.getClipWeight() / getSquadSize());

            }

        }
        return totalWeight;
    }

    @Exclude
    public CharacterState getHeroCharacterState(GameState gameState) {
        TeamState team = gameState.findTeamByCharacterIdentifier(this.getIdentifier());

        for (CharacterState characterState : team.getHeroes()) {
            if (characterState.getIdentifier().equals(getHeroIdentifier())) {
                return characterState;
            }
        }
        return null;
    }

    public String getHeroIdentifier() {
        return heroIdentifier;
    }

    public void setHeroIdentifier(String heroIdentifier) {
        this.heroIdentifier = heroIdentifier;
    }


    @Exclude
    public int getMovementAllowanceForSquad(GameState gameState, TeamState teamState) {

        CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType());


        switch (characterType.getType()) {
            case CharacterType.TYPE_SQUAD_INDEPENDENT:
                return getMovementAllowance(gameState);
            case CharacterType.TYPE_SQUAD_SLAVE:
                return -1;
        }

        //    case CharacterType.TYPE_SQUAD_CLOSE_SUPPORT:

        for (CharacterStateHero hero : teamState.getHeroes()) {
            if (getHeroIdentifier().equals(hero.getIdentifier())) {
                return hero.getMovementAllowance(gameState);
            }
        }

        Log.e("squad movement", "Something failed in squad movement determination");
        return 0;
    }


    @Exclude
    public int getMovementAllowanceForNextTurnForSquad(GameState gameState, TeamState teamState) {

        CharacterType characterType = LookupHelper.getInstance().getCharacterTypeFor(getCharacterType());


        switch (characterType.getType()) {
            case CharacterType.TYPE_SQUAD_INDEPENDENT:
                return getMovementAllowanceForNextTurn(gameState);
            case CharacterType.TYPE_SQUAD_SLAVE:
                return -1;
        }

        //    case CharacterType.TYPE_SQUAD_CLOSE_SUPPORT:

        for (CharacterStateHero hero : teamState.getHeroes()) {
            if (getHeroIdentifier().equals(hero.getIdentifier())) {
                return hero.getMovementAllowanceForNextTurn(gameState);
            }
        }

        Log.e("squad movement", "Something failed in squad movement determination");
        return 0;
    }

    public int getDeadSquadMembers() {
        return deadSquadMembers;
    }


    public List<SquadMember> getMembers() {
        return members;
    }

    public void setMembers(List<SquadMember> members) {
        this.members = members;
    }
}
