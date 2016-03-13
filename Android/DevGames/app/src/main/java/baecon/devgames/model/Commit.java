package baecon.devgames.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "commits")
public class Commit extends AbsSynchronizable implements Serializable {

    public static class Column {
        public static final String PROJECT = "project";
        public static final String COMMITTEE = "committee";
        public static final String TITLE = "title";
        public static final String HASH = "hash";
        public static final String BRANCH = "branch";
        public static final String FILES_CHANGED = "filesChanges";
        public static final String TIME = "timestamp";
        public static final String SCORE = "score";
    }

    @DatabaseField(columnName = Column.PROJECT, dataType = DataType.SERIALIZABLE)
    private Project project;

    @DatabaseField(columnName = Column.COMMITTEE, dataType = DataType.SERIALIZABLE)
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

    @DatabaseField(columnName = Column.SCORE)
    private double score;

    public Commit(Project project, User committee, String title, String hash, String branch, int filesChanges, long timestamp, double score) {
        this.project = project;
        this.committee = committee;
        this.title = title;
        this.hash = hash;
        this.branch = branch;
        this.filesChanges = filesChanges;
        this.timestamp = timestamp;
        this.score = score;
    }

    public Commit() {
        this(null, null, null, null, null, 0, 0, 0);
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


    @Override
    public boolean contentEquals(Object other) {

        Commit o = (Commit) other;

        return id.equals(o.getId()) &&
                timestamp == o.getTimestamp() &&
                project.equals(o.getProject()) &&
                committee.equals(o.getCommittee()) &&
                title.equals(o.getTitle()) &&
                hash.equals(o.getHash()) &&
                score == o.getScore() &&
                branch.equals(o.getBranch()) &&
                filesChanges == o.getFilesChanges();

    }

}
