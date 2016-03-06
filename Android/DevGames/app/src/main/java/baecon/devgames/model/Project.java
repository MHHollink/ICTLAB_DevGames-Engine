package baecon.devgames.model;

import java.util.ArrayList;
import java.util.List;


public class Project {

    private User owner;
    private List<User> developers;
    private List<Commit> commits;
    private String name;
    private String description;
    private double score;

    public Project(User owner, List<User> developers, List<Commit> commits, String name, String description, double score) {
        this.owner = owner;
        this.developers = developers;
        this.commits = commits;
        this.name = name;
        this.description = description;
        this.score = score;
    }

    public Project(User owner, String name, String description, double score) {
        this(owner, new ArrayList<User>(), new ArrayList<Commit>(), name, description, score);
    }

    public Project(User owner, String name, String description) {
        this(owner, new ArrayList<User>(), new ArrayList<Commit>(), name, description, 0);
    }

    public Project(String name, String description) {
        this(null, name, description, 0);
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

    public double getScore() {
        return score;
    }

    public Project resetScore() {
        this.score = 0;
        return this;
    }

    public Project addScore(double score){
        this.score += score;
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
