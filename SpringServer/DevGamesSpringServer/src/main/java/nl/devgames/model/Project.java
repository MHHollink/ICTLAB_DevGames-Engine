package nl.devgames.model;

import java.util.Set;

public class Project extends Model {


    public enum Relations {
        IS_LEAD_BY, CREATED_BY, HAS_SETTINGS
    }

    private String name;
    private String description;
    private User owner;
    private String token;
    public Settings settings;
    public Set<User> developers;

    public Project() {
    }

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Project(String name, String description, String token) {
        this.name = name;
        this.description = description;
        this.token = token;
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

    public User getOwner() {
        return owner;
    }

    public Set<User> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<User> developers) {
        this.developers = developers;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", token='" + token + '\'' +
                ", settings=" + settings +
                ", developers=" + developers +
                "} " + super.toString();
    }
}
