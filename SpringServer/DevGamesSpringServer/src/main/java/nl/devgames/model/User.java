package nl.devgames.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties({"password"})
public class User extends Model {

    private String username;
    private String gitUsername;

    private String firstName;
    private String tween;
    private String lastName;

    private int age;
    private String mainJob;

    private Set<Project> projects;
    private Set<Push> pushes;

    private String sessionId;
    private String gcmId;

    private String password;

    public User() {
    }

    public User(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob, Set<Project> projects, Set<Push> pushes, String sessionId, String gcmId) {
        this.username = username;
        this.gitUsername = gitUsername;
        this.firstName = firstName;
        this.tween = tween;
        this.lastName = lastName;
        this.age = age;
        this.mainJob = mainJob;
        this.projects = projects;
        this.pushes = pushes;
        this.sessionId = sessionId;
        this.gcmId = gcmId;
    }

    public User(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob, Set<Project> projects, Set<Push> pushes, String sessionId, String gcmId, String password) {
        this.username = username;
        this.gitUsername = gitUsername;
        this.firstName = firstName;
        this.tween = tween;
        this.lastName = lastName;
        this.age = age;
        this.mainJob = mainJob;
        this.projects = projects;
        this.pushes = pushes;
        this.sessionId = sessionId;
        this.gcmId = gcmId;
        this.password = password;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setTween(String tween) {
        this.tween = tween;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMainJob(String mainJob) {
        this.mainJob = mainJob;
    }

    public Set<Push> getPushes() {
        return pushes;
    }

    public void setPushes(Set<Push> pushes) {
        this.pushes = pushes;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
                ", pushes=" + pushes +
                ", sessionId='" + sessionId + '\'' +
                ", gcmId='" + gcmId + '\'' +
                "} " + super.toString();
    }

}
