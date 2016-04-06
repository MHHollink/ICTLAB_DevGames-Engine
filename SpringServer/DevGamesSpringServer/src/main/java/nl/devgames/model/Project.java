package nl.devgames.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Project extends Model<Project> {

    private String name;
    private String description;


    public Project() {
    }

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public Project createFromJsonObject(JsonObject object) {
        Project o = new Gson().fromJson(object.get("data"), Project.class);
        o.setId(object.get("id").getAsLong());
        return o;
    }
}
