package com.landsofruin.gametracker.transition;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.landsofruin.companion.state.transition.Transition;

import java.lang.reflect.Type;

/**
 * Adapter for serializing and deserializing {@link Transition} objects.
 */
public class TransitionAdapter implements JsonSerializer<Transition>, JsonDeserializer<Transition> {
    private static final String PROPERTY_TYPE = "type";
    private static final String PROPERTY_DATA = "data";

    @Override
    public JsonElement serialize(Transition transition, Type type, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();

        wrapper.addProperty(PROPERTY_TYPE, transition.getClass().getName());
        wrapper.add(PROPERTY_DATA, context.serialize(transition));

        return wrapper;
    }

    @Override
    public Transition deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject wrapper = (JsonObject) element;
        JsonElement typeName = get(wrapper, PROPERTY_TYPE);
        JsonElement data = get(wrapper, PROPERTY_DATA);

        Type actualType = typeForName(typeName);
        try {
            return context.deserialize(data, actualType);
        } catch (JsonSyntaxException e) {
            Log.e("Transition", "element " + element + "type " + type, e);

            throw e;
        }
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
