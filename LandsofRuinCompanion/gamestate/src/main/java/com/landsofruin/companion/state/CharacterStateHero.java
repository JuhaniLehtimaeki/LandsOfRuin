package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhani on 23/01/16.
 */
@ObjectiveCName("CharacterStateSquad")
public class CharacterStateHero extends CharacterState {


    private List<String> squads = new ArrayList<>();

    public CharacterStateHero(String identifier) {
        super(identifier);
    }


    /**
     * required empty constructor for firebase. don't use in code!
     */
    public CharacterStateHero() {
        super(null);
    }


    public List<String> getSquads() {
        return squads;
    }

    public void addSquad(String squadId) {
        this.squads.add(squadId);
    }

    public void setSquads(List<String> squads) {
        this.squads = squads;
    }


    public String getMinionName(TeamState team) {

        for (CharacterStateSquad squad : team.getSquads()) {
            if (squads.contains(squad.getIdentifier())) {
                return squad.getName();
            }
        }
        return "N/A";

    }

    public void loseMinion(TeamState team) {

        for (CharacterStateSquad squad : team.getSquads()) {
            if (squads.contains(squad.getIdentifier())) {
                squad.killASquadMember();
                return;
            }
        }

    }


    public CharacterStateSquad getFirstCharacterStateSquad(TeamState team) {
        for (CharacterStateSquad squad : team.getSquads()) {
            if (squads.contains(squad.getIdentifier())) {
                return squad;
            }
        }
        return null;
    }


    public int getMinionCount(TeamState team) {
        for (CharacterStateSquad squad : team.getSquads()) {
            if (squads.contains(squad.getIdentifier())) {
                return squad.getSquadSize();
            }
        }
        return 0;
    }
}
