package nl.devgames.model;

import java.util.Set;

public class Push extends Model {

    public enum Relations {
        CONTAINS_COMMIT,
        HAS_ISSUE,
        HAS_DUPLICATION,
        PUSHED_TO
    }


    private String key;
    private Project project;
    private Set<Commit> commits;
    private Set<Issue> issues;
    private Set<Duplication> duplications;

    private Long timestamp;
    private Double score;

    public Push() {

    }

    public Push(String key, long timestamp) {
        this.key = key;
        this.timestamp = timestamp;
    }

    public Push(String key, long timestamp, double score) {
        this(key, timestamp);
        this.score = score;
    }

    public Project getProject() {
        return project;
    }

    public Set<Commit> getCommits() {
        return commits;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public Set<Duplication> getDuplications() {
        return duplications;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getScore() {
        return score;
    }

    public String getKey() {
        return key;

    }

    public Push(String key, Project project, Set<Commit> commits, Set<Issue> issues, Set<Duplication> duplications, long timestamp, double score) {
        this(key, timestamp, score);
        this.project = project;
        this.commits = commits;
        this.issues = issues;
        this.duplications = duplications;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setCommits(Set<Commit> commits) {
        this.commits = commits;
    }

    public void setIssues(Set<Issue> issues) {
        this.issues = issues;
    }

    public void setDuplications(Set<Duplication> duplications) {
        this.duplications = duplications;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setKey(String key) {
        this.key = key;
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
