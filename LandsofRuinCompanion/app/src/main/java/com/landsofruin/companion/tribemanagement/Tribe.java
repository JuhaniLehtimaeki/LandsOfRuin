package com.landsofruin.companion.tribemanagement;

import com.google.firebase.database.Exclude;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by juhani on 29/12/15.
 */
public class Tribe {

    private String id;
    private String name;
    private String smallTribeLogoUri;
    private String largeTribeLogoUri;

    private Map<String, TribeCharacter> characters = new HashMap<>();

    public Tribe() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public Collection<TribeCharacter> getCharactersValues() {
        return characters.values();
    }

    public String removeSpawnedCharacter(String blueprintId) {
        for (TribeCharacter tribeCharacter : this.getCharactersValues()) {

            if (tribeCharacter.getBlueprintId().equals(blueprintId)) {
                this.characters.remove(tribeCharacter);
                return tribeCharacter.getId();
            }
        }
        return null;
    }

    public Map<String, TribeCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(Map<String, TribeCharacter> characters) {
        this.characters = characters;
    }

    public String getSmallTribeLogoUri() {
        return smallTribeLogoUri;
    }

    public void setSmallTribeLogoUri(String smallTribeLogoUri) {
        this.smallTribeLogoUri = smallTribeLogoUri;
    }

    public String getLargeTribeLogoUri() {
        return largeTribeLogoUri;
    }

    public void setLargeTribeLogoUri(String largeTribeLogoUri) {
        this.largeTribeLogoUri = largeTribeLogoUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
