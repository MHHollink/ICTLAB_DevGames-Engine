package baecon.devgames.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

@DatabaseTable(tableName = "projects")
public class Project extends AbsSynchronizable implements Serializable {

    public static class Column {

        public static final String OWNER = "owner";
        public static final String DEVELOPERS = "developers";
        public static final String COMMITS = "commits";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }
    @DatabaseField(columnName = Column.OWNER, dataType = DataType.SERIALIZABLE, foreign = true, foreignAutoRefresh = true)
    private User owner;

    @DatabaseField(columnName = Column.DEVELOPERS, dataType = DataType.SERIALIZABLE)
    private HashMap<Long, User> developers;

    @DatabaseField(columnName = Column.COMMITS, dataType = DataType.SERIALIZABLE)
    private HashMap<Long, Commit> commits;

    @DatabaseField(columnName = Column.NAME)
    private String name;

    @DatabaseField(columnName = Column.DESCRIPTION)
    private String description;

    public Project(User owner, HashMap<Long, User> developers, HashMap<Long, Commit> commits, String name, String description) {
        this.owner = owner;
        this.developers = developers;
        this.commits = commits;
        this.name = name;
        this.description = description;
    }

    public Project(User owner, String name, String description) {
        this(owner, new HashMap<Long, User>(), new HashMap<Long, Commit>(), name, description);
    }

    public Project(String name, String description) {
        this(null, name, description);
    }

    public Project(String name) {
        this(name, "");
    }

    public Project() {
        this("");
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public HashMap<Long, User> getDevelopers() {
        return developers;
    }

    public HashMap<Long, Commit> getCommits() {
        return commits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addDeveloper(User user) {
        developers.put(user.getId(), user);
    }

    public void addDeveloper(User... user) {
        for (User aUser : user) {
            developers.put(aUser.getId(), aUser);
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

    public double getScore() {
        double score = 0;
//        for (Commit commit : commits) {
//            score += commit.getScore();
//        }
        return score;
    }

    @Override
    public boolean contentEquals(Object other) {
        return false;
    }

}
