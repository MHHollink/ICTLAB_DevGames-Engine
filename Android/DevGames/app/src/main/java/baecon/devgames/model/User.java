package baecon.devgames.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "users")
public class User {

    public static class Column {
        public static final String ID = "uuid";
        public static final String USERNAME = "username";
        public static final String GIT_USER = "git_username";
        public static final String PROJECTS = "projects";
        public static final String COMMITS = "commits";
    }

    @DatabaseField(columnName = Column.ID)
    private long id;

    @DatabaseField(columnName = Column.USERNAME)
    private String username;

    @DatabaseField(columnName = Column.GIT_USER)
    private String gitUsername;

    @DatabaseField(columnName = Column.PROJECTS)
    private List<Project> projects;

    @DatabaseField(columnName = Column.COMMITS)
    private List<Commit> commits;

    public User(Long uuid, String username, String gitUsername, List<Project> projects, List<Commit> commits) {
        this.id = uuid;
        this.username = username;
        this.gitUsername = gitUsername;
        this.projects = projects;
        this.commits = commits;
    }

    public User(String username, String gitUsername) {
        this(null, username, gitUsername, new ArrayList<Project>(), new ArrayList<Commit>());
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

    public long getId() {
        return id;
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
        return id == ((User) o).getId();
    }

    /**
     * TODO
     */
    public double getScore() {
        return 60;
    }
}
