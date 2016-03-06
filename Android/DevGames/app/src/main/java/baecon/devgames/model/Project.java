package baecon.devgames.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "projects")
public class Project {

    public static class Column {
        public static final String OWNER = "owner";
        public static final String DEVELOPERS = "developers";
        public static final String COMMITS = "commits";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

    @DatabaseField(columnName = Column.OWNER)
    private User owner;

    @DatabaseField(columnName = Column.DEVELOPERS)
    private List<User> developers;

    @DatabaseField(columnName = Column.COMMITS)
    private List<Commit> commits;

    @DatabaseField(columnName = Column.NAME)
    private String name;

    @DatabaseField(columnName = Column.DESCRIPTION)
    private String description;

    public Project(User owner, List<User> developers, List<Commit> commits, String name, String description) {
        this.owner = owner;
        this.developers = developers;
        this.commits = commits;
        this.name = name;
        this.description = description;
    }

    public Project(User owner, String name, String description) {
        this(owner, new ArrayList<User>(), new ArrayList<Commit>(), name, description);
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

    public List<User> getDevelopers() {
        return developers;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public String getName() {
        return name;
    }

    public Project setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Project setDescription(String description) {
        this.description = description;
        return this;
    }

    public Project addDeveloper(User user) {
        developers.add(user);
        return this;
    }

    public Project addCommit(Commit commit) {
        commits.add(commit);
        return this;
    }
}
