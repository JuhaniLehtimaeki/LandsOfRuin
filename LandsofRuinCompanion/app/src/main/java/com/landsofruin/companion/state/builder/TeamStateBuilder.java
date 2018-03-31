package com.landsofruin.companion.state.builder;

import com.landsofruin.companion.provider.snapshots.CharacterSnapshot;
import com.landsofruin.companion.provider.snapshots.TribeSnapshot;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.TeamState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamStateBuilder {


    public static TeamState buildFrom(String tribeIdentifier, TribeSnapshot tribeSnapshot, List<CharacterSnapshot> team, HashMap<String, Integer> squadCounts) {
        TeamState state = new TeamState(
                tribeIdentifier,
                tribeSnapshot.getSmallTribeLogoUri(),
                tribeSnapshot.getLargeTribeLogoUri()
        );

        List<CharacterStateHero> heroes = new ArrayList<>();
        List<CharacterStateSquad> squads = new ArrayList<>();
        for (CharacterSnapshot snapshot : team) {
            CharacterState character = CharacterStateBuilder.buildFrom(snapshot, tribeSnapshot.getServerId(), squadCounts);

            if (character instanceof CharacterStateHero) {
                heroes.add((CharacterStateHero) character);
            } else if (character instanceof CharacterStateSquad) {
                squads.add((CharacterStateSquad) character);
            }

        }


        state.setHeroes(heroes);
        state.setSquads(squads);

        return state;
    }
}
