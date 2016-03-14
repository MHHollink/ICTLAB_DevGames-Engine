package baecon.devgames.connection.client.dto;

import java.util.Set;

import baecon.devgames.model.Commit;
import baecon.devgames.model.Project;
import baecon.devgames.model.User;


public class ProjectDTO implements ModelDTO<Project> {

    private long id;
    private String name;
    private String description;
    private User owner;
    private Set<User> developers;
    private Set<Commit> commits;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Project toModel() {
        Project project = new Project();

        project.setName(name);
        project.setDescription(description);
        project.setOwner(owner);

        for (User dev : developers) {
            project.addDeveloper(dev);
        }

        for (Commit commit : commits) {
            project.addCommit(commit);
        }

        return project;
    }
}
