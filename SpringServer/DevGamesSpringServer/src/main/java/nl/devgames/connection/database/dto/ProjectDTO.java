package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Project;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import java.util.Set;

/**
 * Created by Marcel on 14-4-2016.
 */
public class ProjectDTO extends ModelDTO<ProjectDTO, Project> {

    public String name;
    public String description;
    public User creator;

    public Set<User> developers;

    @Override
    public Project toModel() {
        Project project = new Project();

        project.setId(id);
        project.setName(this.name);
        project.setDescription(this.description);
        project.setOwner(creator);
        project.setDevelopers(developers);

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

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
