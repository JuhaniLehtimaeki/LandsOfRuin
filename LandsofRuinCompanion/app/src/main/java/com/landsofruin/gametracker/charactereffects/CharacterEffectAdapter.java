package com.landsofruin.gametracker.charactereffects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.landsofruin.companion.state.gameruleobjects.charactereffect.CharacterEffect;

import java.lang.reflect.Type;

public class CharacterEffectAdapter implements JsonSerializer<CharacterEffect>, JsonDeserializer<CharacterEffect> {
    private static final String PROPERTY_TYPE = "type";
    private static final String PROPERTY_DATA = "data";

    @Override
    public JsonElement serialize(CharacterEffect effect, Type type, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();

        wrapper.addProperty(PROPERTY_TYPE, effect.getClass().getName());
        wrapper.add(PROPERTY_DATA, context.serialize(effect));

        return wrapper;
    }

    @Override
    public CharacterEffect deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject wrapper = (JsonObject) element;
        JsonElement typeName = get(wrapper, PROPERTY_TYPE);
        JsonElement data = get(wrapper, PROPERTY_DATA);

        Type actualType = typeForName(typeName);

        return context.deserialize(data, actualType);
    }

    private Type typeForName(JsonElement element) {
        try {
            return Class.forName(element.getAsString());
        } catch (ClassNotFoundException exception) {
            throw new JsonParseException(exception);
        }
    }

    private JsonElement get(JsonObject wrapper, String memberName) {
        final JsonElement element = wrapper.get(memberName);
        if (element == null) {
            throw new JsonParseException("Missing member " + memberName + " in wrapper object");
        }
        return element;
    }
}
