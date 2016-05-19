package nl.devgames.model;

import java.util.Set;

public class Push extends Model {

    private String issueId;
    private Project project;
    private Set<Commit> commits;
    private Set<Issue> issues;
    private Set<Duplication> duplications;

    private long timestamp;
    private double score;

    public Push() {

    }

    public Push(String issueId, long timestamp) {
        this.issueId = issueId;
        this.timestamp = timestamp;
    }

    public Push(String issueId, long timestamp, double score) {
        this(issueId, timestamp);
        this.score = score;
    }

    @Deprecated
    public Push(Project project, Set<Commit> commits, Set<Issue> issues, Set<Duplication> duplications, long timestamp) {
        this.project = project;
        this.commits = commits;
        this.issues = issues;
        this.duplications = duplications;
        this.timestamp = timestamp;
    }

    public Push(String issueId, Project project, Set<Commit> commits, Set<Issue> issues, Set<Duplication> duplications, long timestamp, double score) {
        this(issueId, timestamp, score);
        this.project = project;
        this.commits = commits;
        this.issues = issues;
        this.duplications = duplications;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<Commit> getCommits() {
        return commits;
    }

    public void setCommits(Set<Commit> commits) {
        this.commits = commits;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public void setIssues(Set<Issue> issues) {
        this.issues = issues;
    }

    public Set<Duplication> getDuplications() {
        return duplications;
    }

    public void setDuplications(Set<Duplication> duplications) {
        this.duplications = duplications;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIssueId() {
        return issueId;

    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Push{" +
                "project=" + project +
                ", commits=" + commits +
                ", issues=" + issues +
                ", duplications=" + duplications +
                ", timestamp=" + timestamp +
                "} " + super.toString();
    }


}
