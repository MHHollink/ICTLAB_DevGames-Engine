package baecon.devgames.database.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;

@DatabaseTable(tableName = "users")
public class User extends AbsSynchronizable implements Serializable {

    public static class Column {
        public static final String USERNAME = "username";
        public static final String GIT_USER = "git_username";
        public static final String PROJECTS = "projects";
        public static final String COMMITS = "commits";
        public static final String GCM_KEY = "gcm_registration_key";
    }

    @DatabaseField(columnName = Column.USERNAME)
    private String username;

    @DatabaseField(columnName = Column.GIT_USER)
    private String gitUsername;

    @DatabaseField(columnName = Column.PROJECTS, dataType = DataType.SERIALIZABLE, foreign = true, foreignAutoRefresh = true)
    private HashSet<Project> projects;

    @DatabaseField(columnName = Column.COMMITS, dataType = DataType.SERIALIZABLE, foreign = true, foreignAutoRefresh = true)
    private HashSet<Commit> commits;

    @DatabaseField(columnName = Column.GCM_KEY)
    private String gcmKey;

    public User(Long uuid, String username, String gitUsername, HashSet<Project> projects, HashSet<Commit> commits, String gcmKey) {
        this.id = uuid;
        this.username = username;
        this.gitUsername = gitUsername;
        this.projects = projects;
        this.commits = commits;
        this.gcmKey = gcmKey;
    }

    public User(Long id, String username, String gitUsername) {
        this(id, username, gitUsername, new HashSet<Project>(), new HashSet<Commit>(), null);
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

    public HashSet<Project> getProjects() {
        return projects;
    }

    public HashSet<Commit> getCommits() {
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
                "username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", projects=" + projects +
                ", commits=" + commits +
                ", gcmKey='" + gcmKey + '\'' +
                '}';
    }

    @Override
    public boolean contentEquals(Object other) {
        return false;
    }
}
