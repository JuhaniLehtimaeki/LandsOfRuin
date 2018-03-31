package com.landsofruin.companion.state.battlereport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhani on 20/01/16.
 */
public class BattleReportPlayer {


    private String id;
    private List<String> characters = new ArrayList<>();

    public BattleReportPlayer() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getCharacters() {
        return characters;
    }

    public void setCharacters(List<String> characters) {
        this.characters = characters;
    }
}
