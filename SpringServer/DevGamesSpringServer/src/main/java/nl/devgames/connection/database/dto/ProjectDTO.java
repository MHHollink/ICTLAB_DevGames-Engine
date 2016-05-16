package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Project;
import nl.devgames.utils.L;

/**
 * Created by Marcel on 14-4-2016.
 */
public class ProjectDTO extends ModelDTO<ProjectDTO, Project> {

    public String name;
    public String description;

    @Override
    public Project toModel() {
        Project project = new Project();

        project.setName(this.name);
        project.setDescription(this.description);

        return project;
    }

    @Override
    public boolean isValid() {
        boolean valid = name != null &&
                description != null;

        if(!valid) {
            L.w("Project is not valid! False indicates a problem: " +
                            "name:'%b', description:'%b'",
                    name != null,
                    description != null
            );
        }

        return valid;
    }

    @Override
    public ProjectDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, ProjectDTO.class);
    }

    @Override
    public ProjectDTO createFromNeo4jData(JsonObject data) {
        ProjectDTO dto = new ProjectDTO().createFromJsonObject(
                data.get("data").getAsJsonObject()
        );
        dto.id = data.get("id").getAsLong();
        return dto;
    }

    @Override
    public boolean equalsInContent(ProjectDTO other) {
        return false;
    }
}
