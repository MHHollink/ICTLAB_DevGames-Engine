package baecon.devgames.connection.client.dto;

import baecon.devgames.model.Project;


public class ProjectDTO implements ModelDTO<Project> {
    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Project toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
