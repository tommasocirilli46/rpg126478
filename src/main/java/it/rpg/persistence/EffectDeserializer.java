package it.rpg.persistence;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.rpg.model.Item;
import it.rpg.model.effect.AddItemEffect;
import it.rpg.model.effect.Effect;
import it.rpg.model.effect.GainExperienceEffect;
import it.rpg.model.effect.ModifyAttributeEffect;
import it.rpg.model.effect.ModifyHealthEffect;

import java.lang.reflect.Type;

/**
 * Builds the right {@link Effect} implementation from a JSON object carrying a
 * {@code "type"} discriminator. Keeping this knowledge in the persistence layer
 * lets the domain effects stay free of any serialization concern; adding a new
 * effect type means adding one {@code case} here plus the new class.
 */
final class EffectDeserializer implements JsonDeserializer<Effect> {

    @Override
    public Effect deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject o = json.getAsJsonObject();
        String kind = o.get("type").getAsString();
        return switch (kind) {
            case "MODIFY_HEALTH" -> new ModifyHealthEffect(o.get("amount").getAsInt());
            case "MODIFY_ATTRIBUTE" -> new ModifyAttributeEffect(
                    o.get("attribute").getAsString(), o.get("amount").getAsInt());
            case "GAIN_XP" -> new GainExperienceEffect(o.get("amount").getAsInt());
            case "ADD_ITEM" -> {
                JsonObject it = o.getAsJsonObject("item");
                String desc = it.has("description") ? it.get("description").getAsString() : "";
                yield new AddItemEffect(new Item(
                        it.get("id").getAsString(), it.get("name").getAsString(), desc));
            }
            default -> throw new JsonParseException("Tipo di effetto sconosciuto: " + kind);
        };
    }
}
