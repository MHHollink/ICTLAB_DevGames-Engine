package baecon.devgames.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "commits")
public class Commit {

    public static class Column {
        public static final String PROJECT = "project";
        public static final String COMMITTEE = "committee";
        public static final String TITLE = "title";
        public static final String HASH = "hash";
        public static final String BRANCH = "branch";
        public static final String FILES_CHANGED = "filesChanges";
        public static final String TIME = "timestamp";
    }

    @DatabaseField(columnName = Column.PROJECT)
    private Project project;

    @DatabaseField(columnName = Column.COMMITTEE)
    private User committee;

    @DatabaseField(columnName = Column.TITLE)
    private String title;

    @DatabaseField(columnName = Column.HASH)
    private String hash;

    @DatabaseField(columnName = Column.BRANCH)
    private String branch;

    @DatabaseField(columnName = Column.FILES_CHANGED)
    private int filesChanges;

    @DatabaseField(columnName = Column.TIME)
    private long timestamp;

    public Commit(Project project, User committee, String title, String hash, String branch, int filesChanges, long timestamp) {
        this.project = project;
        this.committee = committee;
        this.title = title;
        this.hash = hash;
        this.branch = branch;
        this.filesChanges = filesChanges;
        this.timestamp = timestamp;
    }

    public Commit() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getCommittee() {
        return committee;
    }

    public void setCommittee(User committee) {
        this.committee = committee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public int getFilesChanges() {
        return filesChanges;
    }

    public void setFilesChanges(int filesChanges) {
        this.filesChanges = filesChanges;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
