package it.rpg.persistence;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.rpg.model.requirement.HasItemRequirement;
import it.rpg.model.requirement.MinAttributeRequirement;
import it.rpg.model.requirement.MinHealthRequirement;
import it.rpg.model.requirement.MinLevelRequirement;
import it.rpg.model.requirement.Requirement;

import java.lang.reflect.Type;

/**
 * Builds the right {@link Requirement} implementation from a JSON object
 * carrying a {@code "type"} discriminator. See {@link EffectDeserializer} for
 * the rationale.
 */
final class RequirementDeserializer implements JsonDeserializer<Requirement> {

    @Override
    public Requirement deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject o = json.getAsJsonObject();
        String kind = o.get("type").getAsString();
        return switch (kind) {
            case "HAS_ITEM" -> new HasItemRequirement(
                    o.get("itemId").getAsString(),
                    o.has("label") ? o.get("label").getAsString() : null);
            case "MIN_ATTRIBUTE" -> new MinAttributeRequirement(
                    o.get("attribute").getAsString(), o.get("value").getAsInt());
            case "MIN_HEALTH" -> new MinHealthRequirement(o.get("value").getAsInt());
            case "MIN_LEVEL" -> new MinLevelRequirement(o.get("value").getAsInt());
            default -> throw new JsonParseException("Tipo di requisito sconosciuto: " + kind);
        };
    }
}
