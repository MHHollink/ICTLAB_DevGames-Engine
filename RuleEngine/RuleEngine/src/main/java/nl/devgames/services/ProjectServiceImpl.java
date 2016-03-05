package nl.devgames.services;

import devgames.entities.Project;
import devgames.services.interfaces.ProjectService;

/**
 * Created by Wouter on 3/5/2016.
 */
public class ProjectServiceImpl extends GenericService<Project> implements ProjectService {
    @Override
    public Class<Project> getEntityType() {
        return Project.class;
    }
}
