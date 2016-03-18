package baecon.devgames.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.squareup.okhttp.internal.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import baecon.devgames.util.Utils;

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

    @DatabaseField(columnName = Column.PROJECTS, dataType = DataType.SERIALIZABLE)
    private HashMap<Long, Project> projects;

    @DatabaseField(columnName = Column.COMMITS, dataType = DataType.SERIALIZABLE)
    private HashMap<Long, Commit> commits;

    @DatabaseField(columnName = Column.GCM_KEY)
    private String gcmKey;

    public User(Long uuid, String username, String gitUsername, HashMap<Long, Project> projects, HashMap<Long, Commit> commits, String gcmKey) {
        this.id = uuid;
        this.username = username;
        this.gitUsername = gitUsername;
        this.projects = projects;
        this.commits = commits;
        this.gcmKey = gcmKey;
    }

    public User(Long id, String username, String gitUsername) {
        this(id, username, gitUsername, new HashMap<Long, Project>(), new HashMap<Long, Commit>(), null);
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

    public String getGcmKey() {
        return gcmKey;
    }

    public void setGcmKey(String gcmKey) {
        this.gcmKey = gcmKey;
    }

    public HashMap<Long, Project> getProjects() {
        return projects;
    }

    public HashMap<Long, Commit> getCommits() {
        return commits;
    }

    public void addProject(Project project) {
        projects.put(project.getId(), project);
    }

    public void addProject(Project... project) {
        for (Project aProject : project) {
            projects.put(aProject.getId(), aProject);
        }
    }

    public void addCommit(Commit commit) {
        commits.put(commit.getId(), commit);
    }

    public void addCommit(Commit... commit) {
        for (Commit aCommit : commit) {
            commits.put(aCommit.getId(), aCommit);
        }
    }

    public void merge(User user) {
        if(!Utils.isEmpty(user.getUsername())) {
            username = user.getUsername();
        }
        if(!Utils.isEmpty(user.getGitUsername())) {
            gitUsername = user.getGitUsername();
        }
        if(!Utils.isEmpty(user.getCommits())) {
            commits = user.getCommits();
        }
        if(!Utils.isEmpty(user.getProjects())) {
            projects = user.getProjects();
        }
        if(!Utils.isEmpty(user.getGcmKey())) {
            gcmKey = user.getGcmKey();
        }
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
//        for (Commit commit : commits) {
//            score += commit.getScore();
//        }

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

    public void logout() {
        setGcmKey(null);
    }
}
