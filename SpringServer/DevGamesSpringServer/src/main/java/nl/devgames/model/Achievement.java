package nl.devgames.model;

import nl.devgames.rules.AchievementType;

import java.util.UUID;

/**
 * Created by Jorikito on 21-Jun-16.
 */
public class Achievement extends Model {

    private AchievementType type;

    private String uuid; // using uuid for unique field

    public Achievement() {

    }

    public Achievement(AchievementType type) {
        this.type = type;
    }

    public void generateUUID() {
        uuid = UUID.randomUUID().toString();
    }

    public AchievementType getType() {
        return type;
    }

    public void setType(AchievementType type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    @Override
    public String toString() {
        return "Achievement{" +
                "type=" + type.name() +
                "} " + super.toString();
    }
}
