package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Achievement;
import nl.devgames.rules.AchievementType;
import nl.devgames.utils.L;

/**
 * Created by Jorikito on 21-Jun-16.
 */
public class AchievementDTO extends ModelDTO<AchievementDTO, Achievement> {
    AchievementType type;

    @Override
    public Achievement toModel() {
        Achievement achievement = new Achievement();

        achievement.setType(this.type);

        return achievement;
    }

    @Override
    public boolean isValid() {
        boolean valid = type != null;

        if(!valid) {
            L.w("Achievement is not valid! False indicates a problem: " +
                            "type:'%b'",
                    type != null
            );
        }

        return valid;
    }

    @Override
    public AchievementDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, AchievementDTO.class);
    }

    @Override
    public AchievementDTO createFromNeo4jData(JsonObject data) {
        AchievementDTO dto = new AchievementDTO().createFromJsonObject(
                data.get("data").getAsJsonObject()
        );
        dto.id = data.get("id").getAsLong();
        return dto;
    }

    @Override
    public boolean equalsInContent(AchievementDTO other) {
        return id.longValue() == other.id.longValue();
    }

    @Override
    public String toString() {
        return "AchievementDTO{" +
                "type=" + type +
                "} " + super.toString();
    }
}
