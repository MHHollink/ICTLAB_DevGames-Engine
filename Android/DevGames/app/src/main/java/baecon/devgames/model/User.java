package baecon.devgames.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class User {

    private String uuid;
    private String username;

    private String gitUsername;

    private List<Project> projects;
    private List<Commit> commits;

    public User(String uuid, String username, String gitUsername, List<Project> projects, List<Commit> commits) {
        this.uuid = uuid;
        this.username = username;
        this.gitUsername = gitUsername;
        this.projects = projects;
        this.commits = commits;
    }

    public User(String username, String gitUsername) {
        this(UUID.randomUUID().toString(), username, gitUsername, new ArrayList<Project>(), new ArrayList<Commit>());
    }

    public User(String username) {
        this(username, "");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public void addProject(Project... project) {
        Collections.addAll(projects, project);
    }

    public void addCommit(Commit commit) {
        commits.add(commit);
    }
    public void addCommit(Commit... commit) {
        Collections.addAll(commits, commit);
    }

    @Override
    public boolean equals(Object o) {
        return uuid.equals(((User)o).getUuid());
    }

    /**
     * TODO
     */
    public double getScore() {
        return 60;
    }
}
