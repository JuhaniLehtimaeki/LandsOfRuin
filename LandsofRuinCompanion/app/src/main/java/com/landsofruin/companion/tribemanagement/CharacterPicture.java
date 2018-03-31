package com.landsofruin.companion.tribemanagement;

/**
 * Created by juhani on 07/02/16.
 */
public class CharacterPicture {

    private String id;
    private String cardPicture;
    private String profilePicture;

public CharacterPicture(){}

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCardPicture() {
        return cardPicture;
    }

    public void setCardPicture(String cardPicture) {
        this.cardPicture = cardPicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
