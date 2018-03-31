package com.landsofruin.companion.state.icons;

/**
 * Created by juhani on 01/07/15.
 */
public interface IconMapper {

    int getIconResourceForAction(int actionId);

    int getIconResourceForIcon(int id);

    int getIconResourceForEffect(int effectId);


    String getStringIconResourceForAction(int actionId);

    String getStringIconResourceForIcon(int id);

    String getStringIconResourceForEffect(int effectId);
}
