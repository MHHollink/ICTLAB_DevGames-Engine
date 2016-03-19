package nl.devgames.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;


@NodeEntity(label = "User")
public class User extends AbsModel {

    private String username;
    private String gitUsername;

    @Relationship(type = "isDeveloperBy")
    private Set<Project> projects;

    @Relationship(type = "isCommittedBy", direction = Relationship.INCOMING)
    private Set<Commit> commits;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Commit> getCommits() {
        return commits;
    }

    public void setCommits(Set<Commit> commits) {
        this.commits = commits;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", projects=" + projects +
                ", commits=" + commits +
                '}';
    }
}
