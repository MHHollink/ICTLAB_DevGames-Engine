package nl.devgames.model;

import java.util.Set;

public class Push extends AbsModel{

    private Project project;
    private Set<Commit> commits;
    private Set<Issue> issues;
    private Set<Duplication> duplications;
    private long timestamp;

    public Push() {
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
