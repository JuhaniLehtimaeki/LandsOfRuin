package com.landsofruin.companion.cards;

import android.support.v4.app.Fragment;

import com.landsofruin.companion.GameActivity;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;

/**
 * Created by juhani on 10/12/13.
 */
public abstract class CharacterCardFragment extends Fragment {


    public abstract CharacterState getCharacter();


    protected boolean isEndGamePrepareVisible() {

        if (getActivity() == null) {
            return true;
        }

        GameState game = ((GameActivity) getActivity()).getGame();

        for (PlayerState player : game.getPlayers()) {
            if (player.getPrepareEndGameState() != null) {
                return true;
            }
        }


        return false;
    }

}
