package com.landsofruin.companion.state.builder;

import com.landsofruin.companion.provider.snapshots.CharacterSnapshot;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.SquadMember;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.gametracker.wargear.WargearManager;

import java.util.HashMap;
import java.util.LinkedList;

public class CharacterStateBuilder {


    public static CharacterState buildFrom(CharacterSnapshot snapshot, String tribId, HashMap<String, Integer> squadCounts) {
        CharacterState state;
        if (LookupHelper.getInstance().getCharacterTypeFor(snapshot.getCharacterType()).getType() == CharacterType.TYPE_HERO) {
            state = new CharacterStateHero(snapshot.getServerId());

            ((CharacterStateHero) state).setSquads(snapshot.getSquads());
        } else {

            int count = squadCounts.get(snapshot.getServerId());

            state = new CharacterStateSquad(snapshot.getServerId());
            ((CharacterStateSquad) state).setHeroIdentifier(snapshot.getHeroIdentifier());


            for (int i = 0; i < count; ++i) {
                ((CharacterStateSquad) state).addSquadMember(new SquadMember());
            }

        }
        state.setRoleIconURL(snapshot.getRoleIconURL());
        state.setBluePrintIdentifier(snapshot.getBlueprintId());
        state.setName(snapshot.getName());
        state.setCharacterType(snapshot.getCharacterType());

        int[] skills = snapshot.getSkills();
        for (int skill : skills) {
            state.addSkill(skill);
        }

        int[] wargear = snapshot.getWargear();
        for (int id : wargear) {
            LinkedList<Wargear> wgs = WargearManager.getInstance().getWargearByWeaponID(id);

            for (Wargear wg2 : wgs) {
                state.addWargear(wg2.getId());
            }
        }

        HashMap<String, Integer> ammo = new HashMap<>();

        for (int i = 0; i < snapshot.getAmmo().size(); ++i) {
            int key = snapshot.getAmmo().keyAt(i);
            int clips = snapshot.getAmmo().get(key);


            LinkedList<Wargear> wgs = WargearManager.getInstance().getWargearByWeaponID(key);

            if (!wgs.isEmpty()) {
                ammo.put(""+key, clips * ((WargearOffensive) wgs.getFirst()).getClipSize());
            }
        }

        state.setAllAmmo(ammo);

        state.setProfilePictureUri(snapshot.getProfilePictureUri());
        state.setCardPictureUri(snapshot.getCardPictureUri());
        state.setTribeId(tribId);
        return state;
    }
}
