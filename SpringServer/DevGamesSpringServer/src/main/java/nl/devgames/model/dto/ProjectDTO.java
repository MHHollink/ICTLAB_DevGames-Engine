package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.Project;

/**
 * Created by Marcel on 14-4-2016.
 */
public class ProjectDTO extends ModelDTO<ProjectDTO, Project> {
    @Override
    public Project toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public ProjectDTO createFromJsonObject(JsonObject object) {
        return null;
    }
}
