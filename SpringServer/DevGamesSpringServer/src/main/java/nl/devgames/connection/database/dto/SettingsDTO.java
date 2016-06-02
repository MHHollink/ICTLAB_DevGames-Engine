package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Project;
import nl.devgames.model.Settings;
import nl.devgames.utils.L;

/**
 * Created by Jorikito on 28-May-16.
 */
public class SettingsDTO extends ModelDTO<SettingsDTO, Settings>{

    Project project;
    double issuesPerCommitThreshold;
    boolean pointStealing;
    boolean negativeScores;

    @Override
    public Settings toModel() {
        Settings settings = new Settings();

        settings.setId(id);
        settings.setProject(this.project);
        settings.setIssuesPerCommitThreshold(this.issuesPerCommitThreshold);
        settings.setPointStealing(this.pointStealing);
        settings.setNegativeScores(this.negativeScores);

        return settings;
    }

    @Override
    public boolean isValid() {
        boolean valid = project != null &&
                issuesPerCommitThreshold != 0d;

        if(!valid) {
            L.w("Settings is not valid! False indicates a problem: " +
                            "project:'%b', issuesPerCommitThreshold:'%b' ",
                    project != null,
                    issuesPerCommitThreshold != 0d
            );
        }

        return valid;
    }

    @Override
    public SettingsDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, SettingsDTO.class);
    }

    @Override
    public SettingsDTO createFromNeo4jData(JsonObject data) {
        SettingsDTO dto = new SettingsDTO().createFromJsonObject(
                data.get("data").getAsJsonObject()
        );
        dto.id = data.get("id").getAsLong();
        return dto;
    }

    @Override
    public boolean equalsInContent(SettingsDTO o) {
        return
                false;
    }

    @Override
    public String toString() {
        return "SettingsDTO{" +
                "project=" + project +
                ", issuesPerCommitThreshold=" + issuesPerCommitThreshold +
                ", pointStealing=" + pointStealing +
                ", negativeScores=" + negativeScores + '\'' +
                "} " + super.toString();
    }
}
