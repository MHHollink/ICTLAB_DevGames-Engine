package nl.devgames.model;


import java.util.Set;

public class User extends AbsModel {

    private String username;
    private String gitUsername;

    private String firstName;
    private String tween;
    private String lastName;

    private int age;
    private String mainJob;

    private Set<Project> projects;
    private Set<Commit> commits;

    public User() {
    }

    public User(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob) {
        this.username = username;
        this.gitUsername = gitUsername;
        this.firstName = firstName;
        this.tween = tween;
        this.lastName = lastName;
        this.age = age;
        this.mainJob = mainJob;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Commit> getCommits() {
        return commits;
    }

    public void setCommits(Set<Commit> commits) {
        this.commits = commits;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getTween() {
        return tween;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getMainJob() {
        return mainJob;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", firstName='" + firstName + '\'' +
                ", tween='" + tween + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", mainJob='" + mainJob + '\'' +
                ", projects=" + projects +
                ", commits=" + commits +
                '}';
    }
}
