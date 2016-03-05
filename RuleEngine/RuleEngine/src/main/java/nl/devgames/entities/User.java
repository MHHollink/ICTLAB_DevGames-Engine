package nl.devgames.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

/**
 * Created by Wouter on 3/5/2016.
 */
@NodeEntity
public class User extends Entity {
    private String username;
    private String githubUsername;
    private long points;

    @Relationship(type = "WORKS_ON", direction = Relationship.OUTGOING)
    private List projects;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public List getProjects() {
        return projects;
    }

    public void setProjects(List projects) {
        this.projects = projects;
    }
}
