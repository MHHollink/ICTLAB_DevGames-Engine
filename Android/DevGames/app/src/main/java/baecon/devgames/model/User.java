package baecon.devgames.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DatabaseTable(tableName = "users")
public class User extends AbsSynchronizable implements Serializable {

    public static class Column {
        public static final String USERNAME = "username";
        public static final String GIT_USER = "git_username";
        public static final String PROJECTS = "projects";
        public static final String COMMITS = "commits";
    }

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

    public User(Long id, String username, String gitUsername) {
        this(id, username, gitUsername, new ArrayList<Project>(), new ArrayList<Commit>());
    }

    public User(Long id){
        this(id, "", "");
    }

    public User() {
        this(0l);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
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
        double score=0;
        for (Commit commit : commits) {
            score += commit.getScore();
        }

        return score;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", projects=" + projects +
                ", commits=" + commits +
                '}';
    }

    @Override
    public boolean contentEquals(Object other) {
        return false;
    }
}
